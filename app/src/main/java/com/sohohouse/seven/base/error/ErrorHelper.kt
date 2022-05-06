package com.sohohouse.seven.base.error

import android.content.Context
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.nullIfBlank
import com.sohohouse.seven.common.utils.StringProvider

object ErrorHelper {

    const val ERROR_LOADING_SHARED_PROFILE = "ERROR_LOADING_SHARED_PROFILE"
    const val EMPTY_PROFILE = "EMPTY_PROFILE"
    const val FILE_CREATE_ERROR = "FILE_CREATE_ERROR"


    val errorCodeMap = hashMapOf(
        "event_double_booked" to R.string.error_event_double_booked,
        "event_unallocatable" to R.string.error_event_unallocatable,
        "booking_max_no_of_guests_exceeded" to R.string.error_booking_max_no_of_guests_exceeded,
        "event_not_open" to R.string.error_event_not_open,
        "manager_booking_error" to R.string.error_manager_booking_error,
        "booking_already_cancelled" to R.string.error_booking_already_cancelled,
        "booking_cancellation_error" to R.string.error_booking_cancellation_error,
        "booking_cancellation_policy_error" to R.string.error_booking_cancellation_policy_error,
        "FAILED_PAYMENT" to R.string.error_failed_payment,
        "FAILED_PAYMENT_NO_CHARGE" to R.string.error_failed_payment_no_charge,
        "INVALID_CARD" to R.string.error_invalid_card,
        "FORM_EXPIRED" to R.string.error_form_expired,
        "PRIMARY_CANNOT_BE_EXPIRED" to R.string.error_primary_cannot_be_expired,
        "VENUE_NOT_OPEN" to R.string.error_venue_not_open,
        "STATUS_OBSCENE" to R.string.error_status_obscene,
        "UPDATE_FAILED" to R.string.error_update_failed,
        "INVALID_CONTENT_TYPE" to R.string.error_invalid_content_type,
        "OBSCENE_BIO" to R.string.error_obscene_bio,
        "BIO_TOO_LONG" to R.string.error_bio_too_long,
        "INVALID_WEBSITE" to R.string.error_invalid_website,
        "INVALID_LINKEDIN_URL" to R.string.error_invalid_linkedin_url,
        "INVALID_SPOTIFY_URL" to R.string.error_invalid_spotify_url,
        "INVALID_YOUTUBE_URL" to R.string.error_invalid_youtube_url,
        "WEBSITE_TOO_LONG" to R.string.error_website_too_long,
        "ASK_ME_ABOUT_TOO_LONG" to R.string.error_ask_me_about_too_long,
        "INVALID_INTERESTS" to R.string.error_invalid_interests,
        "TOO_MANY_INTERESTS" to R.string.error_too_many_interests,
        "ACCOUNT_NOT_FOUND" to R.string.error_account_not_found,
        "INVALID_PHONE_NUMBER" to R.string.error_invalid_phone_number,
        "OBSCENE_ASK_ME_ABOUT" to R.string.error_obscene_bio,
        FILE_CREATE_ERROR to R.string.error_cant_create_temp_file,
        "OBSCENE_ASK_ME_ABOUT" to R.string.error_obscene_bio,
        ERROR_LOADING_SHARED_PROFILE to R.string.error_loading_shared_profile,
        EMPTY_PROFILE to R.string.empty_shared_profile
    )

    fun getErrorMessage(
        errorCodes: Array<out String>,
        stringProvider: StringProvider
    ): DisplayableError {
        val title = stringProvider.getString(R.string.general_error_header)

        val message = StringBuilder().apply {
            errorCodes.forEach { key ->
                errorCodeMap[key]?.let { append("${stringProvider.getString(it)}\n") }
            }
        }.toString()
            .nullIfBlank()
            ?: stringProvider.getString(R.string.error_general)

        return DisplayableError(title, message)
    }

}

data class DisplayableError(
    val title: String,
    val message: String
)