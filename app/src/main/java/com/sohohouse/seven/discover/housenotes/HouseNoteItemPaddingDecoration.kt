package com.sohohouse.seven.discover.housenotes

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.design.card.XLargeCardViewHolder

class HouseNoteItemPaddingDecoration(
    private val horizontalPadding: Int,
    private val verticalPadding: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        if (state.itemCount == 0) return
        if (parent.getChildViewHolder(view) is XLargeCardViewHolder<*>) return

        outRect.top = verticalPadding
        outRect.left = horizontalPadding
        outRect.right = horizontalPadding
    }

}