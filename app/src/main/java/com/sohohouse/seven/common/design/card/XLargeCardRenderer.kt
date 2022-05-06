package com.sohohouse.seven.common.design.card

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.LocalHouseHouseNoteContentLayoutBinding

class XLargeCardRenderer<T : XLargeCard>(
    override val type: Class<T>,
    private val onItemClicked: ((item: T, position: Int) -> Unit)
) : Renderer<T, XLargeCardViewHolder<T>> {

    override fun createViewHolder(parent: ViewGroup): XLargeCardViewHolder<T> {
        return XLargeCardViewHolder(
            LocalHouseHouseNoteContentLayoutBinding.bind(
                createItemView(parent, R.layout.local_house_house_note_content_layout)
            )
        )
    }

    override fun bindViewHolder(holder: XLargeCardViewHolder<T>, item: T) {
        holder.bind(item, onItemClicked)
    }
}