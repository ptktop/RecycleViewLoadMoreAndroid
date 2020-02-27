package com.ptktop.recycleviewloadmoreandroid.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.ptktop.recycleviewloadmoreandroid.R

class ItemEmptyViewGroup: FrameLayout {

    lateinit var textEmpty: TextView

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.item_empty, this)
        textEmpty = findViewById(R.id.tvEmpty)
    }

    fun setData(text: String) {
        this.textEmpty.text = text
    }

}