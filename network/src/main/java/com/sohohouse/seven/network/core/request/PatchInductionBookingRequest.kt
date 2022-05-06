package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.EventBooking
import moe.banana.jsonapi2.ObjectDocument
import retrofit2.Call

class PatchInductionBookingRequest(private val eventId: String, private val bookingId: String) :
    CoreAPIRequest<EventBooking> {
    override fun createCall(api: CoreApi): Call<out EventBooking> {
        val document = ObjectDocument<Event>()
        val event = Event()
        event.id = eventId
        document.set(event)
        return api.updateInductionBooking(bookingId, document)
    }
}