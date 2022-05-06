package com.sohohouse.seven.housepay.checkdetail.render.renderer

import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.render.viewholder.NotificationBannerViewHolder

class NotificationBannerRenderer
    : Renderer<CheckItem.NotificationBanner, NotificationBannerViewHolder> {
    override val type: Class<CheckItem.NotificationBanner>
        get() = CheckItem.NotificationBanner::class.java

    override fun createViewHolder(parent: ViewGroup): NotificationBannerViewHolder {
        return NotificationBannerViewHolder.create(parent)
    }

    override fun bindViewHolder(
        holder: NotificationBannerViewHolder,
        item: CheckItem.NotificationBanner
    ) {
        holder.bind(item.alert)
    }

}