package com.sohohouse.seven.book.eventdetails.renderer

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.book.eventdetails.bookingsuccess.EventGuestListAdapterItem
import com.sohohouse.seven.book.eventdetails.viewholders.GuestRecyclerviewViewHolder
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.EventDetailsGuestListLayoutBinding

class GuestRecyclerviewRenderer : Renderer<EventGuestListAdapterItem, GuestRecyclerviewViewHolder> {

    override val type: Class<EventGuestListAdapterItem> = EventGuestListAdapterItem::class.java

    override fun createViewHolder(parent: ViewGroup): GuestRecyclerviewViewHolder {
        val binding = EventDetailsGuestListLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GuestRecyclerviewViewHolder(binding)
    }

    override fun bindViewHolder(
        holder: GuestRecyclerviewViewHolder,
        item: EventGuestListAdapterItem
    ) {
        holder.bind(item)
    }
}