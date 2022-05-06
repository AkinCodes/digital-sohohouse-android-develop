package com.sohohouse.seven.base.filter

import com.sohohouse.seven.common.extensions.setTimeToMidNight
import java.util.*

class Filter(localHouse: String? = null, favouriteHouses: List<String>? = null) {
    companion object {
        val defaultDate: Date by lazy {
            Calendar.getInstance().setTimeToMidNight(true)
        }
    }

    var selectedRegions: List<String> = listOf()
    var selectedLocationList: List<String>?
    var selectedStartDate: Date = defaultDate
    var selectedEndDate: Date? = null
    var selectedCategoryList: List<String>? = null

    init {
        val locationList = mutableListOf<String>()
        localHouse?.let { locationList.add(localHouse) }
        favouriteHouses?.let {
            locationList.addAll(favouriteHouses)
        }
        val distinctLocationList = locationList.distinct()
        selectedLocationList = if (distinctLocationList.isNotEmpty()) distinctLocationList else null
    }
}