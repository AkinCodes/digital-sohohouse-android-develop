package com.sohohouse.seven.housepay.checkdetail.render.renderer

import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.render.viewholder.SelectTipViewHolder

class SelectTipRenderer : Renderer<CheckItem.TipSelectItem, SelectTipViewHolder> {
    override val type: Class<CheckItem.TipSelectItem>
        get() = CheckItem.TipSelectItem::class.java

    override fun createViewHolder(parent: ViewGroup): SelectTipViewHolder {
        return SelectTipViewHolder.create(parent)
    }

    override fun bindViewHolder(holder: SelectTipViewHolder, item: CheckItem.TipSelectItem) {
        holder.bind(item.tips)
    }
}