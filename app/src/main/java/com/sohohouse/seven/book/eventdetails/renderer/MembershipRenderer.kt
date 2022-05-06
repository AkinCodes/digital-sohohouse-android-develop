package com.sohohouse.seven.book.eventdetails.renderer

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.book.eventdetails.viewholders.MembershipViewHolder
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.common.views.eventdetaillist.EventMembershipAdapterItem
import com.sohohouse.seven.databinding.EventDetailsIconAttributeNameLayoutBinding

class MembershipRenderer : Renderer<EventMembershipAdapterItem, MembershipViewHolder> {

    override val type: Class<EventMembershipAdapterItem> = EventMembershipAdapterItem::class.java

    override fun createViewHolder(parent: ViewGroup): MembershipViewHolder {
        val binding = EventDetailsIconAttributeNameLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MembershipViewHolder(binding)
    }

    override fun bindViewHolder(holder: MembershipViewHolder, item: EventMembershipAdapterItem) {
        holder.bind(item)
    }

}