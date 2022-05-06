package com.sohohouse.seven.housepay.checkdetail.render.renderer

import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.render.viewholder.RevenueCenterViewHolder

class ReveneCenterRenderer : Renderer<CheckItem.RevenueCenter, RevenueCenterViewHolder> {
    override val type: Class<CheckItem.RevenueCenter>
        get() = CheckItem.RevenueCenter::class.java

    override fun createViewHolder(parent: ViewGroup): RevenueCenterViewHolder {
        return RevenueCenterViewHolder.create(parent)
    }

    override fun bindViewHolder(holder: RevenueCenterViewHolder, item: CheckItem.RevenueCenter) {
        holder.bind(item.name)
    }

}