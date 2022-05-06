package com.sohohouse.seven.more.bookings.recycler

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.ItemBookingsHeaderTextBinding

class BookingsHeaderTextViewHolder(val binding: ItemBookingsHeaderTextBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: BookingsHeaderTextItem) {
        binding.headerText.text = item.text
    }

}