package com.sohohouse.seven.base.filter

import androidx.fragment.app.Fragment
import com.sohohouse.seven.base.filter.types.categories.FilterCategoriesFragment
import com.sohohouse.seven.base.filter.types.date.FilterDateFragment
import com.sohohouse.seven.base.filter.types.location.FilterLocationFragment

class FilterFlowManager : BaseFilterFlowManager() {

    override fun transitionFrom(filterType: FilterType): Fragment {
        return when (filterType) {
            FilterType.LOCATION -> {
                currentFragmentTag = FilterLocationFragment.TAG
                FilterLocationFragment()
            }
            FilterType.DATE -> {
                currentFragmentTag = FilterDateFragment.TAG
                FilterDateFragment()
            }
            FilterType.CATEGORIES -> {
                currentFragmentTag = FilterCategoriesFragment.TAG
                FilterCategoriesFragment()
            }
        }
    }
}