package com.sohohouse.seven.common.views.locationlist

import com.sohohouse.seven.common.house.HouseRegion

data class LocationRecyclerParentItem constructor(
    val region: HouseRegion, val childList: List<LocationRecyclerChildItem>,
    var expanded: Boolean
) : LocationRecyclerBaseItem("", FilterItemType.PARENT) {
    override val key: Any?
        get() = region.id

    val selectedChildCount: Int get() = childList.count { it.selected }
}