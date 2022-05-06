package com.sohohouse.seven.more.bookings

import com.sohohouse.seven.R
import com.sohohouse.seven.common.error.ErrorReporter
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.utils.monthLabel
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.more.bookings.recycler.*
import com.sohohouse.seven.network.core.models.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Interval
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class BookingItemsFactory @Inject constructor(val stringProvider: StringProvider) {

    fun buildRoomBookingItem(booking: RoomBooking, venues: VenueList): RoomBookingAdapterItem {
        val venue =
            venues.findById(booking.hotel?.get(booking.document)?.venue?.get()?.id) ?: Venue()
        val startDate = booking.startsAt?.getFormattedDate(venue.timeZone) ?: ""
        val endDate = booking.endDate?.getFormattedDate(venue.timeZone) ?: ""
        val room = booking.room?.get(booking.document)
        val imageUrl = room?.roomImage?.mediumPng ?: ""
        val dates = "$startDate - $endDate"
        val statusLabel =
            if (booking.cancelled) stringProvider.getString(R.string.explore_events_booking_details_cancelled_label) else ""
        return RoomBookingAdapterItem(
            hotelName = venue.name, roomName = room?.name ?: "", imageUrl = imageUrl,
            roomBooking = booking, dateAndTime = dates, statusLabel = statusLabel
        )
    }

    fun buildTableBookingItem(reservation: TableReservation): TableBookingAdapterItem {
        val venue = reservation.venue
        venue
            ?: ErrorReporter.logException(Throwable("Missing venue data on table reservation ${reservation.id}"))
        return TableBookingAdapterItem(
            address = reservation.venue?.buildAddress(true) ?: "",
            dateTime = reservation.dateTime.getFormattedDateTime(""),
            imageUrl = reservation.restaurant?.getImage() ?: "",
            tableBookingId = reservation.id,
            venueName = venue?.restaurant?.bookingPartnerName ?: "",
            tableBooking = reservation
        )
    }

    fun buildEventBookingItem(
        booking: EventBooking,
        venues: VenueList,
        includeStatus: Boolean = true
    ): EventBookingAdapterItem {
        val event = booking.event ?: Event()
        val venue = venues.findById(booking.venue.id) ?: Venue()
        val dateTime = event.startsAt?.getFormattedDateTime(venue.timeZone) ?: ""
        val imageUrl = event.images?.large ?: ""
        return EventBookingAdapterItem(
            venue.name,
            event.name,
            dateTime,
            imageUrl,
            booking,
            includeStatus
        )
    }

    fun buildBookingItems(
        bookings: List<Booking>,
        venues: VenueList,
        includeEventBookingStatus: Boolean = true
    ): List<BookingAdapterItem> {
        return ArrayList<BookingAdapterItem>().apply {
            bookings.iterator().forEach {
                when (it) {
                    is EventBooking -> add(
                        buildEventBookingItem(
                            it,
                            venues,
                            includeEventBookingStatus
                        )
                    )
                    is RoomBooking -> add(buildRoomBookingItem(it, venues))
                    is TableReservation -> add(buildTableBookingItem(it))
                }
            }
        }
    }

    fun getLast12MonthsHeaders(): List<PastBookingsCollapsableMonthItem> {
        return ArrayList<PastBookingsCollapsableMonthItem>().apply {
            val now = DateTime.now(DateTimeZone.getDefault())
            for (i in 0 until 12) {
                val isThisMonth = i == 0
                val monthSnapshot = now.minusMonths(i)
                val monthLabel = mapToHeaderLabel(monthSnapshot)
                val dateRange = getDateRangeForMonth(monthSnapshot)
                add(
                    PastBookingsCollapsableMonthItem(
                        dateRange,
                        monthLabel,
                        collapsed = !isThisMonth
                    )
                )
            }
        }
    }

    private fun getDateRangeForMonth(monthSnapshot: DateTime): Interval {
        val isThisMonth = monthSnapshot.toDate().isThisMonth(monthSnapshot.zone.id)

        val startDate = monthSnapshot
            .withDayOfMonth(monthSnapshot.dayOfMonth().minimumValue)
            .withMinuteOfHour(monthSnapshot.minuteOfHour().minimumValue)
            .withHourOfDay(monthSnapshot.hourOfDay().minimumValue)
            .withSecondOfMinute(monthSnapshot.secondOfMinute().minimumValue)

        val endDate = if (isThisMonth) monthSnapshot else monthSnapshot
            .withDayOfMonth(monthSnapshot.dayOfMonth().maximumValue)
            .withMinuteOfHour(monthSnapshot.minuteOfHour().maximumValue)
            .withHourOfDay(monthSnapshot.hourOfDay().maximumValue)
            .withSecondOfMinute(monthSnapshot.secondOfMinute().maximumValue)


        return Interval(startDate.millis, endDate.millis)
    }

    private fun mapToHeaderLabel(date: DateTime): String {
        val includeYearLabel = !date.toDate().isThisYear(date.zone.id)

        return mapToMonthLabel(date) + if (includeYearLabel) " ${mapToYearLabel(date)}" else ""
    }

    private fun mapToMonthLabel(date: DateTime): String {
        return if (date.toDate()
                .isThisMonth(date.zone.id)
        ) stringProvider.getString(R.string.this_month) else date.monthLabel(stringProvider)
    }

    private fun mapToYearLabel(date: DateTime): String {
        return date.year.toString()
    }

}