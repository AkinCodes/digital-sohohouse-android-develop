package com.sohohouse.seven.common.extensions

import com.sohohouse.seven.network.core.models.OperatingHours
import com.sohohouse.seven.network.core.models.Period

val OperatingHours.isUnavailable: Boolean
    get() {
        return periods == null || periods!!.all { it.isEmpty }
    }

val Period.isEmpty get() = venueOpen._day == venueClose._day && venueOpen.time == venueClose.time