package com.ptktop.recycleviewloadmoreandroid.module.main

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.ptktop.recycleviewloadmoreandroid.R
import com.ptktop.recycleviewloadmoreandroid.data.network.ServiceCreateCall
import com.ptktop.recycleviewloadmoreandroid.data.network.model.CoinDataListResponse
import com.ptktop.recycleviewloadmoreandroid.data.network.model.CoinResponse
import com.ptktop.recycleviewloadmoreandroid.module.adapter.CoinAdapter
import com.ptktop.recycleviewloadmoreandroid.utils.DividerItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var progressLoading: ProgressBar
    private lateinit var edtSearch: TextInputEditText
    private lateinit var swRefresh: SwipeRefreshLayout
    private lateinit var rvView: RecyclerView
    private lateinit var floatAction: FloatingActionButton

    private lateinit var itemDecoration: DividerItemDecoration
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: CoinAdapter? = null

    private var onLoading = MutableLiveData<Boolean>()
    private var coinResponse = MutableLiveData<CoinResponse>()

    private var subscription: Disposable? = null

    private var limitSize = 10
    private var isLoadingMore = false
    private var canLoadingMore = true
    private var listCoin = ArrayList<CoinDataListResponse?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init(savedInstanceState)
        initInstances()
    }

    private fun init(@Suppress("UNUSED_PARAMETER") savedInstanceState: Bundle?) {
        itemDecoration = DividerItemDecoration(this)
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager!!.isSmoothScrollbarEnabled = true
    }

    private fun initInstances() {
        progressLoading = findViewById(R.id.progressLoading)
        edtSearch = findViewById(R.id.edtSearch)
        swRefresh = findViewById(R.id.swRefresh)
        rvView = findViewById(R.id.rvView)
        floatAction = findViewById(R.id.floatAction)
        setupView()
        setupObserve()
        callApiMain()
    }

    private fun setupView() {
        edtSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard(v)
                clearRecycleView()
                callApiMain()
                true
            } else {
                false
            }
        }
        swRefresh.setOnRefreshListener { prepareRefresh() }
        floatAction.setOnClickListener(this)
    }

    private fun setupObserve() {
        onLoading.observe(this, androidx.lifecycle.Observer { isLoad -> loading(isLoad) })
        coinResponse.observe(
            this,
            androidx.lifecycle.Observer { dao -> setDataRecycleView(dao.coinData.listCoin) })
    }

    private fun prepareRefresh() {
        edtSearch.setText("")
        clearRecycleView()
        callApiMain()
    }

    private fun clearRecycleView() {
        if (adapter != null) {
            limitSize = 10
            isLoadingMore = false
            canLoadingMore = true
            listCoin.clear()
            rvView.removeItemDecoration(itemDecoration)
            rvView.adapter = null
            adapter = null
        }
    }

    private fun setDataRecycleView(listData: ArrayList<CoinDataListResponse?>) {
        if (adapter == null) {
            listCoin.addAll(listData)
            swRefresh.isRefreshing = false
            when ((listData.size % 10)) {
                0 -> listCoin.add(null)
                else -> canLoadingMore = false
            }
            adapter = CoinAdapter(listCoin)
            rvView.layoutManager = linearLayoutManager
            rvView.addItemDecoration(itemDecoration)
            rvView.adapter = adapter
            rvView.setHasFixedSize(true)
            adapter!!.setOnItemClickListener(object : CoinAdapter.OnItemClickListener {
                override fun onItemClick(itemView: View, position: Int) {
                    if (listData.size > 0) {
                        val dao = listData[position]
                        if (dao != null) showToast(dao.name!!)
                    }
                }
            })
            rvView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val firstVisiblePosition = linearLayoutManager!!.findFirstVisibleItemPosition()
                    val visibleItemCount = linearLayoutManager!!.childCount
                    val totalItemCount = linearLayoutManager!!.itemCount
                    if (firstVisiblePosition + visibleItemCount >= totalItemCount) {
                        if (listData.size > 0 && canLoadingMore && limitSize < 100) {
                            loadMoreDetail()
                        }
                    }
                }
            })
        } else if (isLoadingMore) {
            isLoadingMore = false
            listCoin.removeAt(listCoin.size - 1)
            adapter?.notifyItemRemoved(listCoin.size)
            addDataAfterLoadMore(listData)
            swRefresh.isRefreshing = false
            if ((listData.size % 10) == 0 && limitSize < 100) listCoin.add(null)
            else canLoadingMore = false
            adapter?.notifyItemRangeInserted(adapter?.itemCount!!, listCoin.size - 1)
        }
    }

    private fun addDataAfterLoadMore(listData: ArrayList<CoinDataListResponse?>) {
        if (listData.size > listCoin.size) {
            for (i in listCoin.size until listData.size) {
                listCoin.add(listData[i])
            }
        }
    }

    private fun loadMoreDetail() {
        if (isLoadingMore) return
        isLoadingMore = true
        limitSize += 10
        callApiMain()
    }

    //-------------------------------------- API ---------------------------------------------------
    private fun callApiMain() {
        val text = edtSearch.text.toString().trim()
        if (text == "") callCoinApiService() else callCoinRankingSearch(text)
    }

    private fun callCoinApiService() {
        subscription = ServiceCreateCall.getInstance().getCoinApiService().coinRanking(limitSize)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                loading(true)
            }
            .doAfterTerminate {
                loading(false)
            }
            .subscribe(
                {
                    when (it.status) {
                        "success" -> coinResponse.value = it
                        else -> showToast(it.status)
                    }
                },
                { showToast(it.message!!) }
            )
    }

    private fun callCoinRankingSearch(text: String) {
        subscription =
            ServiceCreateCall.getInstance().getCoinApiService()
                .coinRankingSearch(text, text, text, limitSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    loading(true)
                }
                .doAfterTerminate {
                    loading(false)
                }
                .subscribe(
                    {
                        when (it.status) {
                            "success" -> coinResponse.value = it
                            else -> showToast(it.status)
                        }
                    },
                    { showToast(it.message!!) }
                )
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun loading(isLoad: Boolean) {
        if (isLoad) {
            progressLoading.visibility = View.VISIBLE
        } else {
            progressLoading.visibility = View.GONE
            swRefresh.isRefreshing = false
        }
    }

    private fun showToast(str: String) {
        Toast.makeText(this@MainActivity, str, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        if (v == floatAction) {
            rvView.smoothScrollToPosition(0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription?.dispose()
    }
}
