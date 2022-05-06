package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.*
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.ObjectDocument
import retrofit2.Call

class PatchEventBookingRequest(
    private val tickets: Int,
    private val eventId: String,
    private val bookingId: String,
    private val paymentId: String? = null,
) : CoreAPIRequest<EventBooking> {
    override fun createCall(api: CoreApi): Call<out EventBooking> {
        val document = ObjectDocument<NewEventBooking>()
        val event = Event()
        event.id = eventId
        val paymentCard = Card()
        paymentCard.id = paymentId ?: ""
        val guestsList = mutableListOf<Guests>()
        for (i in 1..tickets) {
            guestsList.add(Guests())
        }
        val eventBooking = NewEventBooking(guests = guestsList,
            event = HasOne(event),
            paymentCard = HasOne(paymentCard))
        document.set(eventBooking)
        return api.updateBooking(bookingId, document)
    }
}