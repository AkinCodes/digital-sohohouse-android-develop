package com.sohohouse.seven.more.bookings.recycler

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.MorePreviousEmptyBinding

class BookingsEmptyViewHolder(private val binding: MorePreviousEmptyBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: BookingEmptyAdapterItem, onButtonClicked: () -> Unit) = with(binding) {
        noBookingsMessage.setText(item.messageResId)
        button.clicks {
            onButtonClicked()
        }
    }
}