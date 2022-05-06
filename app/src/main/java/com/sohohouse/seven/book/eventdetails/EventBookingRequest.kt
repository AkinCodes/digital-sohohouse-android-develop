package com.sohohouse.seven.book.eventdetails

import com.sohohouse.seven.common.views.UserBookingState
import com.sohohouse.seven.network.core.models.Event

data class EventBookingRequest(
    val eventId: String,
    val eventName: String,
    val eventType: String,
    val eventPrice: Int,
    val eventPriceCurrency: String?,
    val numberOfTickets: Int,
    val newTickets: Int,
    val bookingId: String?
) {

    companion object {
        fun create(
            event: Event,
            numberOfTickets: Int = 0,
            newTickets: Int = 0
        ): EventBookingRequest {
            val bookingState = UserBookingState.getState(event.booking?.get(event.document))
            val eventBooking =
                if (bookingState != null) event.booking?.get(event.document) else null
            return EventBookingRequest(
                eventId = event.id,
                eventName = event.name,
                eventType = event.eventType,
                eventPrice = event.priceCents,
                eventPriceCurrency = event.priceCurrency,
                numberOfTickets = numberOfTickets,
                newTickets = newTickets,
                bookingId = eventBooking?.id
            )
        }
    }
}
