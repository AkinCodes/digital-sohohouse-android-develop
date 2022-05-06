package com.sohohouse.seven.common.views.amountinput

import org.junit.Assert.*
import org.junit.Test

class MoneyInputEvaluatorImplTest {

    private lateinit var cut: MoneyInputEvaluatorImpl

    @Test
    fun `test initial editing value`() {
        val currencyCode = "GBP"
        val initialValueCents = 500

        cut = MoneyInputEvaluatorImpl(initialValueCents, currencyCode)

        assertEquals(
            EditingValue(
                "5.00",
                isInitialValue = true
            ),
            cut.initialEditingValue()
        )
    }

    @Test
    fun `test amount cents`() {
        val currencyCode = "GBP"
        val initialValueCents = 500

        cut = MoneyInputEvaluatorImpl(initialValueCents, currencyCode)

        assertEquals(
            650,
            cut.amountCents(EditingValue("6.50"))
        )
    }

    @Test
    fun `test add currency symbol`() {
        val currencyCode = "GBP"
        val initialValueCents = 500

        cut = MoneyInputEvaluatorImpl(initialValueCents, currencyCode)

        assertTrue(
            cut.formatToCurrencyRepresentation(EditingValue("6.50")) in arrayOf(
                "£6.50",
                "GBP6.50"
            )
        )
    }

    @Test
    fun `test add currency symbol one decimal place`() {
        val currencyCode = "GBP"
        val initialValueCents = 500

        cut = MoneyInputEvaluatorImpl(initialValueCents, currencyCode)

        assertTrue(
            cut.formatToCurrencyRepresentation(EditingValue("6.5")) in arrayOf(
                "£6.50",
                "GBP6.50"
            )
        )
    }

    @Test
    fun `test add currency symbol with just decimal point`() {
        val currencyCode = "GBP"
        val initialValueCents = 500

        cut = MoneyInputEvaluatorImpl(initialValueCents, currencyCode)

        assertEquals(
            "£6.00",
            cut.formatToCurrencyRepresentation(EditingValue("6."))
        )
    }

//    @Test
//    fun `test format to dollars`() {
//        val currencyCode = "GBP"
//        val initialValueCents = 500
//
//        cut = MoneyInputEvaluatorImpl(initialValueCents, currencyCode)
//
//        assertTrue(
//            cut.formatToDollars(EditingValue("6.50")) in arrayOf(
//                "£6.50",
//                "GBP6.50"
//            )
//        )
//    }

}