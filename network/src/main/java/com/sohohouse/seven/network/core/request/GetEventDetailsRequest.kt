package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.common.extensions.formatWithCommas
import com.sohohouse.seven.network.core.models.Event
import retrofit2.Call

class GetEventDetailsRequest(
    private val eventId: String,
    private val includeBookings: Boolean = false,
    private val includeResource: Boolean = false,
) : CoreAPIRequest<Event> {
    companion object {
        const val VENUE_INCLUDE_TYPE = "venue"
        const val FILM_INCLUDE_TYPE = "film"
        const val BOOKING_INCLUDE_TYPE = "booking"
        const val RESOURCE_INCLUDE_TYPE = "resource"
        const val RESOURCE_META_INCLUDE_TYPE = "resource.resource_meta"
    }

    override fun createCall(api: CoreApi): Call<out Event> {
        var includeResources = arrayOf(VENUE_INCLUDE_TYPE, FILM_INCLUDE_TYPE)
        if (includeBookings) {
            includeResources = includeResources.plus(BOOKING_INCLUDE_TYPE)
        }
        if (includeResource) {
            includeResources = includeResources.plus(RESOURCE_INCLUDE_TYPE)
            includeResources = includeResources.plus(RESOURCE_META_INCLUDE_TYPE)
        }
        return api.getEvent(eventId, includeResources.formatWithCommas())
    }
}