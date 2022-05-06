package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.common.extensions.formatWithCommas
import com.sohohouse.seven.network.core.models.EventBooking
import com.sohohouse.seven.network.core.models.Meta
import com.squareup.moshi.Moshi
import moe.banana.jsonapi2.JsonBuffer

class GetEventBookingsRequest constructor(
    private val filterType: Array<String>? = null,
    private val includes: Array<String>? = arrayOf(EVENT_INCLUDE_TYPE,
        VENUE_INCLUDE_TYPE,
        RESOURCE_INCLUDE_TYPE,
        RESOURCE_META_INCLUDE_TYPE,
        FILM_INCLUDE_TYPE),
    override var page: Int? = null,
    override var perPage: Int? = null,
    private val startsAtFrom: String? = null,
    private val startsAtTo: String? = null,
    private val endsAtFrom: String? = null,
    private val filterStates: Array<String> = arrayOf(BOOKING_STATE_CONFIRMED,
        BOOKING_STATE_HELD,
        BOOKING_STATE_UNCONFIRMED,
        BOOKING_STATE_WAITING),
    private val order: String? = ORDER_EVENT_STARTS_AT,
) : CoreAPIRequestPagable<List<EventBooking>> {
    override fun createCall(api: CoreApi): retrofit2.Call<out List<EventBooking>> {
        val filterType = this.filterType?.formatWithCommas()
        val includes = this.includes?.formatWithCommas()
        val filterStates = this.filterStates.formatWithCommas()
        return api.getEventBookings(filterType,
            perPage,
            page,
            includes,
            startsAtFrom = startsAtFrom,
            startsAtTo = startsAtTo,
            endsAtFrom = endsAtFrom,
            filterState = filterStates,
            order = order)
    }

    override fun getMeta(response: List<EventBooking>): Meta? {
        val adapter = Moshi.Builder().build().adapter(Meta::class.java)
        if (response.isNotEmpty() && response[0].document.meta != null) {
            @Suppress("UNCHECKED_CAST")
            return (response[0].document.meta as JsonBuffer<Meta>).get(adapter)
        }
        return null
    }

    companion object {
        const val EVENT_INCLUDE_TYPE = "event"
        const val VENUE_INCLUDE_TYPE = "venue"

        const val BOOKING_STATE_CONFIRMED = "CONFIRMED"
        const val BOOKING_STATE_CANCELLED = "CANCELLED"
        const val BOOKING_STATE_UNCONFIRMED = "UNCONFIRMED"
        const val BOOKING_STATE_WAITING = "WAITING"
        const val BOOKING_STATE_HELD = "HELD"
        const val ORDER_EVENT_STARTS_AT = "event.starts_at"
        const val ORDER_EVENT_STARTS_AT_DESC = "-event.starts_at"
        const val RESOURCE_INCLUDE_TYPE = "event.resource"
        const val RESOURCE_META_INCLUDE_TYPE = "event.resource.resource_meta"
        const val FILM_INCLUDE_TYPE = "event.film"

        fun getMeta(response: List<EventBooking>?): Meta? {
            if (response == null) return null
            val adapter = Moshi.Builder().build().adapter(Meta::class.java)
            if (response.isNotEmpty() && response[0].document.meta != null) {
                @Suppress("UNCHECKED_CAST")
                return (response[0].document.meta as JsonBuffer<Meta>).get(adapter)
            }
            return null
        }
    }
}