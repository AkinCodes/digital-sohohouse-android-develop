package com.sohohouse.seven.common.views.amountinput

import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.formatWhole
import com.sohohouse.seven.common.utils.StringProvider

class TipStateEvaluator(
    initialAmountCents: Int,
    currencyCode: String,
    private val leftToPayCents: Int,
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
        currentValue = inputOperator.operate(currentValue).also {
            exceedsAmountLeftToPay = it.exceedsAmountLeftToPay()
        }.sanitise()

        return AmountInputState(
            primary = formatToCurrencyRepresentation(currentValue),
            secondary = null,
            error = getErrorText(exceedsAmountLeftToPay),
            minusEnabled = minusEnabled(),
            plusEnabled = true,
            confirmEnabled = true
        )
    }

    private fun getErrorText(exceedsAmountLeftToPay: Boolean): String? {
        return if (exceedsAmountLeftToPay) {
            stringProvider.getString(R.string.housepay_house_credit_reached_bill_total)
        } else {
            null
        }
    }

    private fun EditingValue.exceedsAmountLeftToPay(): Boolean {
        return amountCents(this) > leftToPayCents
    }

    private fun EditingValue.sanitise(): EditingValue {
        if (amountCents(this) > leftToPayCents) {
            return copy(stringValue = toAmountDollars(leftToPayCents).formatWhole())
        }
        return this
    }

    private fun minusEnabled(): Boolean {
        return currentValue.floatValue > 0
    }
}