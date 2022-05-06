package com.sohohouse.seven.common.design.list

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ListItemPaddingDecoration(
    @RecyclerView.Orientation private val orientation: Int = RecyclerView.VERTICAL,
    private val verticalSpacing: Int = 0,
    private val horizontalSpacing: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val adapterPosition = parent.getChildAdapterPosition(view)
        val isLastItem = adapterPosition == (state.itemCount - 1)
        when (orientation) {
            RecyclerView.HORIZONTAL -> {
                outRect.left = horizontalSpacing
                outRect.top = verticalSpacing
                outRect.right = if (isLastItem) horizontalSpacing else 0
                outRect.bottom = verticalSpacing
            }
            RecyclerView.VERTICAL -> {
                outRect.left = horizontalSpacing
                outRect.top = verticalSpacing
                outRect.right = horizontalSpacing
                outRect.bottom = if (isLastItem) verticalSpacing else 0
            }
        }
    }
}