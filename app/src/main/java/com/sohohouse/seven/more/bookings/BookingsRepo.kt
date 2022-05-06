package com.sohohouse.seven.more.bookings

import android.text.format.DateUtils
import com.sohohouse.seven.common.extensions.getApiFormattedDate
import com.sohohouse.seven.common.utils.ErrorInteractor
import com.sohohouse.seven.common.utils.Quadruple
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.more.bookings.BookingsRepo.SortOrder.ASC
import com.sohohouse.seven.more.bookings.BookingsRepo.SortOrder.DESC
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.CoreRequestFactory
import com.sohohouse.seven.network.core.models.*
import com.sohohouse.seven.network.core.request.GetEventBookingsRequest
import com.sohohouse.seven.network.core.request.GetRoomBookingsRequest
import com.sohohouse.seven.network.core.request.GetTableBookingsRequest
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingsRepo @Inject constructor(
    private val requestFactory: CoreRequestFactory,
    private val errorInteractor: ErrorInteractor,
    private val venueRepo: VenueRepo
) {

    companion object {
        val ALL_EVENT_TYPES = arrayOf(
            "GYM_CLASS",
            "INDUCTION",
            "MEMBER_EVENT",
            "HOUSE_RIDE",
            "SCREENING",
            "HOUSE_VISIT"
        )
        const val PAGE_SIZE_NO_PAGING = 100
    }

    private fun getEventBookings(
        startsAtFrom: Date,
        startsAtTo: Date,
        order: String,
        types: Array<String>,
        page: Int,
        perPage: Int
    ): Either<ServerError, List<EventBooking>> {
        return requestFactory.createV2(
            getEventBookingsRequest(
                startsAtFrom,
                startsAtTo,
                order,
                types,
                page,
                perPage
            )
        )
    }

    private fun getEventBookingsRequest(
        startsAtFrom: Date,
        startsAtTo: Date,
        order: String,
        types: Array<String>,
        page: Int,
        perPage: Int
    ): GetEventBookingsRequest {
        return GetEventBookingsRequest(
            startsAtFrom = startsAtFrom.getApiFormattedDate(),
            startsAtTo = startsAtTo.getApiFormattedDate(),
            order = order,
            filterType = types,
            page = page,
            perPage = perPage
        )
    }

    private fun getRoomBookings(
        startsAtFrom: Date,
        startsAtTo: Date,
        order: String,
        page: Int,
        perPage: Int
    ): Either<ServerError, List<RoomBooking>> {
        return requestFactory.createV2(
            getRoomBookingsRequest(
                startsAtFrom,
                startsAtTo,
                order,
                page,
                perPage
            )
        )
    }

    private fun getRoomBookingsRequest(
        startsAtFrom: Date,
        startsAtTo: Date,
        order: String,
        page: Int,
        perPage: Int
    ): GetRoomBookingsRequest {
        return GetRoomBookingsRequest(
            startsAtFrom = startsAtFrom.getApiFormattedDate(),
            startsAtTo = startsAtTo.getApiFormattedDate(),
            order = order,
            page = page,
            perPage = perPage
        )
    }

    private fun getTableBookings(
        startsAtFrom: Date,
        startsAtTo: Date,
        status: String?
    ): Either<ServerError, List<TableReservation>> {
        return requestFactory.createV2(
            GetTableBookingsRequest(
                fromDate = startsAtFrom.getApiFormattedDate(),
                toDate = startsAtTo.getApiFormattedDate(),
                status = status
            )
        )
    }

    fun getUpcomingBookings(
        perPageEach: Int = PAGE_SIZE_NO_PAGING,
        page: Int = 1
    ): Either<ServerError, Pair<List<Booking>, VenueList>> {
        val startDateTo =
            Calendar.getInstance().apply { timeInMillis += (DateUtils.YEAR_IN_MILLIS * 5) }.time
        val roomsStartDateFrom = DateTime.now().plusDays(1).toDate()    //rooms from tomorrow
        val eventsStartDateFrom = Date()    // events from now
        val tablesStartDateFrom = Date()    //tables from now
        return getBookings(
            eventsStartsDateFrom = eventsStartDateFrom,
            roomsStartDateFrom = roomsStartDateFrom,
            startsAtTo = startDateTo,
            sortOrder = ASC,
            page = page,
            perPageEach = perPageEach,
            tablesStartDateFrom = tablesStartDateFrom,
            tablesStatus = GetTableBookingsRequest.STATUS_UPCOMING
        )
    }

    fun getBookings(
        eventsStartsDateFrom: Date,
        roomsStartDateFrom: Date,
        startsAtTo: Date,
        eventTypes: Array<String> = ALL_EVENT_TYPES,
        sortOrder: SortOrder,
        page: Int = 1,
        perPageEach: Int = PAGE_SIZE_NO_PAGING,
        tablesStartDateFrom: Date? = null,
        tablesStatus: String? = null
    ): Either<ServerError, Pair<List<Booking>, VenueList>> {

        var eventsOrder = ""
        var roomsOrder = ""

        when (sortOrder) {
            DESC -> {
                eventsOrder = GetEventBookingsRequest.ORDER_EVENT_STARTS_AT
                roomsOrder = GetRoomBookingsRequest.ORDER_ROOM_BOOKING_STARTS_AT
            }
            ASC -> {
                eventsOrder = GetEventBookingsRequest.ORDER_EVENT_STARTS_AT_DESC
                roomsOrder = GetRoomBookingsRequest.ORDER_ROOM_BOOKING_STARTS_AT_DESC
            }
        }

        val events = getEventBookings(
            eventsStartsDateFrom,
            startsAtTo,
            order = eventsOrder,
            page = page,
            perPage = perPageEach,
            types = eventTypes
        )
        val rooms = getRoomBookings(
            roomsStartDateFrom,
            startsAtTo,
            order = roomsOrder,
            page = page,
            perPage = perPageEach
        )
        val tables = tablesStartDateFrom?.let {
            getTableBookings(
                tablesStartDateFrom,
                startsAtTo,
                tablesStatus
            )
        } ?: value(emptyList())

        val zipped = value(Quadruple(value(venueRepo.venues()), events, rooms, tables))
        return errorInteractor.quadError(zipped).fold(
            ifError = { Either.Error(it) },
            ifValue = {
                val (houses, eventBookings, roomBookings, tableBookings) = it
                setEventDetailInfo(eventBookings)
                val bookings =
                    mergeRoomsAndEvents(roomBookings, eventBookings, tableBookings, sortOrder)
                value(Pair(bookings, houses))
            },
            ifEmpty = { Either.Empty() }
        )
    }

    private fun setEventDetailInfo(bookings: List<EventBooking>) {
        for (booking in bookings) {
            val event = booking.event ?: Event()
            event.document.addInclude(booking)
        }
    }

    private fun mergeRoomsAndEvents(
        rooms: List<RoomBooking>,
        events: List<EventBooking>,
        tables: List<TableReservation>,
        order: SortOrder
    ): List<Booking> {
        @Suppress("UNCHECKED_CAST") val bookings =
            rooms.union(events).union(tables).toList() as List<Booking>
        return when (order) {
            DESC -> bookings.sortedByDescending { it.startsAt }
            ASC -> bookings.sortedBy { it.startsAt }
        }
    }

    enum class SortOrder {
        DESC,
        ASC
    }

}