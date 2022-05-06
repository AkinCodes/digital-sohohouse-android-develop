package com.sohohouse.seven.common.utils

import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.isEmpty
import com.sohohouse.seven.common.extensions.replaceBraces
import com.sohohouse.seven.network.core.models.Day
import com.sohohouse.seven.network.core.models.Day.*
import com.sohohouse.seven.network.core.models.OperatingHours
import com.sohohouse.seven.network.core.models.Venue
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.threeten.bp.DayOfWeek
import org.threeten.bp.format.TextStyle
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {

    fun reformatOperatingHoursString(
        time: String,
        resultDateFormatPattern: String = "h:mma"
    ): String {
        val inputTime = SimpleDateFormat("HHmm", Locale.UK).parse(time)
        return SimpleDateFormat(resultDateFormatPattern, Locale.UK).format(inputTime)
    }

    fun isExpiryDateStringValid(time: String): Boolean {
        return try {
            val inputCal = Calendar.getInstance()
            val formatter = SimpleDateFormat("MMyy", Locale.UK)
            formatter.isLenient = false
            inputCal.time = formatter.parse(time)
            val currentCal = Calendar.getInstance()
            return when {
                inputCal.get(Calendar.YEAR) > currentCal.get(Calendar.YEAR) -> true
                inputCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR) -> inputCal.get(
                    Calendar.MONTH
                ) >= currentCal.get(Calendar.MONTH)
                else -> false
            }
        } catch (e: ParseException) {
            false
        }
    }

    fun isVenueOpen(
        operatingHours: OperatingHours,
        zoneId: String,
        currentTime: Calendar = Calendar.getInstance()
    ): Boolean {
        currentTime.timeZone = TimeZone.getTimeZone(zoneId)
        // have to switch index here because day of week for Calendar and model's period are different
        // Calendar: Sun - 1, Mon - 2 ... Sat - 7
        // Period: Sun - 0, Mon - 1 ... Sat - 6
        val currentDay = currentTime.get(Calendar.DAY_OF_WEEK) - 1
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val currentMin = currentTime.get(Calendar.MINUTE)
        operatingHours.periods?.let {
            for (period in it) {
                val openDay = period.venueOpen._day
                val openTime = Calendar.getInstance()
                openTime.time = SimpleDateFormat("HHmm", Locale.UK).parse(period.venueOpen.time)
                val openHour = openTime.get(Calendar.HOUR_OF_DAY)
                val openMinute = openTime.get(Calendar.MINUTE)

                val closeDay = period.venueClose._day
                val closeTime = Calendar.getInstance()
                closeTime.time = SimpleDateFormat("HHmm", Locale.UK).parse(period.venueClose.time)
                val closeHour = closeTime.get(Calendar.HOUR_OF_DAY)
                val closeMinute = closeTime.get(Calendar.MINUTE)

                val isCrossWeek = openDay == 6 && closeDay == 0

                if ((currentDay > openDay || currentDay == 0 && isCrossWeek // sunday to monday interval
                            || (currentDay == openDay && currentHour > openHour)
                            || (currentDay == openDay && currentHour == openHour && currentMin >= openMinute))
                    && (currentDay < closeDay || currentDay == 6 && isCrossWeek // sunday to monday interval
                            || (currentDay == closeDay && currentHour < closeHour)
                            || (currentDay == closeDay && currentHour == closeHour && currentMin <= closeMinute))
                ) {
                    return true
                }
            }
        }
        return false
    }

    fun formatTodayOperatingHours(
        stringProvider: StringProvider,
        operatingHours: OperatingHours,
        zoneId: String,
        currentTime: Calendar = Calendar.getInstance()
    ): String {
        currentTime.timeZone = TimeZone.getTimeZone(zoneId)
        // have to switch index here because day of week for Calendar and model's period are different
        // Calendar: Sun - 1, Mon - 2 ... Sat - 7
        // Period: Mon - 0, Tues - 1 ... Sun - 6
        val currentDay = (currentTime.get(Calendar.DAY_OF_WEEK) + 5) % 7
        operatingHours.periods?.let {
            for (period in it) {
                val openDay = period.venueOpen._day

                if (openDay == currentDay) {
                    if (period.isEmpty) return ""
                    val openTime = period.venueOpen.time
                    val closeTime = period.venueClose.time
                    return stringProvider.getString(R.string.explore_events_date_time_to_placeholders_label)
                        .replaceBraces(
                            reformatOperatingHoursString(openTime),
                            reformatOperatingHoursString(closeTime)
                        )
                }
            }
        }

        return ""
    }

    fun getWelcomeHeaderRes(hourOfDay: Int): Int {
        return when {
            hourOfDay in 0 until 5 -> R.string.home_night_header
            hourOfDay in 5 until 12 -> R.string.home_morning_header
            hourOfDay in 12 until 18 -> R.string.home_afternoon_header
            hourOfDay in 18 until 24 -> R.string.home_evening_header
            else -> R.string.home_welcome_header
        }
    }

    fun getTimeElapsed(from: Date, to: Date): Pair<TimeUnit, Long>? {
        //milliseconds
        val different = to.time - from.time
        return getTimeElapsed(different)
    }

    fun getTimeElapsed(from: DateTime, to: DateTime): Pair<TimeUnit, Long>? {
        //milliseconds
        val different = to.millis - from.millis
        return getTimeElapsed(different)
    }

    fun getTimeElapsed(from: Long, to: Long): Pair<TimeUnit, Long>? {
        //milliseconds
        val different = to - from
        return getTimeElapsed(different)
    }

    private fun getTimeElapsed(diff: Long): Pair<TimeUnit, Long>? {
        var different = diff
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24

        val elapsedDays = different / daysInMilli
        different %= daysInMilli

        val elapsedHours = different / hoursInMilli
        different %= hoursInMilli

        val elapsedMinutes = different / minutesInMilli
        different %= minutesInMilli

        val elapsedSeconds = different / secondsInMilli

        return when {
            elapsedDays < 1 && elapsedHours < 1 && elapsedMinutes < 1 -> Pair(
                TimeUnit.SECONDS,
                elapsedSeconds
            )
            elapsedDays < 1 && elapsedHours < 1 && elapsedMinutes >= 1 -> Pair(
                TimeUnit.MINUTES,
                elapsedMinutes
            )
            elapsedDays < 1 && elapsedHours in 1..23 -> Pair(TimeUnit.HOURS, elapsedHours)
            elapsedDays >= 1 -> Pair(TimeUnit.DAYS, elapsedDays)
            else -> null
        }
    }

    fun getTimeElapsedLabel(stringProvider: StringProvider, date: Date?): String {
        if (date == null) return ""

        val curDate = Calendar.getInstance().time

        val elapsedTime = getTimeElapsed(date, curDate) ?: return ""
        val (timeUnit, elapsed) = elapsedTime

        return when {
            timeUnit == TimeUnit.SECONDS -> stringProvider.getString(R.string.connect_board_post_now_label)
            timeUnit == TimeUnit.MINUTES -> stringProvider.getString(R.string.connect_board_post_minutes_label)
                .replaceBraces(elapsed.toString())
            timeUnit == TimeUnit.HOURS -> stringProvider.getString(R.string.connect_board_post_hours_label)
                .replaceBraces(elapsed.toString())
            timeUnit == TimeUnit.DAYS -> {
                stringProvider.getString(R.string.connect_board_post_days_label)
                    .replaceBraces(elapsed.toString())
            }
            else -> ""
        }
    }

    val Day.label: String
        get() {
            val textStyle = TextStyle.FULL
            val locale = Locale.getDefault()
            return when (this) {
                MONDAY -> DayOfWeek.MONDAY.getDisplayName(textStyle, locale)
                TUESDAY -> DayOfWeek.TUESDAY.getDisplayName(textStyle, locale)
                WEDNESDAY -> DayOfWeek.WEDNESDAY.getDisplayName(textStyle, locale)
                THURSDAY -> DayOfWeek.THURSDAY.getDisplayName(textStyle, locale)
                FRIDAY -> DayOfWeek.FRIDAY.getDisplayName(textStyle, locale)
                SATURDAY -> DayOfWeek.SATURDAY.getDisplayName(textStyle, locale)
                SUNDAY -> DayOfWeek.SUNDAY.getDisplayName(textStyle, locale)
                else -> ""
            }
        }

    val Day.sortOrder: Int
        get() = when (this) {
            MONDAY -> 0
            TUESDAY -> 1
            WEDNESDAY -> 2
            THURSDAY -> 3
            FRIDAY -> 4
            SATURDAY -> 5
            SUNDAY -> 6
        }

}

fun DateTime.monthLabel(stringProvider: StringProvider): String {
    DateTimeConstants.JANUARY
    return when (monthOfYear) {
        DateTimeConstants.JANUARY -> stringProvider.getString(R.string.january)
        DateTimeConstants.FEBRUARY -> stringProvider.getString(R.string.feb)
        DateTimeConstants.MARCH -> stringProvider.getString(R.string.march)
        DateTimeConstants.APRIL -> stringProvider.getString(R.string.april)
        DateTimeConstants.MAY -> stringProvider.getString(R.string.may)
        DateTimeConstants.JUNE -> stringProvider.getString(R.string.june)
        DateTimeConstants.JULY -> stringProvider.getString(R.string.july)
        DateTimeConstants.AUGUST -> stringProvider.getString(R.string.august)
        DateTimeConstants.SEPTEMBER -> stringProvider.getString(R.string.september)
        DateTimeConstants.OCTOBER -> stringProvider.getString(R.string.october)
        DateTimeConstants.NOVEMBER -> stringProvider.getString(R.string.november)
        DateTimeConstants.DECEMBER -> stringProvider.getString(R.string.december)
        else -> ""
    }
}

fun Venue?.isVenueOpen(): Boolean {
    if (this == null) return false

    return DateUtils.isVenueOpen(operatingHours, timeZone)
}