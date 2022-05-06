package com.sohohouse.seven.common.views.amountinput

import org.junit.Assert.assertEquals
import org.junit.Test

class InputTest {
    @Test
    fun `test plus with floating point value`() {
        val input = EditingValue(
            "5.5"
        )
        val expectedOutput = EditingValue(
            "6.5"
        )
        assertEquals(expectedOutput, InputOperator.Plus(step = 1f).operate(input))
    }

    @Test
    fun `test plus with whole value`() {
        val input = EditingValue(
            "5"
        )
        val expectedOutput = EditingValue(
            "15"
        )
        assertEquals(expectedOutput, InputOperator.Plus(step = 10f).operate(input))
    }

    @Test
    fun `test minus with floating point value`() {
        val input = EditingValue(
            "5.5"
        )
        val expectedOutput = EditingValue(
            "4.5"
        )
        assertEquals(expectedOutput, InputOperator.Minus(step = 1f).operate(input))
    }

    @Test
    fun `test minus with whole value`() {
        val input = EditingValue(
            "5"
        )
        val expectedOutput = EditingValue(
            "4"
        )
        assertEquals(expectedOutput, InputOperator.Minus(step = 1f).operate(input))
    }

    @Test
    fun `test minus with zero`() {
        val input = EditingValue(
            "0"
        )
        val expectedOutput = EditingValue(
            "0"
        )
        assertEquals(expectedOutput, InputOperator.Minus(1f).operate(input))
    }

    @Test
    fun `test dot with whole value`() {
        val input = EditingValue(
            "5"
        )
        val expectedOutput = EditingValue(
            "5."
        )
        assertEquals(expectedOutput, InputOperator.Dot.operate(input))
    }

    @Test
    fun `test dot with floating point value`() {
        val input = EditingValue(
            "5."
        )
        val expectedOutput = EditingValue(
            "5."
        )
        assertEquals(expectedOutput, InputOperator.Dot.operate(input))
    }

    @Test
    fun `test dot with initial value`() {
        val input = EditingValue(
            "5",
            isInitialValue = true
        )
        val expectedOutput = EditingValue(
            "0."
        )
        assertEquals(expectedOutput, InputOperator.Dot.operate(input))
    }

    @Test
    fun `test backspace`() {
        val input = EditingValue(
            "55"
        )
        val expectedOutput = EditingValue(
            "5"
        )
        assertEquals(expectedOutput, InputOperator.Backspace.operate(input))
    }

    @Test
    fun `test backspace with single character`() {
        val input = EditingValue(
            "5"
        )
        val expectedOutput = EditingValue(
            "0"
        )
        assertEquals(expectedOutput, InputOperator.Backspace.operate(input))
    }

    @Test
    fun `test backspace with initial value`() {
        val input = EditingValue(
            "50",
            isInitialValue = true
        )
        val expectedOutput = EditingValue(
            "0"
        )
        assertEquals(expectedOutput, InputOperator.Backspace.operate(input))
    }

    @Test
    fun `test number`() {
        val input = EditingValue(
            "5"
        )
        val expectedOutput = EditingValue(
            "53"
        )
        assertEquals(expectedOutput, InputOperator.Number(3).operate(input))
    }

    @Test
    fun `test number with initial value`() {
        val input = EditingValue(
            "5",
            isInitialValue = true
        )
        val expectedOutput = EditingValue(
            "3"
        )
        assertEquals(expectedOutput, InputOperator.Number(3).operate(input))
    }
}