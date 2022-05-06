package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.*
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.ObjectDocument
import retrofit2.Call

class PostEventBookingRequest(
    private val guestTickets: Int,
    private val eventId: String,
    private val cardId: String? = null,
) : CoreAPIRequest<EventBooking> {
    override fun createCall(api: CoreApi): Call<out EventBooking> {
        val document = ObjectDocument<NewEventBooking>()
        val event = Event()
        event.id = eventId
        val guestsList = mutableListOf<Guests>()
        for (i in 1..guestTickets) {
            guestsList.add(Guests(null, null, null))
        }
        val eventBooking = if (cardId == null) {
            NewEventBooking(guests = guestsList, event = HasOne(event))
        } else {
            val paymentCard = Card()
            paymentCard.id = cardId
            NewEventBooking(guests = guestsList,
                event = HasOne(event),
                paymentCard = HasOne(paymentCard))
        }
        document.set(eventBooking)
        return api.postEventBooking(document)
    }
}