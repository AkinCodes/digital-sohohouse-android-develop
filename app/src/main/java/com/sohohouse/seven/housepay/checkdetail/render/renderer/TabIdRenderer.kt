package com.sohohouse.seven.housepay.checkdetail.render.renderer

import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.render.viewholder.CheckMetaInfoViewHolder

class TabIdRenderer : Renderer<CheckItem.TabId, CheckMetaInfoViewHolder> {
    override val type: Class<CheckItem.TabId>
        get() = CheckItem.TabId::class.java

    override fun createViewHolder(parent: ViewGroup): CheckMetaInfoViewHolder {
        return CheckMetaInfoViewHolder.create(parent)
    }

    override fun bindViewHolder(holder: CheckMetaInfoViewHolder, item: CheckItem.TabId) {
        holder.bind(item.value)
    }

}