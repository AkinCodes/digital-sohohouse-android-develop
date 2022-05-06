package com.sohohouse.seven.more.bookings.recycler

import androidx.annotation.StringRes
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.network.core.models.EventBooking
import com.sohohouse.seven.network.core.models.RoomBooking
import com.sohohouse.seven.network.core.models.TableReservation
import org.joda.time.Interval

enum class BookingAdapterItemType {
    HEADER,
    DATE_HEADER,
    COLLAPSIBLE_MONTH,
    EVENT_BOOKING,
    ROOM_BOOKING,
    TABLE_BOOKING,
    EMPTY,
    EMPTY_MONTH,
    ERROR
}

open class BookingAdapterItem(val itemType: BookingAdapterItemType) : DiffItem

data class BookingsHeaderTextItem(val text: String) :
    BookingAdapterItem(BookingAdapterItemType.HEADER) {
    override val key: Any
        get() = javaClass
}

data class UpcomingBookingsDateAdapterItem(val formattedDate: String) :
    BookingAdapterItem(BookingAdapterItemType.DATE_HEADER) {
    override val key: Any
        get() = formattedDate
}

data class EventBookingAdapterItem constructor(
    val houseName: String, val eventName: String, val dateAndTime: String,
    val imageUrl: String, val eventBooking: EventBooking,
    val includeStatus: Boolean
) : BookingAdapterItem(BookingAdapterItemType.EVENT_BOOKING) {
    override val key: Any?
        get() = eventBooking.id
}

data class TableBookingAdapterItem(
    val tableBookingId: String,
    val venueName: String,
    val dateTime: String,
    val address: String,
    val imageUrl: String,
    val tableBooking: TableReservation
) : BookingAdapterItem(BookingAdapterItemType.TABLE_BOOKING) {
    override val key: Any
        get() = tableBookingId
}

data class RoomBookingAdapterItem(
    val hotelName: String, val dateAndTime: String, val roomName: String,
    val imageUrl: String, val roomBooking: RoomBooking, val statusLabel: String
) : BookingAdapterItem(BookingAdapterItemType.ROOM_BOOKING) {
    override val key: Any?
        get() = roomBooking.id
}

data class BookingEmptyAdapterItem(@StringRes val messageResId: Int) :
    BookingAdapterItem(BookingAdapterItemType.EMPTY)

object PastBookingsEmptyMonthAdapterItem : BookingAdapterItem(BookingAdapterItemType.EMPTY_MONTH)

object BookingErrorStateAdapterItem : BookingAdapterItem(BookingAdapterItemType.ERROR)

data class PastBookingsCollapsableMonthItem(
    val interval: Interval,
    val label: String,
    var collapsed: Boolean = true
) : BookingAdapterItem(BookingAdapterItemType.COLLAPSIBLE_MONTH) {
    override val key: Any
        get() = label
}