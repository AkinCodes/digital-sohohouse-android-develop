package com.sohohouse.seven.common.views.locationlist

import com.sohohouse.seven.common.house.HouseRegion

data class LocationCityItem(
    val venueIdsInCity: List<String>,
    override val name: String,
    val region: HouseRegion,
    var selected: Boolean = false
) : LocationRecyclerBaseItem(name, FilterItemType.CHILD)