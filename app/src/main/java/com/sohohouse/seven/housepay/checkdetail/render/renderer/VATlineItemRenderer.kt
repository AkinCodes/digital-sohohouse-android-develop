package com.sohohouse.seven.housepay.checkdetail.render.renderer

import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.render.viewholder.CheckCalculationSectionItemViewHolder

class VATlineItemRenderer : Renderer<CheckItem.VATlineItem, CheckCalculationSectionItemViewHolder> {
    override val type: Class<CheckItem.VATlineItem>
        get() = CheckItem.VATlineItem::class.java

    override fun createViewHolder(parent: ViewGroup): CheckCalculationSectionItemViewHolder {
        return CheckCalculationSectionItemViewHolder.create(parent)
    }

    override fun bindViewHolder(
        holder: CheckCalculationSectionItemViewHolder,
        item: CheckItem.VATlineItem
    ) {
        holder.bind(item.item)
    }
}