package com.sohohouse.seven.book.adapter.renderer

import android.view.View
import ca.symbilityintersect.rendereradapter.BaseRenderer
import com.sohohouse.seven.R
import com.sohohouse.seven.book.adapter.model.FilterStateHeaderAdapterItem
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.common.viewholders.FilterStateViewHolder
import com.sohohouse.seven.databinding.FilterStateHeaderBinding

class FilterStateRenderer :
    BaseRenderer<FilterStateHeaderAdapterItem, FilterStateViewHolder>(FilterStateHeaderAdapterItem::class.java),
    Renderer<FilterStateHeaderAdapterItem, FilterStateViewHolder> {

    override fun createViewHolder(itemView: View) = FilterStateViewHolder(
        FilterStateHeaderBinding.bind(itemView)
    )

    override fun bindViewHolder(item: FilterStateHeaderAdapterItem, holder: FilterStateViewHolder) {
        holder.bind(item)
    }

    override fun getLayoutResId(): Int = R.layout.filter_state_header

    /**
     * Renderer<FilterStateHeaderAdapterItem, FilterStateViewHolder>
     */
    override val type: Class<FilterStateHeaderAdapterItem>
        get() = FilterStateHeaderAdapterItem::class.java

    override fun bindViewHolder(holder: FilterStateViewHolder, item: FilterStateHeaderAdapterItem) {
        holder.bind(item)
    }

}