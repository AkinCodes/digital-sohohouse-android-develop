package com.sohohouse.seven.base.filter.types.location

import com.sohohouse.seven.R
import com.sohohouse.seven.base.filter.FilterListener
import com.sohohouse.seven.common.views.locationlist.BaseFilterLocationAdapter
import com.sohohouse.seven.common.views.locationlist.LocationClickListener
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerChildItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerParentItem

class ExploreFilterLocationAdapter(
    allHouses: List<LocationRecyclerParentItem>,
    favouriteHouses: List<LocationRecyclerChildItem>,
    private val valueChangedListener: FilterListener
) : BaseFilterLocationAdapter(
    R.string.content_filter_all_houses_label,
    R.string.explore_events_filter_my_houses_supporting,
    R.string.explore_events_filter_my_houses_label,
    favouriteHouses,
    allHouses,
    object : LocationClickListener {
        override fun onLocationClicked(selectedLocations: List<String>) {
            valueChangedListener.onSelectedLocationsChanged(selectedLocations)
        }
    })