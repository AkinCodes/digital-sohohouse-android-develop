package com.sohohouse.seven.common.views.locationlist

import androidx.annotation.StringRes

abstract class BaseFilterLocationAdapter(
    @StringRes private val allHousesLabel: Int,
    @StringRes private val myHousesSupportingLabel: Int,
    @StringRes private val myHousesLabel: Int,
    favouriteHouses: List<LocationRecyclerChildItem>,
    allHouses: List<LocationRecyclerParentItem>,
    locationClickListener: LocationClickListener
) : BaseLocationRecyclerAdapter(favouriteHouses, allHouses, locationClickListener) {

    init {
        add(LocationRecyclerTextItem(myHousesLabel, FilterItemType.HEADER, myHousesSupportingLabel))
        addMyHouses()
        add(LocationRecyclerTextItem(allHousesLabel, FilterItemType.SUBHEADER))
        addAllHouses()
    }
}