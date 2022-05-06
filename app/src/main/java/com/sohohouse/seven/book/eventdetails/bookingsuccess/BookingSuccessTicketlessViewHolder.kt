package com.sohohouse.seven.book.eventdetails.bookingsuccess

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.databinding.BookingSuccessTicketlessOverviewLayoutBinding

class BookingSuccessTicketlessOverviewViewHolder(
    private val binding: BookingSuccessTicketlessOverviewLayoutBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: EventBookingTicketlessAdapterItem,
        listener: BookingSuccessAdapterListener? = null,
    ) = with(binding) {
        bookingEventCardItem.eventImage.setImageFromUrl(item.eventImageUrl)

        if (item.eventDate != null) {
            bookingEventCardItem.eventDateAndTimeLabel.text = item.eventDate
        } else {
            bookingEventCardItem.eventDateAndTimeLabel.visibility = View.GONE
        }
        bookingEventCardItem.eventTitleLabel.text = item.eventName
        bookingEventCardItem.eventLocationName.text = item.venueName

        bookingSuccessBackBtn.clicks {
            listener?.onBackClicked()
        }
    }
}