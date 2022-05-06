package com.sohohouse.seven.common.views.amountinput

import com.sohohouse.seven.common.utils.EmptyStringProvider
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test

class HouseCreditStateEvaluatorTest {
    @Test
    fun testHouseCreditStateEvaluator() {
        val cut = HouseCreditStateEvaluator(
            initialAmountCents = 500,
            currencyCode = "GBP",
            creditAvailableCents = 50000,
            leftToPayCents = 100000,
            stringProvider = EmptyStringProvider()
        )

        val actions = listOf(
            InputOperator.None to "£5.00",
            InputOperator.Number(1) to "£1.00",
            InputOperator.Dot to "£1.00",
            InputOperator.Plus(10f) to "£11.00",
            InputOperator.Number(5) to "£115.00"
        )

        var result: AmountInputState
        actions.forEach { (action, expected) ->
            result = cut.evaluate(action)

            assertEquals(expected, result.primary)
        }

    }
}