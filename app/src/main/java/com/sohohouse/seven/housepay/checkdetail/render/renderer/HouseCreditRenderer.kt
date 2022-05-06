package com.sohohouse.seven.housepay.checkdetail.render.renderer

import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.render.viewholder.CheckAlertViewHolder

class HouseCreditRenderer : Renderer<CheckItem.HouseCreditAlert, CheckAlertViewHolder> {
    override val type: Class<CheckItem.HouseCreditAlert>
        get() = CheckItem.HouseCreditAlert::class.java

    override fun createViewHolder(parent: ViewGroup) = CheckAlertViewHolder.create(parent)

    override fun bindViewHolder(holder: CheckAlertViewHolder, item: CheckItem.HouseCreditAlert) {
        holder.bind(item.info)
    }
}