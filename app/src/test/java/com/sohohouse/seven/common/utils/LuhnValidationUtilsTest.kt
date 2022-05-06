package com.sohohouse.seven.common.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LuhnValidationUtilsTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `Invalid Credit Card`() {
        assertFalse(LuhnValidationUtils.isValid("8273 1232 7352 0569"))
    }

    @Test
    fun `Valid Credit Card`() {
        assertTrue(LuhnValidationUtils.isValid("4276 3141 2925 0571"))
    }

    @Test
    fun `Valid Credit Card Number Without Space`() {
        assertTrue(LuhnValidationUtils.isValid("4276314129250571"))
    }

    @Test
    fun `Valid Credit Card Number With Space`() {
        assertTrue(LuhnValidationUtils.isValid("4276 3141 2925 0571"))
    }

    @Test
    fun `Valid Visa With Different Character Count`() {
        assertTrue(LuhnValidationUtils.isValid("4222 2222 2222 2"))
    }

    @Test
    fun `Valid Credit Card Number With Punctuation`() {
        assertTrue(LuhnValidationUtils.isValid("4276-3141-2925-0571"))
    }

    @Test
    fun `Valid American Express Credit Card Number`() {
        assertTrue(LuhnValidationUtils.isValid("378282246310005"))
    }

    @Test
    fun `Valid Diners Club Credit Card Number`() {
        assertTrue(LuhnValidationUtils.isValid("30569309025904"))
    }

    @Test
    fun `Valid Discover Credit Card Number`() {
        assertTrue(LuhnValidationUtils.isValid("6011111111111117"))
    }

}