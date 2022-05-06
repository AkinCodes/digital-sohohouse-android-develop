package com.sohohouse.seven.common.views.amountinput

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import com.sohohouse.seven.R
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.views.amountinput.AmountInputKey.*
import java.lang.IllegalArgumentException

sealed class AmountInputMode {

    data class Tips(
        val initialAmountCents: Int,
        val currencyCode: String,
        val leftToPayCents: Int
    ) : AmountInputMode()

    class Credits(
        val initialAmountCents: Int,
        val creditAvailableCents: Int,
        val leftToPayCents: Int,
        val currencyCode: String
    ) : AmountInputMode()

    @StringRes
    fun title(): Int {
        return when (this) {
            is Credits -> R.string.housepay_house_credit_title
            is Tips -> R.string.housepay_tips_input_title
        }
    }

    fun evaluator(stringProvider: StringProvider): InputStateEvaluator {
        return when (this) {
            is Credits -> HouseCreditStateEvaluator(
                initialAmountCents,
                creditAvailableCents,
                leftToPayCents,
                currencyCode,
                stringProvider
            )
            is Tips -> TipStateEvaluator(
                initialAmountCents,
                currencyCode,
                leftToPayCents,
                stringProvider
            )
        }
    }

    fun toBundle(): Bundle {
        return when (this) {
            is Credits -> bundleOf(
                BundleKeys.AMOUNT_INPUT_MODE to Credits::class.java.simpleName,
                BundleKeys.CURRENCY_CODE to currencyCode,
                BundleKeys.INITIAL_VALUE to initialAmountCents,
                BundleKeys.CREDIT_AVAILABLE to creditAvailableCents,
                BundleKeys.LEFT_TO_PAY to leftToPayCents
            )
            is Tips -> bundleOf(
                BundleKeys.AMOUNT_INPUT_MODE to Tips::class.java.simpleName,
                BundleKeys.CURRENCY_CODE to currencyCode,
                BundleKeys.INITIAL_VALUE to initialAmountCents,
                BundleKeys.LEFT_TO_PAY to leftToPayCents
            )
        }
    }

    fun operatorFor(key: AmountInputKey): InputOperator {
        return when (key) {
            MINUS -> InputOperator.Minus(incrementStepDollars)
            PLUS -> InputOperator.Plus(incrementStepDollars)
            ONE -> InputOperator.Number(1)
            TWO -> InputOperator.Number(2)
            THREE -> InputOperator.Number(3)
            FOUR -> InputOperator.Number(4)
            FIVE -> InputOperator.Number(5)
            SIX -> InputOperator.Number(6)
            SEVEN -> InputOperator.Number(7)
            EIGHT -> InputOperator.Number(8)
            NINE -> InputOperator.Number(9)
            DOT -> InputOperator.Dot
            ZERO -> InputOperator.Number(0)
            UNDO -> InputOperator.Backspace
        }
    }

    private val incrementStepDollars: Float
        get() = when (this) {
            is Credits -> 10f
            is Tips -> 1f
        }

    companion object {
        fun fromBundle(bundle: Bundle): AmountInputMode {
            return when (bundle.getString(BundleKeys.AMOUNT_INPUT_MODE)) {
                Tips::class.java.simpleName -> {
                    Tips(
                        initialAmountCents = bundle.getInt(BundleKeys.INITIAL_VALUE),
                        currencyCode = bundle.getString(BundleKeys.CURRENCY_CODE) ?: "",
                        leftToPayCents = bundle.getInt(BundleKeys.LEFT_TO_PAY)
                    )
                }
                Credits::class.java.simpleName -> {
                    Credits(
                        initialAmountCents = bundle.getInt(BundleKeys.INITIAL_VALUE),
                        currencyCode = bundle.getString(BundleKeys.CURRENCY_CODE) ?: "",
                        creditAvailableCents = bundle.getInt(BundleKeys.CREDIT_AVAILABLE),
                        leftToPayCents = bundle.getInt(BundleKeys.LEFT_TO_PAY)
                    )
                }
                else -> throw IllegalArgumentException("No matching input mode")
            }
        }
    }
}