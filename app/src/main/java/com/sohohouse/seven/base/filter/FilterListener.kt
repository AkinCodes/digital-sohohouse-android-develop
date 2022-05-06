package com.sohohouse.seven.base.filter

import com.sohohouse.seven.common.views.categorylist.CategoryDataItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerChildItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerParentItem
import java.util.*

interface FilterListener {
    fun onSelectedLocationsChanged(locationList: List<String>)
    fun onSelectedDateChanged(date: Date?, isStart: Boolean)
    fun getFavouriteHousesData(): List<LocationRecyclerChildItem>
    fun getAllHousesData(): List<LocationRecyclerParentItem>
    fun getSelectedCategories(): List<String>
    fun getAllCategories(): List<CategoryDataItem>
    fun getSelectedLocations(): List<String>
    fun onCategorySelectionUpdated(selectedItems: List<String>)
    fun getSelectedStartDate(): Date?
    fun getSelectedEndDate(): Date?
}