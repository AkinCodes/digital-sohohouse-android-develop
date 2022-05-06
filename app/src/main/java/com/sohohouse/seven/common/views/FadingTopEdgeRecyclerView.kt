package com.sohohouse.seven.common.views

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet

class FadingTopEdgeRecyclerView : RecyclerView {
    companion object {
        private const val FLING_FACTOR = 5
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun getBottomFadingEdgeStrength(): Float {
        return 0f
    }

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        return super.fling(velocityX, velocityY / FLING_FACTOR)
    }
}
