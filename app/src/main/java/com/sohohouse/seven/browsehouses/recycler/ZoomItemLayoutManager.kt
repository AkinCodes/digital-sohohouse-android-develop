package com.sohohouse.seven.browsehouses.recycler

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.sohohouse.seven.R


class ZoomItemLayoutManager(
    context: Context,
    private var topPadding: Float,
    private val childSizeListener: (measuredHeight: Int) -> Unit
) : LinearLayoutManager(context) {

    // shrink from 22sp to 18sp means shrink to x%
    private val mShrinkAmount = 1 / (1 - 18 / 22f)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
        scrollVerticallyBy(0, recycler, state)
    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)

        val child = getChildAt(1)
        childSizeListener(child?.height ?: 0)

    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        if (orientation == VERTICAL) {
            val scrolled = super.scrollVerticallyBy(dy, recycler, state)

            for (i in 0 until childCount) {
                val child = getChildAt(i)
                child?.let {
                    val scrolledAmount = Math.abs(getDecoratedTop(it) - topPadding)
                    val scalableHeight = it.height.toFloat()
                    val scrolledPercentage = Math.min(scrolledAmount, scalableHeight)
                    val scale = 1 - scrolledPercentage / scalableHeight / mShrinkAmount

                    if (child.id == R.id.house_name_container) {
                        val houseName = child.findViewById<TextView>(R.id.browse_houses_name)

                        houseName.scaleX = scale
                        houseName.scaleY = scale
                    }
                }
            }
            return scrolled
        } else {
            return 0
        }
    }

}
