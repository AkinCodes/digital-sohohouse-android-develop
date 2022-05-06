package com.sohohouse.seven.common.views

enum class BookingState {
    CANCELLED,
    HELD,
    CONFIRMED,
    UNCONFIRMED,
    FAILED,
    EXPIRED,
    PENDING,
    WAITING;

    companion object {
        fun isPendingOrHeld(str: String?): Boolean {
            return str == PENDING.name || str == HELD.name
        }
    }
}

