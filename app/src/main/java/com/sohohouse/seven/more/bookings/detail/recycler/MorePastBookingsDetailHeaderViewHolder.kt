package com.sohohouse.seven.more.bookings.detail.recycler

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.MorePastBookingsDetailHeaderBinding

class MorePastBookingsDetailHeaderViewHolder(private val binding: MorePastBookingsDetailHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(headerString: String) {
        binding.header.text = headerString
    }
}