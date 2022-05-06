package com.sohohouse.seven.housepay.checkdetail.render.renderer

import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.render.viewholder.CheckCalculationSectionItemViewHolder

class DiscountItemRenderer : Renderer<CheckItem.Discount, CheckCalculationSectionItemViewHolder> {
    override val type: Class<CheckItem.Discount>
        get() = CheckItem.Discount::class.java

    override fun createViewHolder(parent: ViewGroup): CheckCalculationSectionItemViewHolder {
        return CheckCalculationSectionItemViewHolder.create(parent)
    }

    override fun bindViewHolder(
        holder: CheckCalculationSectionItemViewHolder,
        item: CheckItem.Discount
    ) {
        holder.bind(item.item)
    }
}