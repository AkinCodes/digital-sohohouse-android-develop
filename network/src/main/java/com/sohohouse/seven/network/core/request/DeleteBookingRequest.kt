package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import retrofit2.Call

class DeleteBookingRequest(private val bookingId: String) : CoreAPIRequest<Void> {
    override fun createCall(api: CoreApi): Call<out Void> {
        return api.deleteBooking(bookingId)
    }
}