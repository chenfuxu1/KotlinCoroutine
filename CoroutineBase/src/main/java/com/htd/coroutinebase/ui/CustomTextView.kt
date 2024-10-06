package com.htd.coroutinebase.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.htd.coroutinebase.R


/**
 * Project: CoroutineBase
 * Create By: ChenFuXu
 * DateTime: 2024-10-02 11:37
 *
 **/
class CustomTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    override fun onFinishInflate() {
        super.onFinishInflate()
        background = context.getDrawable(R.drawable.text_view_shape)
        textSize = 20f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}