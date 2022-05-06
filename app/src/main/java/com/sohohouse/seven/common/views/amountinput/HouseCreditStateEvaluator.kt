package com.sohohouse.seven.common.views.amountinput

import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.formatWhole
import com.sohohouse.seven.common.utils.CurrencyUtils
import com.sohohouse.seven.common.utils.StringProvider

class HouseCreditStateEvaluator(
    initialAmountCents: Int,
    private val creditAvailableCents: Int,
    private val leftToPayCents: Int,
    private val currencyCode: String,
    private val stringProvider: StringProvider
) : InputStateEvaluator,
    MoneyInputEvaluator by MoneyInputEvaluatorImpl(
        initialAmountCents,
        currencyCode
    ) {

    private var currentValue: EditingValue = initialEditingValue()

    override val amountCents: Int
        get() = amountCents(currentValue)

    override fun evaluate(inputOperator: InputOperator): AmountInputState {
        var exceedsAmountLeftToPay: Boolean
        currentValue = inputOperator.operate(currentValue)
            .also { exceedsAmountLeftToPay = it.exceedsAmountLeftToPay() }
            .sanitise()

        return AmountInputState(
            primary = formatToCurrencyRepresentation(currentValue),
            secondary = getSecondaryText(),
            error = getErrorText(exceedsAmountLeftToPay),
            minusEnabled = minusEnabled(),
            plusEnabled = plusEnabled(),
            confirmEnabled = confirmEnabled()
        )
    }

    private fun getErrorText(exceedsMax: Boolean): String? {
        return if (exceedsMax) {
            stringProvider.getString(R.string.housepay_house_credit_reached_bill_total)
        } else {
            null
        }
    }

    private fun EditingValue.exceedsAmountLeftToPay(): Boolean {
        return amountCents(this) > leftToPayCents
    }

    private fun EditingValue.sanitise(): EditingValue {
        val max = getMax()
        if (amountCents(this) > max) {
            return copy(stringValue = toAmountDollars(max).formatWhole())
        }
        return this
    }

    private fun getMax() = minOf(leftToPayCents, creditAvailableCents)

    private fun getSecondaryText(): String {
        val remainingCents = creditAvailableCents - amountCents
        val remainingCreditFormatted = CurrencyUtils.getFormattedPrice(
            remainingCents,
            currencyCode,
            showCurrency = false
        )
        return stringProvider.getString(
            R.string.housepay_remaining_credit,
            remainingCreditFormatted
        )
    }

    private fun confirmEnabled(): Boolean {
        return amountCents <= creditAvailableCents
    }

    private fun plusEnabled(): Boolean {
        return amountCents < creditAvailableCents
    }

    private fun minusEnabled(): Boolean {
        return currentValue.floatValue > 0
    }
}