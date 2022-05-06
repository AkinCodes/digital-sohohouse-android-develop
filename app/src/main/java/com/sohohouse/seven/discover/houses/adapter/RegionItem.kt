package com.sohohouse.seven.discover.houses.adapter

import androidx.annotation.StringRes

class RegionItem(
    @StringRes val title: Int,
    val houses: List<HouseItem> = emptyList()
) : BaseHouseItem() {
    override val key: Any? = title

    override val type: Int = TYPE_REGION_ITEM
}
