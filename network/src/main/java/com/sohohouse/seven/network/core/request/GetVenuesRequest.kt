package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.common.extensions.formatWithCommas
import com.sohohouse.seven.network.core.models.Venue
import retrofit2.Call

class GetVenuesRequest
    : CoreAPIRequest<List<Venue>> {
    companion object {
        private const val HOUSE_INCLUDE_TYPE = "house"
        private const val RESTAURANT_INCLUDE_TYPE = "restaurant"
        private const val HOTEL_INCLUDE_TYPE = "hotel"
        const val HOUSE_VENUE_TYPE = "HOUSE"
        const val CWH_VENUE_TYPE = "CWH"
    }

    override fun createCall(api: CoreApi): Call<out List<Venue>> {
        return api.getVenues(
            includeResources = arrayOf(
                HOUSE_INCLUDE_TYPE,
                RESTAURANT_INCLUDE_TYPE,
                HOTEL_INCLUDE_TYPE
            ).formatWithCommas(),
            isTopLevel = false,
            venueTypes = null
        )
    }
}