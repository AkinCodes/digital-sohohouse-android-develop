package com.sohohouse.seven.book.eventdetails.renderer

import android.view.ViewGroup
import com.sohohouse.seven.book.eventdetails.EventDetailsSubDescriptionAdapterItem
import com.sohohouse.seven.book.eventdetails.viewholders.SUB_DESCRIPTION_LAYOUT
import com.sohohouse.seven.book.eventdetails.viewholders.SubDescriptionAttributeViewHolder
import com.sohohouse.seven.common.design.adapter.Renderer

class SubDescriptionAttributeRenderer :
    Renderer<EventDetailsSubDescriptionAdapterItem, SubDescriptionAttributeViewHolder> {

    override val type: Class<EventDetailsSubDescriptionAdapterItem> =
        EventDetailsSubDescriptionAdapterItem::class.java

    override fun createViewHolder(parent: ViewGroup): SubDescriptionAttributeViewHolder {
        return SubDescriptionAttributeViewHolder(createItemView(parent, SUB_DESCRIPTION_LAYOUT))
    }

    override fun bindViewHolder(
        holder: SubDescriptionAttributeViewHolder,
        item: EventDetailsSubDescriptionAdapterItem
    ) {
        holder.bind(item.attrStringRes, item.value, item.isLastItem)
    }
}