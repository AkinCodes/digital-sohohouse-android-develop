package com.sohohouse.seven.network.utils

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

fun LocalDateTime.getApiFormattedDate(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    return formatter.format(this)
}
