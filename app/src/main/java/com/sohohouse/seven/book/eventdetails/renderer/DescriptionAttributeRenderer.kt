package com.sohohouse.seven.book.eventdetails.renderer

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.book.eventdetails.viewholders.DescriptionAttributeViewHolder
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.common.views.eventdetaillist.EventDescriptionAdapterItem
import com.sohohouse.seven.databinding.EventDetailsDescriptionLayoutBinding

class DescriptionAttributeRenderer<T : EventDescriptionAdapterItem>(override val type: Class<T>) :
    Renderer<T, DescriptionAttributeViewHolder> {
    override fun createViewHolder(parent: ViewGroup): DescriptionAttributeViewHolder {
        val binding = EventDetailsDescriptionLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DescriptionAttributeViewHolder(
            binding
        )
    }

    override fun bindViewHolder(holder: DescriptionAttributeViewHolder, item: T) {
        holder.bind(item)
    }
}