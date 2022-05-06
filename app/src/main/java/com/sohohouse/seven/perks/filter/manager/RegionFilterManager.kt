package com.sohohouse.seven.perks.filter.manager

import com.sohohouse.seven.base.filter.Filter
import com.sohohouse.seven.common.baseclasses.BaseFilterManager

enum class RegionFilterState {
    NO_FILTER,
    FILTERED,
}

class RegionFilterManager : BaseFilterManager() {

    internal var filterState: RegionFilterState = RegionFilterState.NO_FILTER

    override fun applyDraft() {
        val draftList = draftFilter.selectedRegions
        if (draftList.isEmpty()) {
            setToNoFilterState()
        } else {
            filterState = RegionFilterState.FILTERED
            appliedFilter.selectedRegions = draftList
        }
        draftFilter = Filter()
    }

    override fun setToNoFilterState() {
        filterState = RegionFilterState.NO_FILTER
        defaultFilter.selectedRegions = listOf()
        appliedFilter.selectedRegions = listOf()
    }

}