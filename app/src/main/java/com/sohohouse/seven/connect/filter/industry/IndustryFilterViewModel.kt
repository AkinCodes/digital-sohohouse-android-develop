package com.sohohouse.seven.connect.filter.industry

import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.connect.filter.FilterManager
import com.sohohouse.seven.connect.filter.base.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class IndustryFilterViewModel @Inject constructor(
    repo: IndustryFilterRepository,
    filterManager: FilterManager,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : FilterViewModel(FilterType.TOPIC_FILTER, repo, filterManager, analyticsManager, dispatcher) {
    override fun buildItemsWithFilters(items: List<Filterable>, filters: Array<Filter>) {
        items.forEach { (it as FilterItem).selected = filters.contains(it.filter) }
        _items.postValue(items)
    }
}