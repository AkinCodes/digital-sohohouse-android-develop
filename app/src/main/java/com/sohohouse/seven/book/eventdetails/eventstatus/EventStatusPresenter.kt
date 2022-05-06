package com.sohohouse.seven.book.eventdetails.eventstatus

import com.sohohouse.seven.R
import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.common.extensions.getFormattedDateTime
import com.sohohouse.seven.common.extensions.isPendingLotteryState
import com.sohohouse.seven.common.views.*
import com.sohohouse.seven.common.views.eventdetaillist.BaseEventDetailsAdapterItem
import com.sohohouse.seven.common.views.eventdetaillist.EventGuestAdapterItem
import com.sohohouse.seven.book.eventdetails.bookingsuccess.EventBookingSuccessDescriptionItem
import com.sohohouse.seven.book.eventdetails.bookingsuccess.EventGuestListAdapterItem
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.network.core.models.EventBooking

val EVENT_STATUS_TYPES = listOf(
    EventStatusType.OPEN_FOR_BOOKING,
    EventStatusType.WAITING_LIST,
    EventStatusType.FULLY_BOOKED
)

class EventStatusPresenter(private val eventStatusItem: EventStatusItem) :
    BasePresenter<EventStatusViewController>() {

    private var guestCount: Int = 0
    private var bookingInfo: EventBooking? = null

    override fun onAttach(
        view: EventStatusViewController,
        isFirstAttach: Boolean,
        isRecreated: Boolean
    ) {
        super.onAttach(view, isFirstAttach, isRecreated)
        view.setScreenName(AnalyticsManager.Screens.BookingConfirmation.name)
        if (isFirstAttach) {
            prepareData()
        }
    }

    fun setUpData() {

        var userBookingState: UserBookingState? = null
        bookingInfo?.let {
            userBookingState = UserBookingState.getState(
                BookingType.valueOf(it.bookingType),
                it.state?.let { state -> BookingState.valueOf(state) })
        }

        val overviewItem =
            EventStatusAdapterItem(
                getEventStatusText(),
                eventStatusItem.event.startsAt?.getFormattedDateTime(eventStatusItem.timeZone),
                eventStatusItem.event.name,
                eventStatusItem.event.images?.large,
                eventStatusItem.venueName,
                eventStatusItem.venueColor,
                isStatusViewVisible(userBookingState),
                isCinemaSupporting(userBookingState)
            )

        val data: List<BaseEventDetailsAdapterItem>
        data = if (bookingInfo == null) {
            listOf(
                overviewItem,
                EventBookingSuccessDescriptionItem(
                    description =
                    if (eventStatusItem.eventStatus == EventStatusType.FULLY_BOOKED)
                        view.context.getString(R.string.event_booked_no_waitlist_supporting)
                    else view.context.getString(R.string.event_booked_waitlist_supporting)
                )
            )

        } else if (guestCount > 0 && eventStatusItem.event.startsAt != null) {
            listOf(
                overviewItem,
                EventGuestAdapterItem(
                    maxGuestNum = eventStatusItem.event.maxGuestsPerBooking,
                    guestNum = guestCount,
                    eventId = eventStatusItem.event.id,
                    eventName = eventStatusItem.event.name,
                    eventType = eventStatusItem.event.eventType
                ),
                EventGuestListAdapterItem(
                    guestNum = guestCount,
                    deleteGuestListener = null,
                    eventName = eventStatusItem.event.name,
                    venueName = eventStatusItem.venueName,
                    startDate = eventStatusItem.event.startsAt,
                    timeZone = eventStatusItem.timeZone
                )
            )
        } else {
            listOf(overviewItem)
        }

        executeWhenAvailable { view, _, _ -> view.setUpRecyclerView(data) }
    }

    private fun prepareData() {
        val bookingState =
            UserBookingState.getState(eventStatusItem.event.booking?.get(eventStatusItem.event.document))
        bookingInfo =
            if (bookingState != null) eventStatusItem.event.booking?.get(eventStatusItem.event.document) else null

        guestCount = bookingInfo?.numberOfGuests ?: 0

        executeWhenAvailable { v, _, _ ->
            v.initLayout(
                eventStatusItem.event,
                getBackground(),
                getButtonText(),
                eventStatusItem.eventStatus == EventStatusType.WAITING_LIST && bookingInfo == null
            )
        }

    }

    private fun getBackground(): Int {
        return if (eventStatusItem.event.isTicketless) {
            R.color.solitude
        } else if (guestCount > 0 || bookingInfo == null) {
            R.color.solitude
        } else {
            R.color.white
        }
    }

    private fun getButtonText(): String {
        return when {
            eventStatusItem.eventStatus == EventStatusType.OPEN_FOR_BOOKING
                    || eventStatusItem.eventStatus == EventStatusType.WAITING_LIST
                    && bookingInfo != null -> view.context.getString(R.string.explore_events_event_done_cta)
            eventStatusItem.eventStatus == EventStatusType.WAITING_LIST -> view.context.getString(R.string.event_booked_waitlist_cta)
            eventStatusItem.eventStatus == EventStatusType.FULLY_BOOKED -> view.context.getString(R.string.event_unavailable_explore_cta)
            eventStatusItem.event.isTicketless -> view.context.getString(R.string.explore_events_confirm_modal_ticketless_cta)
            else -> throw IllegalArgumentException("Unexpected event status type $${eventStatusItem.eventStatus} for event button text")
        }
    }

    private fun isCinemaSupporting(userBookingState: UserBookingState?): Boolean {
        return (eventStatusItem.event.isPendingLotteryState() && userBookingState != UserBookingState.HELD)
    }

    private fun isStatusViewVisible(userBookingState: UserBookingState?): Boolean {
        return (userBookingState == UserBookingState.WAIT_LIST && !eventStatusItem.event.isPendingLotteryState())
    }

    private fun getEventStatusText(): String {
        return when {
            eventStatusItem.eventStatus == EventStatusType.OPEN_FOR_BOOKING
                    || eventStatusItem.eventStatus == EventStatusType.WAITING_LIST
                    && bookingInfo != null -> view.context.getString(R.string.explore_events_event_going_label)
            eventStatusItem.eventStatus == EventStatusType.FULLY_BOOKED
                    && bookingInfo != null -> view.context.getString(R.string.event_booked_header)
            eventStatusItem.eventStatus == EventStatusType.WAITING_LIST
                    || eventStatusItem.eventStatus == EventStatusType.FULLY_BOOKED -> view.context.getString(
                R.string.event_booked_header
            )
            else -> throw IllegalArgumentException("Unexpected event status type $${eventStatusItem.eventStatus} for event status text")
        }
    }

}
