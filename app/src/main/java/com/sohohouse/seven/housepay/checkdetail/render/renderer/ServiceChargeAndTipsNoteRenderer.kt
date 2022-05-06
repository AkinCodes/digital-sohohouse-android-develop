package com.sohohouse.seven.housepay.checkdetail.render.renderer

import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.render.viewholder.CheckLineNoteViewHolder

class ServiceChargeAndTipsNoteRenderer
    : Renderer<CheckItem.ServiceChargeAndTipNote, CheckLineNoteViewHolder> {
    override val type: Class<CheckItem.ServiceChargeAndTipNote>
        get() = CheckItem.ServiceChargeAndTipNote::class.java

    override fun createViewHolder(parent: ViewGroup): CheckLineNoteViewHolder {
        return CheckLineNoteViewHolder.create(parent)
    }

    override fun bindViewHolder(
        holder: CheckLineNoteViewHolder,
        item: CheckItem.ServiceChargeAndTipNote
    ) {
        holder.bind(item.text)
    }


}