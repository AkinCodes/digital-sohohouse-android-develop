package com.sohohouse.seven.common.extensions

import com.sohohouse.seven.common.views.BookingState
import com.sohohouse.seven.common.views.BookingType
import com.sohohouse.seven.common.views.EventBookableStatus
import com.sohohouse.seven.common.views.EventState
import com.sohohouse.seven.network.core.models.Event
import java.util.*

fun Event.isPendingLotteryState(): Boolean {
    return this.hasLottery && !this.lotteryDrawn
}

fun Event.isBookable(): Boolean {
    if (isTicketless && state == EventState.CLOSED_FOR_BOOKING.name) {
        return false
    }
    return bookable == EventBookableStatus.IS_BOOKABLE.name
}

fun Event.isBooked(): Boolean {
    return isConfirmed() || isPending() || isOnWaitinglist()
}

fun Event.isConfirmed(): Boolean {
    if (state.isNullOrEmpty()) return false

    if (BookingType.WAITING_LIST.name == booking?.get(document)?.bookingType) return false

    return BookingState.CONFIRMED.name == state || BookingState.UNCONFIRMED.name == state
}

fun Event.isPending(): Boolean {
    return BookingState.HELD.name == state || BookingState.PENDING.name == state
}

fun Event.isOnWaitinglist(): Boolean {
    if (state.isNullOrEmpty()) return false

    if (BookingState.WAITING.name == state) return true

    if (BookingType.WAITING_LIST.name == booking?.get(document)?.bookingType
        && (BookingState.CONFIRMED.name == state || BookingState.UNCONFIRMED.name == state)
    ) return true

    return false
}

fun Event.isPastEvent(): Boolean {
    val endsAt = this.endsAt ?: return false
    return endsAt.before(Date())
}

val Event.isDigitalEvent
    get() = digitalInfo != null

fun Event.isHappeningNow(): Boolean {
    return Date().isBetween(startsAt, endsAt)
}

fun Event.isStartingSoon(): Boolean {
    return startsAt?.let { startsAt ->
        val calendar = Calendar.getInstance()
        calendar.time = startsAt
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) - 1)

        Date().isBetween(calendar.time, startsAt)
    } ?: false
}

