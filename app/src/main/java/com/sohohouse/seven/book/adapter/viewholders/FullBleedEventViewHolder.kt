package com.sohohouse.seven.book.adapter.viewholders

import android.widget.ImageView
import android.widget.TextView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.views.HouseNameTextView
import com.sohohouse.seven.common.views.StatusView
import com.sohohouse.seven.databinding.FullBleedEventCardBinding

const val FULL_BLEED_EVENT_LAYOUT = R.layout.full_bleed_event_card

class FullBleedEventViewHolder(
    private val binding: FullBleedEventCardBinding
) : EventViewHolder(binding.root, binding.fullBleedEventImage) {

    override val houseName: HouseNameTextView
        get() = binding.fullBleedEventLocationName

    override val eventName: TextView
        get() = binding.fullBleedEventTitleLabel

    override val eventDateAndTime: TextView
        get() = binding.fullBleedEventDateAndTimeLabel

    override val eventStatus: StatusView
        get() = binding.fullBleedEventStatus

    override val eventBookingStatus: TextView
        get() = binding.fullBleedEventBookingStatus

    override val eventCategory: ImageView
        get() = binding.fullBleedEventCategoryIcon
}