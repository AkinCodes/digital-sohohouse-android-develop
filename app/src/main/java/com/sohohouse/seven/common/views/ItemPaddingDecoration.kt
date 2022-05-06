package com.sohohouse.seven.common.views

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.State

class ItemPaddingDecoration(
    @RecyclerView.Orientation private val orientation: Int,
    private val itemPadding: Int
) : RecyclerView.ItemDecoration() {

    var skipFirst = false

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
        if (state.itemCount == 0) return

        val adapterPosition = parent.getChildAdapterPosition(view)

        if (adapterPosition == 0 || (skipFirst && adapterPosition == 1)) {
            if (orientation == RecyclerView.HORIZONTAL) {
                outRect.left = 0
            } else {
                outRect.top = 0
            }
            return
        }

        if (adapterPosition < state.itemCount) {
            if (orientation == RecyclerView.HORIZONTAL) {
                outRect.left = itemPadding
            } else {
                outRect.top = itemPadding
            }
        }
    }
}
