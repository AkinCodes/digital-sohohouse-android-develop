package com.sohohouse.seven.book.table

import com.sohohouse.seven.R
import java.util.*

object TableBookingErrorMapper {
    private const val ERROR_TABLES_INVALID_PHONE_NUMBER = "TABLES_INVALID_PHONE_NUMBER"
    private const val ERROR_TABLES_CONFLICTING_BOOKING = "TABLES_CONFLICTING_BOOKING"
    private const val ERROR_RESTAURANT_NOT_FOUND = "RESTAURANT_NOT_FOUND"
    private const val ERROR_BOOKING_PARTNER_ID_NOT_FOUND = "BOOKING_PARTNER_ID_NOT_FOUND"
    private const val ERROR_TABLES_LOCK_ERROR = "TABLES_LOCK_ERROR"
    private const val ERROR_TABLES_LOCK_NOT_FOUND = "TABLES_LOCK_NOT_FOUND"
    private const val ERROR_TABLES_INVALID_LOCK = "TABLES_INVALID_LOCK"
    private const val ERROR_TABLES_BOOKING_NOT_FOUND = "TABLES_BOOKING_NOT_FOUND"
    private const val ERROR_TABLES_CANCELLATION_FORBIDDEN = "TABLES_CANCELLATION_FORBIDDEN"
    private const val ERROR_TABLES_BOOKING_ALREADY_CANCELLED = "TABLES_BOOKING_ALREADY_CANCELLED"
    private const val ERROR_TABLES_ABOVE_MAX_SIZE = "TABLES_ABOVE_MAX_SIZE"


    fun handleError(code: String?): Int {
        return when (code?.toUpperCase(Locale.getDefault())) {
            ERROR_TABLES_CONFLICTING_BOOKING -> R.string.book_a_table_conflicting_booking
            ERROR_TABLES_INVALID_PHONE_NUMBER -> R.string.book_a_table_invalid_phone_number
            ERROR_TABLES_LOCK_ERROR -> R.string.book_a_table_no_availibility
            else -> R.string.book_a_table_default_error
        }
    }
}