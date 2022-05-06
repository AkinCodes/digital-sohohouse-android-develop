@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.sohohouse.seven.common.extensions

import com.sohohouse.seven.book.table.TableBookingUtil
import com.sohohouse.seven.network.core.models.SlotLock
import java.util.*

val SlotLock.dateTime: Date
    get() = TableBookingUtil.DATE_FORMATTER.parse(date_time)