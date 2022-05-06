package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.common.extensions.formatWithCommas
import com.sohohouse.seven.network.core.models.Meta
import com.sohohouse.seven.network.core.models.RoomBooking
import com.squareup.moshi.Moshi
import moe.banana.jsonapi2.JsonBuffer
import retrofit2.Call

class GetRoomBookingsRequest(
    private val includes: Array<String>? = arrayOf(ROOM_INCLUDE_TYPE,
        HOTEL_INCLUDE_TYPE,
        RESOURCE_INCLUDE_TYPE,
        RESOURCE_META_INCLUDE_TYPE),
    override var page: Int? = null,
    override var perPage: Int? = null,
    private val startsAtFrom: String? = null,
    private val startsAtTo: String? = null,
    private val endsAtFrom: String? = null,
    private val filterStates: Array<String> = arrayOf(BOOKING_STATE_ACTIVE,
        BOOKING_STATE_CANCELLED),
    private val order: String? = ORDER_ROOM_BOOKING_STARTS_AT,
) : CoreAPIRequestPagable<List<RoomBooking>> {

    override fun getMeta(response: List<RoomBooking>): Meta? {
        val adapter = Moshi.Builder().build().adapter(Meta::class.java)
        if (response.isNotEmpty() && response[0].document.meta != null) {
            @Suppress("UNCHECKED_CAST")
            return (response[0].document.meta as JsonBuffer<Meta>).get(adapter)
        }
        return null
    }


    companion object {
        const val ORDER_ROOM_BOOKING_STARTS_AT_DESC: String = "-room-booking.starts_at"
        const val ROOM_INCLUDE_TYPE = "room"
        const val HOTEL_INCLUDE_TYPE = "hotel"
        const val BOOKING_STATE_ACTIVE = "ACTIVE"
        const val BOOKING_STATE_CANCELLED = "CANCELLED"
        const val ORDER_ROOM_BOOKING_STARTS_AT = "room-booking.starts_at"
        const val RESOURCE_INCLUDE_TYPE = "room_booking.resource"
        const val RESOURCE_META_INCLUDE_TYPE = "room_booking.resource.resource_meta"

        fun getMeta(response: List<RoomBooking>?): Meta? {
            if (response == null) return null
            val adapter = Moshi.Builder().build().adapter(Meta::class.java)
            if (response.isNotEmpty() && response[0].document.meta != null) {
                @Suppress("UNCHECKED_CAST")
                return (response[0].document.meta as JsonBuffer<Meta>).get(adapter)
            }
            return null
        }
    }


    override fun createCall(api: CoreApi): Call<out List<RoomBooking>> {
        val includes = this.includes?.formatWithCommas()
        return api.getRoomBookings(perPage = perPage, page = page, includeResources = includes,
            startsAtFrom = startsAtFrom, startsAtTo = startsAtTo, endsAtFrom = endsAtFrom,
            order = order
//                , filterState = filterStates  //TODO
        )
    }

}