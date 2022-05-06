package com.sohohouse.seven.book.eventdetails.bookingsuccess

import com.sohohouse.seven.common.views.UserBookingState
import java.io.Serializable
import java.util.*

class BookingSuccessItem(
    val bookingState: UserBookingState,
    val eventDate: Date? = null,
    val timeZone: String? = null,
    val eventId: String,
    val eventName: String,
    val eventImageUrl: String?,
    val venueName: String,
    val venueColor: String,
    val maxGuest: Int = 0,
    val guestCount: Int = 0,
    val isInduction: Boolean = false,
    val isPendingLotteryState: Boolean = false,
    val isTicketless: Boolean = false,
    val eventType: String? = null,
    val isDigitalEvent: Boolean = false
) : Serializable
