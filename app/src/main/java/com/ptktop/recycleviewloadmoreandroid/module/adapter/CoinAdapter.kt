package com.ptktop.recycleviewloadmoreandroid.module.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ptktop.recycleviewloadmoreandroid.data.network.model.CoinDataListResponse
import com.ptktop.recycleviewloadmoreandroid.view.ItemEmptyViewGroup
import com.ptktop.recycleviewloadmoreandroid.view.ItemHeadViewGroup
import com.ptktop.recycleviewloadmoreandroid.view.ItemLoadingViewGroup
import com.ptktop.recycleviewloadmoreandroid.view.ItemSubViewGroup

class CoinAdapter(@JvmField private val listDao: ArrayList<CoinDataListResponse?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        @JvmField
        var listener: OnItemClickListener? = null
        var listClick: ArrayList<CoinDataListResponse?>? = null
    }

    private val viewEmpty = 0
    private val viewHead = 1
    private val viewSub = 2
    private val viewLoad = 3

    interface OnItemClickListener {
        fun onItemClick(itemView: View, position: Int)
    }

    fun setOnItemClickListener(listenerParam: OnItemClickListener) {
        listener = listenerParam
        listClick = listDao
    }

    override fun getItemViewType(position: Int): Int {
        return if (listDao.size == 0) {
            viewEmpty
        } else if (listDao[position] == null) {
            viewLoad
        } else {
            if ((position + 1) % 5 == 0) {
                viewSub
            } else {
                viewHead
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = when (viewType) {
            viewHead -> ItemHeadViewGroup(parent.context)
            viewSub -> ItemSubViewGroup(parent.context)
            viewLoad -> ItemLoadingViewGroup(parent.context)
            else -> ItemEmptyViewGroup(parent.context)
        }
        view.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dao = listDao[position]
        if (holder.itemViewType == viewHead) {
            if (dao != null) {
                ((holder as ViewHolder).itemView as ItemHeadViewGroup).setData(
                    if (dao.iconUrl != null) dao.iconUrl else "",
                    if (dao.name != null) dao.name else "-",
                    if (dao.description != null) dao.description else "-"
                )
            }
        } else if (holder.itemViewType == viewSub) {
            if (dao != null) {
                ((holder as ViewHolder).itemView as ItemSubViewGroup).setData(
                    if (dao.iconUrl != null) dao.iconUrl else "",
                    if (dao.name != null) dao.name else "-"
                )
            }
        } else if (holder.itemViewType == viewEmpty) {
            ((holder as ViewHolder).itemView as ItemEmptyViewGroup).setData("Empty")
        }
    }

    override fun getItemCount(): Int {
        return if (listDao.size > 0) listDao.size else 1
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            if (listClick!!.size > 0) {
                itemView.setOnClickListener {
                    if (listener != null)
                        listener!!.onItemClick(itemView, layoutPosition)
                }
            }
        }
    }

}