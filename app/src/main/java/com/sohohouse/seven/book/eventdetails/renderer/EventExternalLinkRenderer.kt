package com.sohohouse.seven.book.eventdetails.renderer

import android.view.ViewGroup
import com.sohohouse.seven.book.eventdetails.model.EventExternalLinkAdapterItem
import com.sohohouse.seven.book.eventdetails.viewholders.EVENT_EXTERNAL_LINK_LAYOUT
import com.sohohouse.seven.book.eventdetails.viewholders.EventExternalLinkViewHolder
import com.sohohouse.seven.common.design.adapter.Renderer

class EventExternalLinkRenderer(
    private val onItemClicked: (url: String) -> Unit
) : Renderer<EventExternalLinkAdapterItem, EventExternalLinkViewHolder> {

    override val type: Class<EventExternalLinkAdapterItem> =
        EventExternalLinkAdapterItem::class.java

    override fun createViewHolder(parent: ViewGroup): EventExternalLinkViewHolder {
        return EventExternalLinkViewHolder(createItemView(parent, EVENT_EXTERNAL_LINK_LAYOUT))
    }

    override fun bindViewHolder(
        holder: EventExternalLinkViewHolder,
        item: EventExternalLinkAdapterItem
    ) {
        holder.bind(item, onItemClicked)
    }
}