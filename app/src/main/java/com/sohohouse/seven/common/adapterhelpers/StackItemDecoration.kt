package com.sohohouse.seven.common.adapterhelpers

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.views.ExpandableListView
import com.sohohouse.seven.common.views.StickyHeaderDecoration
import com.sohohouse.seven.home.houseboard.viewholders.NotificationViewHolder
import kotlin.math.max

class StackItemDecoration<T : RecyclerView.ViewHolder>(
    adapter: StickyHeaderAdapter<T>,
    private val stackOffsetFactor: Float
) : StickyHeaderDecoration<T>(adapter), StickyHeaderTouchHelper, ExpandableListView.Listener {

    private var expanded: Boolean = false

    var stackPosition: Int = 0

    /**
     * ExpandableListView.Listener
     */
    override fun onExpandableListChanged(expanded: Boolean) {
        this.expanded = expanded
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        var headerHeight = 0

        if (position != RecyclerView.NO_POSITION && hasHeader(position) && showHeaderAboveItem(
                position
            )
        ) {
            val header = getHeader(parent, position).itemView
            headerHeight = header.measuredHeight
        }

        if (parent.findContainingViewHolder(view) is NotificationViewHolder) {
            setElevation(view, position)
            measureSize(view, parent)

            val scale = if (expanded) 1f else 1f - stackOffsetFactor * (position - stackPosition)
            view.scaleX = scale
            view.scaleY = scale

            val top = when (position) {
                stackPosition -> if (expanded) headerHeight else 0
                else -> if (expanded) 0 else headerHeight - view.measuredHeight
            }
            outRect.set(0, top, 0, 0)
        }
    }

    private fun measureSize(view: View, parent: RecyclerView) {
        if (view.measuredHeight == 0) {
            val widthSpec = View.MeasureSpec.makeMeasureSpec(
                parent.measuredWidth,
                View.MeasureSpec.EXACTLY
            )
            val heightSpec = View.MeasureSpec.makeMeasureSpec(
                parent.measuredHeight,
                View.MeasureSpec.UNSPECIFIED
            )

            val childWidth = ViewGroup.getChildMeasureSpec(
                widthSpec,
                parent.paddingLeft + parent.paddingRight,
                view.layoutParams.width
            )
            val childHeight = ViewGroup.getChildMeasureSpec(
                heightSpec,
                parent.paddingTop + parent.paddingBottom,
                view.layoutParams.height
            )

            view.measure(childWidth, childHeight)
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        }
    }

    private fun setElevation(view: View, position: Int) {
        view.elevation = max(3f - 1f * position, 0f)
    }

    /**
     * StickyHeaderTouchHelper
     */
    override fun findHeaderViewUnder(x: Float, y: Float): View? {
        if (!expanded) return null

        for (holder in headerCache.values) {
            if (isInViewBoundary(holder.itemView, x, y)) {
                return holder.itemView
            }
        }
        return null
    }

    override fun onSingleTap(event: MotionEvent): Boolean {
        val view = findHeaderViewUnder(event.x, event.y) ?: return false
        handleTouch(view, event.x - view.translationX, event.y - view.translationY)
        view.onTouchEvent(event)
        return true
    }

    private fun handleTouch(view: View, x: Float, y: Float) {
        if (view.isClickable) {
            view.performClick()
            return
        }

        if (view is ViewGroup) {
            for (pos in 0 until view.childCount) {
                view.getChildAt(pos)?.let { child ->
                    if (isInViewBoundary(child, x, y)) {
                        handleTouch(child, x, y)
                        return
                    }
                }
            }
        }
    }

    private fun isInViewBoundary(view: View, x: Float, y: Float): Boolean {
        val translationX = view.translationX
        val translationY = view.translationY
        return x >= view.left + translationX &&
                x <= view.right + translationX &&
                y >= view.top + translationY &&
                y <= view.bottom + translationY
    }
}
