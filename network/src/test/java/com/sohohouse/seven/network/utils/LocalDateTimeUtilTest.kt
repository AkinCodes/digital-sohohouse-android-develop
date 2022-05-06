package com.sohohouse.seven.network.utils

import com.sohohouse.seven.network.utils.LocalDateTimeUtil.getNext15MinuteIntervalTime
import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.LocalDateTime

class LocalDateTimeUtilTest {

    @Test
    fun `test get next 15 minute interval time`() {

        with (LocalDateTime.now().withHour(0).withMinute(0)) {
            assertEquals(0, hour)
            assertEquals(0, minute)
        }

        with (getNext15MinuteIntervalTime(LocalDateTime.now().withHour(0).withMinute(10))) {
            assertEquals(0, hour)
            assertEquals(15, minute)
        }

        with (getNext15MinuteIntervalTime(LocalDateTime.now().withHour(0).withMinute(15))) {
            assertEquals(0, hour)
            assertEquals(15, minute)
        }

        with (getNext15MinuteIntervalTime(LocalDateTime.now().withHour(0).withMinute(20))) {
            assertEquals(0, hour)
            assertEquals(30, minute)
        }

        with (getNext15MinuteIntervalTime(LocalDateTime.now().withHour(0).withMinute(30))) {
            assertEquals(0, hour)
            assertEquals(30, minute)
        }

        with (getNext15MinuteIntervalTime(LocalDateTime.now().withHour(0).withMinute(40))) {
            assertEquals(0, hour)
            assertEquals(45, minute)
        }

        with (getNext15MinuteIntervalTime(LocalDateTime.now().withHour(0).withMinute(45))) {
            assertEquals(0, hour)
            assertEquals(45, minute)
        }

        with (getNext15MinuteIntervalTime(LocalDateTime.now().withHour(0).withMinute(50))) {
            assertEquals( 1, hour)
            assertEquals(0, minute)
        }

        with (getNext15MinuteIntervalTime(LocalDateTime.now().withHour(23).withMinute(50))) {
            assertEquals( 23, hour)
            assertEquals(45, minute)
        }

    }

}