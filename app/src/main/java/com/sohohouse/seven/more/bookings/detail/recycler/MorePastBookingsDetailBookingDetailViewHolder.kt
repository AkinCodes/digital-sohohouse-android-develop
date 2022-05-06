package com.sohohouse.seven.more.bookings.detail.recycler

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.MorePastBookingsDetailBookingDetailBinding

class MorePastBookingsDetailBookingDetailViewHolder(
    private val binding: MorePastBookingsDetailBookingDetailBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(titleString: String, supportingString: String, isLastItem: Boolean) = with(binding) {
        detailTitle.text = titleString
        detailSupporting.text = supportingString
        divider.isVisible = !isLastItem
    }
}