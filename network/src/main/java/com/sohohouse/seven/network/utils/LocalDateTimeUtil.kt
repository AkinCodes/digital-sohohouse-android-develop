package com.sohohouse.seven.network.utils

import org.threeten.bp.LocalDateTime

object LocalDateTimeUtil {

    fun since1970(): LocalDateTime = LocalDateTime.of(1970, 1, 1, 0, 0)

    fun getNext15MinuteIntervalTime(initialTime: LocalDateTime): LocalDateTime {
        var nextTime = initialTime
        when {
            nextTime.minute in 1..15 -> {
                nextTime = nextTime.withMinute(15)
            }
            nextTime.minute in 16..30 -> {
                nextTime = nextTime.withMinute(30)
            }
            nextTime.minute in 31..45 -> {
                nextTime = nextTime.withMinute(45)
            }
            nextTime.minute > 45 -> {
                if (nextTime.hour < 23) {
                    nextTime = nextTime.plusHours(1)
                    nextTime = nextTime.withMinute(0)
                } else {
                    nextTime = nextTime.withMinute(45)
                }
            }
        }
        return nextTime
    }

}