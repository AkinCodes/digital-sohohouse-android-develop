package com.sohohouse.seven.connect.match

import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.analytics.FilterEventParam
import com.sohohouse.seven.connect.filter.FilterManager
import com.sohohouse.seven.connect.filter.SohoHouseFilter
import com.sohohouse.seven.connect.filter.base.Filter

class RecommendedPeopleFilter(
    filterManager: FilterManager, val analyticsManager: AnalyticsManager,
) : SohoHouseFilter(filterManager) {

    override fun removeFilter(filter: Filter) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.RecommendationsFilterDeselect,
            FilterEventParam.withFilters(filterManager.asMap())
        )
        super.removeFilter(filter)
    }
}