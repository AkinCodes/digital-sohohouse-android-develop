package com.sohohouse.seven.book.table

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.sohohouse.seven.R
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.network.core.models.TABLE_BOOKINGS_DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.*

object TableBookingUtil {

    const val TIME_SLOTS_SPAN_COUNT = 4

    val DATE_FORMATTER get() = SimpleDateFormat(TABLE_BOOKINGS_DATE_FORMAT, Locale.getDefault())

    fun createErrorDialog(
        context: Context,
        errorMessageResId: Int,
        onShowContactUsClick: () -> Unit
    ): AlertDialog {
        return CustomDialogFactory.createThemedAlertDialog(context = context,
            title = context.getString(R.string.book_a_table_default_error),
            message = context.getString(errorMessageResId),
            positiveButtonText = context.getString(R.string.book_a_table_error_ok_cta),
            negativeButtonText = context.getString(R.string.book_a_table_contact_us),
            positiveClickListener = { _, _ -> },
            negativeClickListener = { _, _ -> onShowContactUsClick() }
        )
    }

}