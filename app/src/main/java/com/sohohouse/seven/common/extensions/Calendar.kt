package com.sohohouse.seven.common.extensions

import java.util.*

fun Calendar.setTimeToMidNight(isStart: Boolean): Date {
    if (isStart) {
        this.set(Calendar.HOUR_OF_DAY, 0)
        this.clear(Calendar.MINUTE)
        this.clear(Calendar.SECOND)
        this.clear(Calendar.MILLISECOND)
    } else {
        this.set(Calendar.MILLISECOND, 999)
        this.set(Calendar.SECOND, 59)
        this.set(Calendar.MINUTE, 59)
        this.set(Calendar.HOUR_OF_DAY, 23)
    }
    return this.time
}