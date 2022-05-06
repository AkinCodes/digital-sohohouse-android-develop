package com.sohohouse.seven.book.eventdetails

import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.analytics.AnalyticsEvent.Events.*
import com.sohohouse.seven.common.utils.CurrencyUtils
import com.sohohouse.seven.common.views.*
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.EventBooking

interface EventDetailTracker {

    fun track(analytics: AnalyticsEvent)

    fun track(event: Event)

    fun trackDeleteBooking(event: Event, eventBooking: EventBooking?, bookingType: BookingType?)

    fun trackDeleteGuest(event: Event)

    fun trackBookingSuccess(event: Event, numberOfTickets: Int, state: String)

    fun trackUserClickCancelButton(event: Event)
}

class EventDetailTrackerImpl(private val analyticsManager: AnalyticsManager) : EventDetailTracker {

    override fun track(analytics: AnalyticsEvent) {
        analyticsManager.track(analytics)
    }

    override fun track(event: Event) {
        when (EventType.get(event.eventType)) {
            EventType.MEMBER_EVENT -> track(AnalyticsEvent.Explore.Events.View(event.id))
            EventType.CINEMA_EVENT -> track(AnalyticsEvent.Explore.Cinema.View(event.id))
            EventType.FITNESS_EVENT -> track(AnalyticsEvent.Explore.Fitness.View(event.id))
            EventType.HOUSE_VISIT -> track(AnalyticsEvent.Explore.HouseVisit.View(event.id))
        }
    }

    override fun trackDeleteBooking(
        event: Event,
        eventBooking: EventBooking?,
        bookingType: BookingType?
    ) {
        val eventAnalytics = when {
            bookingType == BookingType.GUEST_LIST -> event.priceCurrency?.let {
                CancelConfirmation(
                    event.id,
                    CurrencyUtils.getFormattedPrice(
                        event.priceCents * (eventBooking?.numberOfGuests
                            ?: 0 + 1), it
                    )
                )
            } ?: return
            event.priceCents > 0 -> {
                LeaveWaitListPaid(
                    event.id,
                    eventBooking?.numberOfGuests ?: 0 + 1
                )
            }
            else -> {
                LeaveWaitListFree(
                    event.id,
                    eventBooking?.numberOfGuests ?: 0 + 1
                )
            }
        }
        track(eventAnalytics)
    }

    override fun trackDeleteGuest(event: Event) {
        if (event.priceCents > 0) {
            track(RemoveGuestPaid(event.id))
        } else {
            track(RemoveGuestFree(event.id))
        }
    }

    override fun trackBookingSuccess(event: Event, numberOfTickets: Int, state: String) {
        if (event.priceCents > 0 && UserBookingState.valueOf(state) == UserBookingState.GUEST_LIST) {
            track(BookPaid(event.id, numberOfTickets))
        } else if (event.priceCents > 0 && UserBookingState.valueOf(state) == UserBookingState.GUEST_LIST) {
            track(JoinWaitListPaid(event.id, numberOfTickets))
        } else if (UserBookingState.valueOf(state) == UserBookingState.GUEST_LIST) {
            track(BookFree(event.id, numberOfTickets))
        } else if (UserBookingState.valueOf(state) == UserBookingState.WAIT_LIST) {
            track(JoinWaitListFree(event.id, numberOfTickets))
        }
    }

    override fun trackUserClickCancelButton(event: Event) {
        if (event.priceCents > 0) {
            track(CancelPaid(event.id))
        } else {
            track(CancelFree(event.id))
        }
    }
}
