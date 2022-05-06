package com.sohohouse.seven.book.eventdetails.viewholders

import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.book.eventdetails.EventOverviewAdapterItem
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.views.EventStatusType
import com.sohohouse.seven.common.views.UserBookingState
import com.sohohouse.seven.databinding.EventDetailsOverviewLayoutBinding
import java.util.*

class EventOverviewViewHolder(
    private val binding: EventDetailsOverviewLayoutBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: EventOverviewAdapterItem,
        onButtonClicked: (isWaitList: Boolean, isLottery: Boolean, isTicketless: Boolean) -> Unit,
    ) = with(binding) {

        eventTitleLabel.text = item.eventName
        eventLocationName.text =
            if (item.isDigitalEvent) getString(R.string.event_digital_event) else item.houseName

        item.eventStatus?.let {
            setEventStatus(it, item.openingCancellationDate, item.timeZone)
        }
        setBookingStatus(item, onButtonClicked)

        if (item.instructor.isNotEmpty()) {
            eventInstructorName.visibility = VISIBLE
            eventInstructorName.text = item.instructor
        } else {
            eventInstructorName.visibility = GONE
        }
    }

    private fun EventDetailsOverviewLayoutBinding.setBookingStatus(
        item: EventOverviewAdapterItem,
        onButtonClicked: (isWaitList: Boolean, isLottery: Boolean, isTicketless: Boolean) -> Unit,
    ) {
        if (item.bookingState != null && item.isCancelled.not()) {
            setBookingStatus(item)
            leaveGuestWaitListButton.clicks {
                onButtonClicked(
                    item.bookingState == UserBookingState.WAIT_LIST,
                    item.isPendingLotteryState,
                    item.isTicketless
                )
            }
        } else {
            guestWaitListText.visibility = GONE
            leaveGuestWaitListButton.visibility = GONE
        }
    }

    private fun EventDetailsOverviewLayoutBinding.setBookingStatus(item: EventOverviewAdapterItem) {
        when {
            item.isPendingLotteryState -> {
                guestWaitListText.setText(
                    R.string.explore_cinema_event_confirmation_pending_label,
                    getAttributeColor(R.attr.colorEventLottery)
                )
                leaveGuestWaitListButton.text =
                    getString(R.string.explore_cinema_event_pending_cancel_cta)
            }
            item.isTicketless -> {
                guestWaitListText.text = ""
                leaveGuestWaitListButton.text =
                    getString(R.string.explore_events_event_cancel_ticketless_cta)
            }
            item.bookingState == UserBookingState.GUEST_LIST -> {
                guestWaitListText.setText(
                    if (item.numberOfGuests > 0) {
                        getQuantityString(
                            R.plurals.explore_events_event_guests_card_label,
                            item.numberOfGuests
                        ).replaceBraces(item.numberOfGuests.toString())
                    } else {
                        getString(R.string.explore_events_event_booked_success_label)
                    }, getAttributeColor(item.bookingState.colorAttr)
                )
                leaveGuestWaitListButton.text =
                    getString(R.string.explore_events_event_cancel_booking_cta)
            }
            item.bookingState == UserBookingState.WAIT_LIST -> {
                guestWaitListText.setText(
                    R.string.explore_events_event_waiting_success_label,
                    getAttributeColor(item.bookingState.colorAttr)
                )
                leaveGuestWaitListButton.text =
                    getString(R.string.explore_events_event_leave_waiting_cta)
            }
            item.bookingState == UserBookingState.HELD -> {
                guestWaitListText.setText(
                    R.string.explore_events_event_pending_label,
                    getAttributeColor(item.bookingState.colorAttr)
                )
                leaveGuestWaitListButton.text =
                    getString(R.string.explore_events_event_cancel_booking_cta)
            }
        }
        guestWaitListText.setVisible()
        leaveGuestWaitListButton.isVisible = !item.hideActionButton()
    }

    private fun EventDetailsOverviewLayoutBinding.setEventStatus(
        eventStatusType: EventStatusType,
        openDate: Date? = null,
        timeZone: String,
    ) {
        eventStatus.setupLayout(
            eventStatusType,
            openDate = openDate?.getFormattedDateTime(timeZone)
        )
        eventStatus.setVisible()
    }
}