package com.sohohouse.seven.common.views

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet

class SquareImageView @JvmOverloads constructor(
    con: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(con, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}
