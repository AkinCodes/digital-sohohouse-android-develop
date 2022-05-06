package com.sohohouse.seven.perks.filter

import com.sohohouse.seven.base.mvpimplementation.ViewController
import com.sohohouse.seven.common.views.categorylist.CategoryAdapterBaseItem

interface PerksFilterViewController : ViewController {
    fun onDataReady(
        selectedRegions: List<String>,
        allRegionDataItems: MutableList<CategoryAdapterBaseItem>
    )
}