package com.sohohouse.seven.network.chat

import com.sohohouse.seven.network.chat.model.channel.OneToOneChatChannel
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.junit.Assert
import org.junit.Test

class LastMessageTimeTest {

    @Test
    fun `when messageTime is today print HH and mm`() {
        val localDate = LocalDate()
        val messageTime = OneToOneChatChannel.LastMessageTime(
            DateTime(localDate.year, localDate.monthOfYear, localDate.dayOfMonth, 10, 10)
        )

        Assert.assertEquals("10:10", messageTime.toString())
    }

    @Test
    fun `when messageTime is in past print dd mm yyyy`() {
        val localDate = LocalDate().minusDays(1)
        val messageTime = OneToOneChatChannel.LastMessageTime(
            DateTime(localDate.year, localDate.monthOfYear, localDate.dayOfMonth, 10, 10)
        )

        Assert.assertEquals(
            "${localDate.dayOfMonth.toString().padStart(2,'0')
            }/${localDate.monthOfYear.toString().padStart(2, '0')
            }/${localDate.year}",
            messageTime.toString())
    }

}