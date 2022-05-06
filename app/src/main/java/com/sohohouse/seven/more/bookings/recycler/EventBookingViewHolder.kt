package com.sohohouse.seven.more.bookings.recycler

import android.view.View
import android.widget.TextView
import com.sohohouse.seven.R
import com.sohohouse.seven.book.adapter.viewholders.EventViewHolder
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.getColor
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.common.views.HouseNameTextView
import com.sohohouse.seven.common.views.StatusView
import com.sohohouse.seven.databinding.MorePastBookingsDetailCardBinding
import com.sohohouse.seven.more.bookings.detail.recycler.MorePastBookingsDetailCardAdapterItem
import com.sohohouse.seven.network.core.models.Event

class EventBookingViewHolder(
    private val binding: MorePastBookingsDetailCardBinding,
    private val lightTheme: Boolean = false
) : EventViewHolder(binding.root, binding.detailCardContent.eventImage) {

    override val houseName: HouseNameTextView
        get() = binding.detailCardContent.eventLocationName

    override val eventName: TextView
        get() = binding.detailCardContent.eventTitleLabel

    override val eventDateAndTime: TextView
        get() = binding.detailCardContent.eventDateAndTimeLabel

    override val eventStatus: StatusView
        get() = binding.detailCardContent.eventStatus

    override val eventBookingStatus: TextView
        get() = binding.detailCardContent.eventBookingStatus

    fun bind(
        item: EventBookingAdapterItem,
        onItemClicked: (eventBooking: EventBookingAdapterItem) -> Unit
    ) {

        bindEvent(item.eventBooking.event ?: Event(), eventStatusType = null, timeZone = null)

        eventDateAndTime.text = item.dateAndTime
        binding.detailCardContent.eventLocationName.text = item.houseName
        binding.detailCardContent.root.clicks { onItemClicked(item) }

        eventBookingStatus.visibility = if (item.includeStatus) View.VISIBLE else View.INVISIBLE

        if (lightTheme) {
            with(binding.detailCardContent) {
                eventTitleLabel.setTextColor(getColor(R.color.white))
                eventDateAndTimeLabel.setTextColor(getColor(R.color.white56))
                eventLocationName.setTextColor(getColor(R.color.white56))
            }
        }
    }

    fun bind(item: MorePastBookingsDetailCardAdapterItem) {
        bindView(item.imageUrl, item.houseName, item.eventName, item.dateAndTime)
    }

    private fun bindView(
        imageUrl: String,
        houseText: String,
        eventText: String,
        dateAndTime: String,
        status: String = ""
    ) {
        houseName.text = houseText
        eventName.text = eventText
        eventDateAndTime.text = dateAndTime
        eventBookingStatus.visibility = if (status.isNotEmpty()) View.VISIBLE else View.INVISIBLE
        eventBookingStatus.text = status
        eventImage.setImageFromUrl(imageUrl)
    }

}