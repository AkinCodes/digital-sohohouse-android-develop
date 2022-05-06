package com.sohohouse.seven.book.eventdetails.renderer

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.book.eventdetails.viewholders.AttributeWClickableTextViewHolder
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.common.views.eventdetaillist.EventAttributeAdapterItem
import com.sohohouse.seven.databinding.EventDetailsAttributeLayoutBinding

class AttributeWClickableTextRenderer<T : EventAttributeAdapterItem>(
    override val type: Class<T>
) : Renderer<T, AttributeWClickableTextViewHolder> {

    override fun createViewHolder(parent: ViewGroup): AttributeWClickableTextViewHolder {
        val binding = EventDetailsAttributeLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AttributeWClickableTextViewHolder(binding)
    }

    override fun bindViewHolder(holder: AttributeWClickableTextViewHolder, item: T) {
        holder.bind(item)
    }
}