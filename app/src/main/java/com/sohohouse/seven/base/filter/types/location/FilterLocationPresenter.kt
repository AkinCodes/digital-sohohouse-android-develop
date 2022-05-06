package com.sohohouse.seven.base.filter.types.location

import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerChildItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerParentItem

class FilterLocationPresenter : BasePresenter<FilterLocationViewController>() {
    fun onDataReady(
        favouriteHousesData: List<LocationRecyclerChildItem>,
        allHousesData: List<LocationRecyclerParentItem>
    ) {
        executeWhenAvailable { view, _, _ ->
            view.setUpRecyclerView(
                favouriteHousesData,
                allHousesData
            )
        }
    }
}