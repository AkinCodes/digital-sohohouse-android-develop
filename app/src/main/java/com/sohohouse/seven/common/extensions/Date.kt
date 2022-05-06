package com.sohohouse.seven.common.extensions

import android.text.format.DateFormat
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone


fun Date.getApiFormattedDate(): String {
    val pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS"
    val formatter = SimpleDateFormat(pattern, Locale.UK)
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(this)
}

fun Date.getApiFormattedDateIgnoreTimezone(): String {
    val pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS"
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(this)
}

fun Date.getFilterApiFormattedDate(): String {
    val pattern = "yyyy-MM-dd"
    return SimpleDateFormat(pattern, Locale.UK)
        .format(this)
}

fun Date.getFormattedDate(zoneId: String? = null): String {
    val formatter =
        SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.UK, "dMMMyyyy"), Locale.UK)
    if (!zoneId.isStringEmpty()) {
        formatter.timeZone = TimeZone.getTimeZone(zoneId)
    }
    return formatter.format(this)
}

fun Date.getDayAndMonthFormattedDate(zoneId: String? = null): String {
    val formatter =
        SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.UK, "d MMMM"), Locale.UK)
    if (!zoneId.isStringEmpty()) {
        formatter.timeZone = TimeZone.getTimeZone(zoneId)
    }
    return formatter.format(this)
}

fun Date.getFormattedDayOfWeekDayMonth(zoneId: String? = null): String {
    val formatter =
        SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.UK, "EEEdMMM"), Locale.UK)
    if (!zoneId.isStringEmpty()) {
        formatter.timeZone = TimeZone.getTimeZone(zoneId)
    }
    return formatter.format(this)
}

fun Date.getFormattedDateTime(zoneId: String?): String {
    val dateFormat = DateFormat.getBestDateTimePattern(Locale.UK, "EEEdMMMhmma")
    val formatter = SimpleDateFormat(dateFormat, Locale.UK)
    if (!zoneId.isStringEmpty()) {
        formatter.timeZone = TimeZone.getTimeZone(zoneId)
    }
    return formatter.format(this)
}

fun Date.getFormattedMonth(zoneId: String?): String {
    val dateFormat = DateFormat.getBestDateTimePattern(Locale.UK, "yMMMM")
    val formatter = SimpleDateFormat(dateFormat, Locale.UK)
    if (!zoneId.isStringEmpty()) {
        formatter.timeZone = TimeZone.getTimeZone(zoneId)
    }
    return formatter.format(this)
}

fun Date.getFormattedTime(zoneId: String?): String {
    val dateFormat = DateFormat.getBestDateTimePattern(Locale.UK, "hmma")
    val formatter = SimpleDateFormat(dateFormat, Locale.UK)
    if (!zoneId.isStringEmpty()) {
        formatter.timeZone = TimeZone.getTimeZone(zoneId)
    }
    return formatter.format(this)
}

fun Date.isSameDay(date: Date, zoneId: String?): Boolean {
    return getFormattedDate(zoneId) == date.getFormattedDate(zoneId)
}

fun Date.isThisWeek(): Boolean {
    val zone = DateTimeZone.forID(timeZoneId)
    val dateTime = DateTime(this, zone)
    val now = DateTime.now(zone)
    return (dateTime.weekOfWeekyear == now.weekOfWeekyear && dateTime.year == now.year)
}

fun Date.isNextWeek(): Boolean {
    val zone = DateTimeZone.forID(timeZoneId)
    val dateTime = DateTime(this, zone)
    val now = DateTime.now(zone)
    return (dateTime.weekOfWeekyear == now.weekOfWeekyear + 1 && dateTime.year == now.year)
}

fun Date.isThisMonth(zoneId: String?): Boolean {
    val zone = DateTimeZone.forID(zoneId)
    val dateTime = DateTime(this, zone)
    val now = DateTime.now(zone)
    return (dateTime.monthOfYear == now.monthOfYear && dateTime.year == now.year)
}

fun Date.isThisYear(zoneId: String?): Boolean {
    val zone = DateTimeZone.forID(zoneId)
    val dateTime = DateTime(this, zone)
    val now = DateTime.now(zone)
    return (dateTime.year == now.year)
}

val Date.timeZoneId get() = DateTime(this).zone.id

fun Date.isToday(): Boolean {
    return DateUtils.isToday(this.time)
}

fun Date.isBetween(start: Date?, end: Date?): Boolean {
    if (start == null || end == null) return false

    return with(Date()) { after(start) && before(end) }
}

val Date.yearMonthDay: Triple<Int, Int, Int>
    get() = Calendar.getInstance().apply { time = this@yearMonthDay }.let {
        Triple(
            it.get(Calendar.YEAR),
            it.get(Calendar.MONTH),
            it.get(Calendar.DAY_OF_MONTH)
        )
    }

val Date.hourMinute: Pair<Int, Int>
    get() = Calendar.getInstance().apply {
        time = this@hourMinute
    }.let {
        Pair(
            it.get(Calendar.HOUR_OF_DAY),
            it.get(Calendar.MINUTE)
        )
    }

fun Date.dateToString(): String {
    val pattern = "yyyy-MM-dd hh:mm:ss.sss"
    //simple date formatter
    val dateFormatter = SimpleDateFormat(pattern, Locale.getDefault())
    //return the formatted date string
    return dateFormatter.format(this)
}