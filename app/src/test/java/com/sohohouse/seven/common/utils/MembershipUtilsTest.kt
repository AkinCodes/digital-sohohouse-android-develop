package com.sohohouse.seven.common.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class MembershipUtilsTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `Format mambership number of empty`() {
        val formatted = MembershipUtils.formatMembershipNumber("")
        assertTrue(formatted == "")
    }

    @Test
    fun `Format mambership number of less than 8 characters in length`() {
        val formatted = MembershipUtils.formatMembershipNumber("1234567")
        assertTrue(formatted == "1234567")
    }

    @Test
    fun `Format mambership number of 8 characters in length`() {
        val formatted = MembershipUtils.formatMembershipNumber("12345678")
        assertTrue(formatted == "12 345 678")
    }

    @Test
    fun `Format mambership number of 9 characters in length`() {
        val formatted = MembershipUtils.formatMembershipNumber("123456789")
        assertTrue(formatted == "123 456 789")
    }

    @Test
    fun `Format mambership number of 10 characters in length`() {
        val formatted = MembershipUtils.formatMembershipNumber("1234567890")
        assertTrue(formatted == "1234 567 890")
    }

    @Test
    fun `Format mambership number of 15 characters in length`() {
        val formatted = MembershipUtils.formatMembershipNumber("123456789012345")
        assertTrue(formatted == "123456789 012 345")
    }
}