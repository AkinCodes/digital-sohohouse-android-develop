package com.sohohouse.seven.book.eventdetails.bookingsuccess

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.views.EventStatusType
import com.sohohouse.seven.common.views.UserBookingState
import com.sohohouse.seven.databinding.BookingSuccessOverviewLayoutBinding

class BookingSuccessOverviewViewHolder(
    private val binding: BookingSuccessOverviewLayoutBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: EventBookingSuccessAdapterItem, listener: BookingSuccessAdapterListener) =
        with(binding) {
            bookingSuccessText.setText(
                when {
                    item.bookingState == UserBookingState.HELD -> R.string.explore_events_event_booked_pending_label
                    item.isPendingLotteryState -> R.string.explore_cinema_request_recieved_header
                    item.isInduction && item.bookingState == UserBookingState.WAIT_LIST -> R.string.onboarding_intro_contacted_header
                    item.isInduction -> R.string.onboarding_intro_booked_header
                    item.bookingState == UserBookingState.WAIT_LIST -> R.string.explore_events_event_waiting_success_label
                    item.isDigitalEvent -> R.string.explore_events_confirm_modal_ticketless_header
                    else -> R.string.explore_events_event_booked_success_label
                }
            )

            eventCardItem.eventImage.setImageFromUrl(item.eventImageUrl)

            if (item.eventDate != null) {
                eventCardItem.eventDateAndTimeLabel.text = item.eventDate
            } else {
                eventCardItem.eventDateAndTimeLabel.visibility = View.GONE
            }

            eventCardItem.eventTitleLabel.text = item.eventName
            eventCardItem.eventLocationName.text = item.venueName

            if (item.bookingState == UserBookingState.WAIT_LIST
                && !item.isPendingLotteryState
                && !item.isInduction
            ) {
                eventCardItem.eventStatus.setupLayout(EventStatusType.WAITING_LIST)
            } else {
                eventCardItem.eventStatus.visibility = View.GONE
            }

            bookingSuccessCinemaSupporting.setVisible(
                item.isPendingLotteryState && item.bookingState != UserBookingState.HELD
            )
            bookingSuccessBackBtn.clicks { listener.onBackClicked() }
        }
}