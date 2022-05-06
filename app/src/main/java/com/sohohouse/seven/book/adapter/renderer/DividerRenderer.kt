package com.sohohouse.seven.book.adapter.renderer

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.book.adapter.model.DividerBookAdapterItem
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.common.viewholders.ListSectionHeaderViewHolder

class DividerRenderer : Renderer<DividerBookAdapterItem, ListSectionHeaderViewHolder> {

    override val type: Class<DividerBookAdapterItem> = DividerBookAdapterItem::class.java

    override fun createViewHolder(parent: ViewGroup): ListSectionHeaderViewHolder {
        return ListSectionHeaderViewHolder(createItemView(parent, R.layout.text_block_small_header))
    }

    override fun bindViewHolder(holder: ListSectionHeaderViewHolder, item: DividerBookAdapterItem) {
        holder.bind(item)
    }
}