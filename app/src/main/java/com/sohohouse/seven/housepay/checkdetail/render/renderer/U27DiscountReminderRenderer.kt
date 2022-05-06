package com.sohohouse.seven.housepay.checkdetail.render.renderer

import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.render.viewholder.CheckInfoBannerViewHolder

class U27DiscountReminderRenderer() :
    Renderer<CheckItem.U27DiscountReminder, CheckInfoBannerViewHolder> {
    override val type: Class<CheckItem.U27DiscountReminder>
        get() = CheckItem.U27DiscountReminder::class.java

    override fun createViewHolder(parent: ViewGroup): CheckInfoBannerViewHolder {
        return CheckInfoBannerViewHolder.create(parent)
    }

    override fun bindViewHolder(
        holder: CheckInfoBannerViewHolder,
        item: CheckItem.U27DiscountReminder
    ) {
        holder.bind(item.item)
    }
}