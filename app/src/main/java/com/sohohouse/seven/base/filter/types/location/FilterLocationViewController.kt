package com.sohohouse.seven.base.filter.types.location

import com.sohohouse.seven.base.filter.types.FilterBaseViewController
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerChildItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerParentItem

interface FilterLocationViewController : FilterBaseViewController {
    fun setUpRecyclerView(
        favouriteHousesData: List<LocationRecyclerChildItem>,
        allHousesData: List<LocationRecyclerParentItem>
    )
}