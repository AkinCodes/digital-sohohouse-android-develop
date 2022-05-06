package com.sohohouse.seven.home.adapter.renderers

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.home.adapter.viewholders.BannerCarouselViewHolder
import com.sohohouse.seven.home.adapter.viewholders.BannerShortcut

class RoundelShortcutsRenderer(private val listener: ((BannerShortcut) -> Unit) = {}) :
    Renderer<BaseAdapterItem.BannerCarouselItem, BannerCarouselViewHolder> {

    override val type: Class<BaseAdapterItem.BannerCarouselItem> =
        BaseAdapterItem.BannerCarouselItem::class.java

    override fun createViewHolder(parent: ViewGroup): BannerCarouselViewHolder {
        return BannerCarouselViewHolder(createItemView(parent, R.layout.item_home_carousel))
    }

    override fun bindViewHolder(
        holder: BannerCarouselViewHolder,
        item: BaseAdapterItem.BannerCarouselItem
    ) {
        holder.bind(item.shortcuts, listener)
    }

}