package com.sohohouse.seven.housepay.checkdetail.render.renderer

import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.render.viewholder.OrderTotalViewHolder

class OrderTotalRenderer : Renderer<CheckItem.OrderTotal, OrderTotalViewHolder> {
    override val type: Class<CheckItem.OrderTotal>
        get() = CheckItem.OrderTotal::class.java

    override fun createViewHolder(parent: ViewGroup): OrderTotalViewHolder {
        return OrderTotalViewHolder.create(parent)
    }

    override fun bindViewHolder(holder: OrderTotalViewHolder, item: CheckItem.OrderTotal) {
        holder.bind(item.item)
    }
}