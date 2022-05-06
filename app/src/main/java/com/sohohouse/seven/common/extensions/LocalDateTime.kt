package com.sohohouse.seven.common.extensions

import android.content.Context
import android.text.format.DateUtils
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.WeekFields
import java.util.*

fun LocalDateTime.isToday(): Boolean {
    val now = LocalDateTime.now()
    return this.year == now.year && this.dayOfYear == now.dayOfYear
}

fun LocalDateTime.isThisWeek(): Boolean {
    val now = LocalDateTime.now()
    return this.year == now.year && this.getWeekNumber() == now.getWeekNumber()
}

fun LocalDateTime.isNextWeek(): Boolean {
    val now = LocalDateTime.now()
    return this.year == now.year && this.getWeekNumber() == now.plusWeeks(1).getWeekNumber()
}

fun LocalDateTime.getWeekNumber(): Int {
    val weekFields = WeekFields.of(Locale.getDefault())
    return this.get(weekFields.weekOfYear())
}

fun LocalDateTime.format(context: Context, flags: Int): String {
    val long = this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    return DateUtils.formatDateTime(context, long, flags)
}

fun LocalDateTime.toDate(): Date {
    return DateTimeUtils.toDate(atZone(ZoneId.systemDefault()).toInstant())
}

val LocalDateTime.zeroIndexedMonth: Int
    get() = month.ordinal
