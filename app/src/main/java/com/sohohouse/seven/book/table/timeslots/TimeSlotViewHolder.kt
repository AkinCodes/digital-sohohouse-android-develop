package com.sohohouse.seven.book.table.timeslots

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.ItemTimeSlotBinding

class TimeSlotViewHolder(
    private val binding: ItemTimeSlotBinding, val listener: ((BookSlot) -> (Unit))
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(slot: BookSlot) {
        binding.root.setOnClickListener { listener(slot) }
        with(binding) {
            timeSlot.label = slot.text
            timeSlot.isActivated = slot.isSelected
        }
    }
}