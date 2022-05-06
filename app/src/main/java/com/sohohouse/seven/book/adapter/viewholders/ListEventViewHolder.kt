package com.sohohouse.seven.book.adapter.viewholders

import android.widget.TextView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.views.HouseNameTextView
import com.sohohouse.seven.common.views.StatusView
import com.sohohouse.seven.databinding.ItemBookingListItemBinding

const val LIST_EVENT_LAYOUT = R.layout.item_booking_list_item

class ListEventViewHolder(
    private val binding: ItemBookingListItemBinding
) : EventViewHolder(binding.root, binding.itemContent.eventImage) {

    override val houseName: HouseNameTextView
        get() = binding.itemContent.eventLocationName

    override val eventName: TextView
        get() = binding.itemContent.eventTitleLabel

    override val eventDateAndTime: TextView
        get() = binding.itemContent.eventDateAndTimeLabel

    override val eventStatus: StatusView
        get() = binding.itemContent.eventStatus

    override val eventBookingStatus: TextView
        get() = binding.itemContent.eventBookingStatus
}