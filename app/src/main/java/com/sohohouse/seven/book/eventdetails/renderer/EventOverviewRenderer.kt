package com.sohohouse.seven.book.eventdetails.renderer

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.book.eventdetails.EventOverviewAdapterItem
import com.sohohouse.seven.book.eventdetails.viewholders.EventOverviewViewHolder
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.EventDetailsOverviewLayoutBinding

class EventOverviewRenderer(
    private val onButtonClicked: (isWaitList: Boolean, isLottery: Boolean, isTicketless: Boolean) -> Unit
) : Renderer<EventOverviewAdapterItem, EventOverviewViewHolder> {

    override val type: Class<EventOverviewAdapterItem> = EventOverviewAdapterItem::class.java

    override fun createViewHolder(parent: ViewGroup): EventOverviewViewHolder {
        val binding = EventDetailsOverviewLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EventOverviewViewHolder(binding)
    }

    override fun bindViewHolder(holder: EventOverviewViewHolder, item: EventOverviewAdapterItem) {
        holder.bind(item, onButtonClicked)
    }

}