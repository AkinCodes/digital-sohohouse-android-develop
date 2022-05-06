package com.sohohouse.seven.common.utils

import com.sohohouse.seven.common.extensions.isNotEmpty
import java.text.NumberFormat
import java.util.*
import kotlin.math.pow

class CurrencyUtils {
    companion object {
        const val BASE = 10.0
        fun getFormattedPrice(
            priceInCents: Int,
            currencyCode: String? = null,
            showCurrency: Boolean = currencyCode.isNotEmpty()
        ): String {
            if (currencyCode.isNullOrEmpty() || !showCurrency) {
                return "%.2f".format(priceInCents.toFloat() / 100F)
            }

            val currency = Currency.getInstance(currencyCode)
            val formatter = NumberFormat.getCurrencyInstance(Locale.UK)
            formatter.currency = currency
            val dollars: Float =
                priceInCents.toFloat() / BASE.pow(currency.defaultFractionDigits).toFloat()
            return formatter.format(dollars)
        }

        fun getCurrencySymbol(currencyCode: String): String {
            return Currency.getInstance(currencyCode).symbol
        }
    }
}