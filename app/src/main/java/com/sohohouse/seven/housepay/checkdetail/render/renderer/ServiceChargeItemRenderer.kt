package com.sohohouse.seven.housepay.checkdetail.render.renderer

import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.render.viewholder.CheckCalculationSectionItemViewHolder

class ServiceChargeItemRenderer :
    Renderer<CheckItem.ServiceCharge, CheckCalculationSectionItemViewHolder> {
    override val type: Class<CheckItem.ServiceCharge>
        get() = CheckItem.ServiceCharge::class.java

    override fun createViewHolder(parent: ViewGroup): CheckCalculationSectionItemViewHolder {
        return CheckCalculationSectionItemViewHolder.create(parent)
    }

    override fun bindViewHolder(
        holder: CheckCalculationSectionItemViewHolder,
        item: CheckItem.ServiceCharge
    ) {
        holder.bind(item.item)
    }
}