package com.ptktop.recycleviewloadmoreandroid.view

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener
import com.ptktop.recycleviewloadmoreandroid.R

class ItemSubViewGroup : FrameLayout {

    lateinit var imgView: AppCompatImageView
    lateinit var tvHead: AppCompatTextView

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.item_sub, this)
        imgView = findViewById(R.id.imgView)
        tvHead = findViewById(R.id.tvHead)
    }

    fun setData(url: String?, head: String?) {
        GlideToVectorYou
            .init()
            .with(context)
            .withListener(object : GlideToVectorYouListener {
                override fun onLoadFailed() {
                }

                override fun onResourceReady() {
                }
            })
            .setPlaceHolder(R.drawable.ic_preview, android.R.drawable.ic_menu_close_clear_cancel)
            .load(Uri.parse(url), imgView)
        this.tvHead.text = head
    }

}