package com.sohohouse.seven.more.bookings.recycler

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.MorePreviousBookingsDateBinding

class EventBookingsDateViewHolder(private val binding: MorePreviousBookingsDateBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(formattedDate: String) {
        binding.dateSubHeader.text = formattedDate
    }

}