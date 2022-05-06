package com.sohohouse.seven.housepay.checkdetail.render.renderer

import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.render.viewholder.LineItemViewHolder

class LineItemRenderer : Renderer<CheckItem.LineItem, LineItemViewHolder> {
    override val type: Class<CheckItem.LineItem>
        get() = CheckItem.LineItem::class.java

    override fun createViewHolder(parent: ViewGroup): LineItemViewHolder {
        return LineItemViewHolder.create(parent)
    }

    override fun bindViewHolder(holder: LineItemViewHolder, item: CheckItem.LineItem) {
        holder.bind(item.lineItemDTO)
    }
}