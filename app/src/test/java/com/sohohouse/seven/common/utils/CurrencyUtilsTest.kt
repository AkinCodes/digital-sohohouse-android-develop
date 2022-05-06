package com.sohohouse.seven.common.utils

import org.junit.Assert
import org.junit.Test

class CurrencyUtilsTest {

    @Test
    fun testEur() {
        CurrencyUtils.getFormattedPrice(345, "EUR").let {
            Assert.assertEquals("€3.45", it)
        }
    }

    @Test
    fun testGbp() {
        CurrencyUtils.getFormattedPrice(25, "GBP").let {
            Assert.assertEquals("£0.25", it)
        }
    }

    @Test
    fun testUsd() {
        CurrencyUtils.getFormattedPrice(7, "USD").let {
            //This method returns "US$" or "USD" depending on different jdk version (I assume)
            Assert.assertTrue(
                it in arrayOf(
                    "US$0.07",
                    "USD0.07"
                )
            )
        }
    }

    @Test
    fun testNoCurrency() {
        CurrencyUtils.getFormattedPrice(7500, showCurrency = false).let {
            Assert.assertEquals("75.00", it)
        }
    }

}