package com.sohohouse.seven.base.error

import android.content.Context
import com.sohohouse.seven.R
import com.sohohouse.seven.base.error.ErrorHelper.errorCodeMap
import com.sohohouse.seven.common.extensions.nullIfBlank
import com.sohohouse.seven.common.views.CustomDialogFactory

object ErrorDialogHelper {

    fun showErrorDialogByErrorCode(context: Context, keys: Array<out String> = emptyArray()) {
        val displayMsg = StringBuilder().apply {
            keys.forEach { key ->
                errorCodeMap[key]?.let { append("${context.getString(it)}\n") }
            }
        }.toString().nullIfBlank()

        CustomDialogFactory.createThemedAlertDialog(
            context,
            context.getString(R.string.general_error_header),
            displayMsg ?: context.getString(R.string.error_general),
            context.getString(R.string.general_error_ok_cta)
        ).show()
    }

    fun showGenericErrorDialog(context: Context, message: String? = null) {
        CustomDialogFactory.createThemedAlertDialog(
            context,
            context.getString(R.string.general_error_header),
            message ?: context.getString(R.string.error_general),
            context.getString(R.string.general_error_ok_cta)
        ).show()
    }

    fun showNetworkErrorDialog(context: Context) {
        CustomDialogFactory.createThemedAlertDialog(
            context,
            context.getString(R.string.internet_error_header),
            context.getString(R.string.internet_error_supporting),
            context.getString(R.string.internet_error_cta)
        ).show()
    }
}