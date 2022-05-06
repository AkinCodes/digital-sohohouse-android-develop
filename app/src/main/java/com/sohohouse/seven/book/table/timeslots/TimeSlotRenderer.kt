package com.sohohouse.seven.book.table.timeslots

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.ItemTimeSlotBinding

class TimeSlotRenderer(private val itemClick: ((BookSlot) -> (Unit))) :
    Renderer<BookSlot, TimeSlotViewHolder> {

    override val type: Class<BookSlot>
        get() = BookSlot::class.java

    override fun createViewHolder(parent: ViewGroup): TimeSlotViewHolder {
        val binding = ItemTimeSlotBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeSlotViewHolder(binding, itemClick)
    }

    override fun bindViewHolder(holder: TimeSlotViewHolder, item: BookSlot) {
        holder.bind(item)
    }
}

