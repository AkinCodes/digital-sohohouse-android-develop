package com.sohohouse.seven.common.extensions

import com.sohohouse.seven.network.core.models.Attendance

fun Attendance?.isVisiting(): Boolean {
    return this?.createdAt?.isToday() == true
}
