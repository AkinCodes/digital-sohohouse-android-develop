package com.sohohouse.seven.common.views

import androidx.annotation.AttrRes
import com.sohohouse.seven.R
import com.sohohouse.seven.network.core.models.EventBooking

enum class UserBookingState(@AttrRes val colorAttr: Int) {
    GUEST_LIST(R.attr.colorEventRSVP),
    WAIT_LIST(R.attr.colorEventWaitingList),
    HELD(R.attr.colorEventHeld);

    companion object {
        fun getState(
            bookingType: BookingType?,
            bookingState: BookingState?
        ): UserBookingState? {
            return when (bookingState) {
                // soho wants us to handle unconfirmed and confirmed the same way, according to Sam @ Soho
                BookingState.CONFIRMED, BookingState.UNCONFIRMED -> {
                    when (bookingType) {
                        BookingType.WAITING_LIST -> WAIT_LIST
                        BookingType.GUEST_LIST -> GUEST_LIST
                        else -> null
                    }
                }
                BookingState.HELD, BookingState.PENDING -> HELD
                else -> null
            }
        }

        fun getState(booking: EventBooking?): UserBookingState? {
            return getState(
                booking?.bookingType?.let { BookingType.valueOf(it) },
                booking?.state?.let { BookingState.valueOf(it) })
        }
    }
}