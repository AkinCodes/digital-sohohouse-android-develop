package com.sohohouse.seven.book.eventdetails.bookingsuccess

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.BookingSuccessDescriptionBinding

class BookingSuccessDescriptionViewHolder(
    private val binding: BookingSuccessDescriptionBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: EventBookingSuccessDescriptionItem) = with(binding) {
        label.isVisible = item.label.isNotEmpty()
        label.text = item.label
        description.text = item.description
    }
}
