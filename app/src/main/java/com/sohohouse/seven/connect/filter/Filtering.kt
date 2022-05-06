package com.sohohouse.seven.connect.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.filter.base.FilterType

interface Filtering {
    val filters: LiveData<List<Filter>>
    fun removeFilter(filter: Filter)
    fun checkUpdateFilters()
    fun clearFiltersAndRefresh()
}

abstract class SohoHouseFilter(
    val filterManager: FilterManager
) : Filtering {

    override val filters: MutableLiveData<List<Filter>> = MutableLiveData<List<Filter>>()

    init {
        filterManager.clear()
        refresh()
    }

    override fun checkUpdateFilters() {
        val latest = filterManager.asList()
        val existing = filters.value ?: emptyList()
        val unChanged = latest.containsAll(existing) && existing.containsAll(latest)
        if (!unChanged) {
            refresh()
        }
    }

    override fun removeFilter(filter: Filter) {
        filterManager.remove(filter)
        refresh()
    }

    override fun clearFiltersAndRefresh() {
        filterManager.clear()
        refresh()
    }

    fun refresh() {
        filters.postValue(mutableListOf<Filter>().apply {
            addAll(filterManager.get(FilterType.HOUSE_FILTER))
            addAll(filterManager.get(FilterType.CITY_FILTER))
            addAll(filterManager.get(FilterType.TOPIC_FILTER))
            addAll(filterManager.get(FilterType.INDUSTRY_FILTER))
        })
    }
}