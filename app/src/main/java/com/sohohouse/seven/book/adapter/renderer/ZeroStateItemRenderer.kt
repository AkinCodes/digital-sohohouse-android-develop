package com.sohohouse.seven.book.adapter.renderer

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.book.adapter.model.ZeroStateAdapterItem
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.common.viewholders.FilterZeroStateViewHolder

class ZeroStateItemRenderer : Renderer<ZeroStateAdapterItem, FilterZeroStateViewHolder> {

    override val type: Class<ZeroStateAdapterItem> = ZeroStateAdapterItem::class.java

    override fun createViewHolder(parent: ViewGroup): FilterZeroStateViewHolder {
        return FilterZeroStateViewHolder(createItemView(parent, R.layout.filter_zero_state_layout))
    }

    override fun bindViewHolder(holder: FilterZeroStateViewHolder, item: ZeroStateAdapterItem) {
        holder.bind(item)
    }

}