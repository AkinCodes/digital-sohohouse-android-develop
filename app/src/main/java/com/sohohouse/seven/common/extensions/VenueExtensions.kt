package com.sohohouse.seven.common.extensions

import com.sohohouse.seven.common.house.HouseType
import com.sohohouse.seven.common.utils.DateUtils
import com.sohohouse.seven.common.utils.DateUtils.label
import com.sohohouse.seven.common.utils.DateUtils.sortOrder
import com.sohohouse.seven.network.core.models.Venue

fun Venue.getLocationColor(): String {
    return this.venueColors.house
}

fun Venue?.isOpenNow(): Boolean {
    if (this != null) {
        if (DateUtils.isVenueOpen(operatingHours, timeZone)) {
            return true
        }
    }
    return false
}


val Venue.isCwh
    get() = venueType == HouseType.CWH.name

fun Venue.isOpenForBusiness(): Boolean {
    return this.isCwh || this.operatingHours.isUnavailable.not()
}

fun Venue.buildAddress(singleLine: Boolean): String {
    val address = venueAddress
    val locationStrings = ArrayList<String>()
    if (address.lines?.isNotEmpty() == true) {
        address.lines!!.forEach {
            if (it.isNotEmpty()) locationStrings.add(it)
        }
    } else {
        if (address.locality.isNotEmpty()) {
            locationStrings.add(address.locality!!)
        }
        if (address.country.isNotEmpty()) {
            locationStrings.add(address.country!!)
        }
    }
    return locationStrings.joinToString(separator = if (singleLine) ", " else "\n")
}

fun Venue?.getVenueOperatingHours(dayAndTimesPlaceholder: String): List<String> {
    this ?: return emptyList()

    val hoursList = java.util.ArrayList<String>()
    operatingHours.periods?.let { periodList ->
        periodList.sortedBy { it.venueOpen.day?.sortOrder }.forEachIndexed { index, period ->
            if (period.isEmpty.not()) {
                hoursList.add(
                    dayAndTimesPlaceholder.replaceBraces(
                        period.venueOpen.day?.label ?: "",
                        DateUtils.reformatOperatingHoursString(period.venueOpen.time),
                        DateUtils.reformatOperatingHoursString(period.venueClose.time)
                    )
                )
            }
        }
    }
    return hoursList
}