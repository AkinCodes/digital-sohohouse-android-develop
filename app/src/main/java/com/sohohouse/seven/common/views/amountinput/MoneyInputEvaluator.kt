package com.sohohouse.seven.common.views.amountinput

import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.utils.CurrencyUtils
import kotlin.math.roundToInt

interface MoneyInputEvaluator {
    fun initialEditingValue(): EditingValue
    fun amountCents(currentValue: EditingValue): Int
    fun formatToCurrencyRepresentation(currentValue: EditingValue): String
    fun toAmountDollars(amountCents: Int): Float
}

class MoneyInputEvaluatorImpl(
    private val initialAmountCents: Int,
    private val currencyCode: String
) : MoneyInputEvaluator {

    override fun initialEditingValue(): EditingValue {
        return EditingValue(
            toAmountDollars(initialAmountCents).formatToTwoDecimalPlaces(),
            isInitialValue = true
        )
    }

    override fun amountCents(currentValue: EditingValue): Int {
        return (currentValue.floatValue * 100).roundToInt()
    }

    override fun formatToCurrencyRepresentation(currentValue: EditingValue): String {
        return addCurrencySymbol(
            currencyCode,
            currentValue.stringValue.toFloat().formatToTwoDecimalPlaces()
        )
    }

    override fun toAmountDollars(amountCents: Int) = (amountCents / 100f)

    private fun addCurrencySymbol(
        currencyCode: String,
        value: String
    ): String {
        return value.prepend(CurrencyUtils.getCurrencySymbol(currencyCode))
    }
}
