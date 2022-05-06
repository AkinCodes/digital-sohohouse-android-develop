package com.sohohouse.seven.common.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class QuadrupleTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun quadrupleDestructing() {
        val (a, b, c, d) = Quadruple(2, "x", true, listOf(null))
        Assert.assertEquals(2, a)
        Assert.assertEquals("x", b)
        Assert.assertTrue(c)
        Assert.assertEquals(listOf(null), d)
    }

    @Test
    fun quadrupleToList() {
        val mixedList: List<Any> = Quadruple(1, "a", 0.5, false).toList()
        Assert.assertTrue(mixedList[0] is Int)
        Assert.assertTrue(mixedList[1] is String)
        Assert.assertTrue(mixedList[2] is Double)
        Assert.assertTrue(mixedList[3] is Boolean)
    }

    @Test
    fun quadrupleToString() {
        val mixedList: List<Any> = Quadruple(1, "a", 0.5, false).toList()
        Assert.assertEquals("[1, a, 0.5, false]", mixedList.toString())

        val intList: List<Int> = Quadruple(0, 1, 2, 3).toList()
        Assert.assertEquals("[0, 1, 2, 3]", intList.toString())
    }
}