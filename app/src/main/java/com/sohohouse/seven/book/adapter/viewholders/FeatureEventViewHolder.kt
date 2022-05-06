package com.sohohouse.seven.book.adapter.viewholders

import android.widget.TextView
import com.sohohouse.seven.common.views.HouseNameTextView
import com.sohohouse.seven.common.views.StatusView
import com.sohohouse.seven.databinding.FeatureEventCardBinding

class FeatureEventViewHolder(
    private val binding: FeatureEventCardBinding
) : EventViewHolder(binding.root, binding.featureEventCardEventImage) {

    override val houseName: HouseNameTextView
        get() = binding.eventLocationName

    override val eventName: TextView
        get() = binding.eventTitleLabel

    override val eventDateAndTime: TextView
        get() = binding.eventDateAndTimeLabel

    override val eventStatus: StatusView
        get() = binding.eventStatus

    override val eventBookingStatus: TextView
        get() = binding.eventBookingStatus

}