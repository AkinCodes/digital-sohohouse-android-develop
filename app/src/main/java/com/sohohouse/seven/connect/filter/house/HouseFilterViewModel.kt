package com.sohohouse.seven.connect.filter.house

import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.connect.filter.FilterManager
import com.sohohouse.seven.connect.filter.base.FilterType
import com.sohohouse.seven.connect.filter.base.FilterViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class HouseFilterViewModel @Inject constructor(
    repo: HouseFilterRepository,
    filterManager: FilterManager,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : FilterViewModel(FilterType.HOUSE_FILTER, repo, filterManager, analyticsManager, dispatcher)