package com.sohohouse.seven.connect.filter.city

import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.connect.filter.FilterManager
import com.sohohouse.seven.connect.filter.base.FilterType
import com.sohohouse.seven.connect.filter.base.FilterViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class CityFilterViewModel @Inject constructor(
    repo: CityFilterRepository,
    filterManager: FilterManager,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : FilterViewModel(FilterType.CITY_FILTER, repo, filterManager, analyticsManager, dispatcher)