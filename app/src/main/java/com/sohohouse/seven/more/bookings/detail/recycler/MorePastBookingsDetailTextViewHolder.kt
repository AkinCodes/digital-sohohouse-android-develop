package com.sohohouse.seven.more.bookings.detail.recycler

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.MorePastBookingsDetailTextBinding

class MorePastBookingsDetailTextViewHolder(private val binding: MorePastBookingsDetailTextBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(textString: String) {
        binding.text.text = textString
    }
}