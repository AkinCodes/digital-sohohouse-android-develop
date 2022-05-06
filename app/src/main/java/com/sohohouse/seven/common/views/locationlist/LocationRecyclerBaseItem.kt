package com.sohohouse.seven.common.views.locationlist

import com.sohohouse.seven.base.DiffItem

abstract class LocationRecyclerBaseItem(open val name: String, val filterType: FilterItemType) :
    DiffItem