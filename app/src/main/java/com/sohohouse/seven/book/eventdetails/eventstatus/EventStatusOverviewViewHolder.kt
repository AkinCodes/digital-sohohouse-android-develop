package com.sohohouse.seven.book.eventdetails.eventstatus

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.views.EventStatusType
import com.sohohouse.seven.databinding.BookingSuccessOverviewLayoutBinding

class EventStatusOverviewViewHolder(
    private val binding: BookingSuccessOverviewLayoutBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: EventStatusAdapterItem, listener: EventStatusAdapterListener) = with(binding) {

        bookingSuccessText.text = item.eventStatusTitle

        eventCardItem.eventImage.setImageFromUrl(item.eventImageUrl)

        if (item.eventDate != null) {
            eventCardItem.eventDateAndTimeLabel.text = item.eventDate
        } else {
            eventCardItem.eventDateAndTimeLabel.visibility = View.GONE
        }

        eventCardItem.eventTitleLabel.text = item.eventName
        eventCardItem.eventLocationName.text = item.venueName

        if (item.isStatusViewVisible) {
            eventCardItem.eventStatus.visibility = View.VISIBLE
            eventCardItem.eventStatus.setupLayout(EventStatusType.WAITING_LIST)
        } else {
            eventCardItem.eventStatus.visibility = View.GONE
        }

        bookingSuccessCinemaSupporting.setVisible(item.isCinemaSupporting)

        bookingSuccessBackBtn.clicks { listener.onBackPress() }
    }
}