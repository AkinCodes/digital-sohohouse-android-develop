package com.sohohouse.seven.housepay.checkdetail.render.renderer

import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.render.viewholder.CheckCalculationSectionItemViewHolder

class SubtotalDueRenderer :
    Renderer<CheckItem.TotalsSectionLineItem, CheckCalculationSectionItemViewHolder> {
    override val type: Class<CheckItem.TotalsSectionLineItem>
        get() = CheckItem.TotalsSectionLineItem::class.java

    override fun createViewHolder(parent: ViewGroup): CheckCalculationSectionItemViewHolder {
        return CheckCalculationSectionItemViewHolder.create(parent)
    }

    override fun bindViewHolder(
        holder: CheckCalculationSectionItemViewHolder,
        item: CheckItem.TotalsSectionLineItem
    ) {
        holder.bind(item.item)
    }
}