package com.sohohouse.seven.book.table.model

import com.sohohouse.seven.common.extensions.getFormattedDateTime
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Period
import java.io.Serializable
import java.util.*

class BookedTableDate(
    val date: Date,
    private val dateTime: DateTime,
    private val venueTimeZone: String
) : Serializable {

    private fun now(): DateTime {
        return DateTime.now(DateTimeZone.forTimeZone(TimeZone.getTimeZone(venueTimeZone)))
    }

    fun isCancellable(): Boolean {
        return Period(now(), dateTime).minutes > 10
    }

    fun isEditable(): Boolean {
        return Period(now(), dateTime).hours >= 2
    }

    fun getFormattedDateTime(zoneId: String?) = date.getFormattedDateTime(zoneId)

}