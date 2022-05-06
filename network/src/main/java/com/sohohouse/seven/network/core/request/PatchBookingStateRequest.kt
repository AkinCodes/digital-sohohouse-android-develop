package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.PatchBookingState
import moe.banana.jsonapi2.HasOne
import retrofit2.Call

class PatchBookingStateRequest(
    private val bookingState: String,
    private val eventId: String,
    private val bookingId: String,
) : CoreAPIRequest<Void> {
    override fun createCall(api: CoreApi): Call<out Void> {
        val patchBookingState = PatchBookingState(
            state = bookingState,
            eventState = HasOne("events", eventId)
        )
        return api.patchBookingState(bookingId, patchBookingState)
    }
}