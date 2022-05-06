package com.sohohouse.seven.book

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.book.table.model.BookedTableDate
import junit.framework.TestCase.assertEquals
import org.joda.time.DateTime
import org.junit.Rule
import org.junit.Test
import java.util.*

class BookedTableDateTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `should be cancellable when now is 15 minutes before booking time`() {
        val now = Date()
        val bookedTableDate = BookedTableDate(
            Date(now.time + (15 * 60 * 1000L)),
            DateTime(Date(now.time + (15 * 60 * 1000L))),
            "UTC"
        )
        assertEquals(true, bookedTableDate.isCancellable())
    }

    @Test
    fun `should not be cancellable when now is 5 minutes before booking time`() {
        val now = Date()
        val bookedTableDate = BookedTableDate(
            Date(now.time + (5 * 60 * 1000L)),
            DateTime(now.time + (5 * 60 * 1000L)),
            "UTC"
        )
        assertEquals(false, bookedTableDate.isCancellable())
    }

    @Test
    fun `should be editable when now is 3 hours before booking time`() {
        val now = Date()
        val bookedTableDate = BookedTableDate(
            Date(now.time + (180 * 60 * 1000L)),
            DateTime(now.time + (180 * 60 * 1000L)),
            "UTC"
        )
        assertEquals(true, bookedTableDate.isEditable())
    }

    @Test
    fun `should not be editable when now is 1 hours before booking time`() {
        val now = Date()
        val bookedTableDate = BookedTableDate(
            Date(now.time + (60 * 60 * 1000L)),
            DateTime(now.time + (60 * 60 * 1000L)),
            "UTC"
        )
        assertEquals(false, bookedTableDate.isEditable())
    }

}