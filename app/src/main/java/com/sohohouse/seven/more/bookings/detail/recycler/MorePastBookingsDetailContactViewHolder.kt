package com.sohohouse.seven.more.bookings.detail.recycler

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.MorePastBookingsContactCtaBinding
import com.sohohouse.seven.more.bookings.detail.MorePastBookingsDetailContactListener

class MorePastBookingsDetailContactViewHolder(private val binding: MorePastBookingsContactCtaBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(contactListener: MorePastBookingsDetailContactListener) = with(binding.supporting) {
        setup(null, R.string.explore_events_booking_details_contact_supporting, true)
        clicks { contactListener.onContactButtonClicked() }
    }
}