package com.sohohouse.seven.connect.filter

import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.filter.base.FilterType
import java.util.*

interface FilterManager {

    fun get(filterType: FilterType): List<Filter>

    fun set(filterType: FilterType, filters: List<Filter>)

    fun add(filterType: FilterType, filter: Filter)

    fun remove(filterType: FilterType, filter: Filter)

    fun remove(filter: Filter)

    fun clear()

    fun clear(filterType: FilterType)

    fun asList(): List<Filter>

    fun asMap(): Map<FilterType, Collection<Filter>>

    /**
     * Track filter interactions like selecting, unselecting and resetting
     */
    fun setTrackedFilters(filterType: FilterType, filters: Collection<Filter>)

    fun clearTrackedFilters()

    fun clearTrackedFilters(filterType: FilterType)

    fun trackFilter(filterType: FilterType, filter: Filter, selected: Boolean)

    fun trackedFilters(): Map<FilterType, Collection<Filter>>

    fun saveTrackedFilters()
}

class FilterManagerImpl : FilterManager {

    private var filters = EnumMap<FilterType, HashSet<Filter>>(FilterType::class.java)

    override fun get(filterType: FilterType): List<Filter> =
        filters[filterType]?.toList() ?: emptyList()

    override fun set(filterType: FilterType, filters: List<Filter>) {
        this.filters[filterType] = filters.toHashSet()
    }

    override fun add(filterType: FilterType, filter: Filter) {
        val set = filters[filterType] ?: hashSetOf<Filter>().also { filters[filterType] = it }
        set.add(filter)
    }

    override fun remove(filterType: FilterType, filter: Filter) {
        filters[filterType]?.remove(filter)
    }

    override fun remove(filter: Filter) {
        filters.keys.forEach { if (filters[it]?.remove(filter) == true) return }
    }

    override fun clear() {
        filters = EnumMap<FilterType, HashSet<Filter>>(FilterType::class.java)
        clearTrackedFilters()
    }

    override fun clear(filterType: FilterType) {
        filters[filterType]?.clear()
        clearTrackedFilters(filterType)
    }

    override fun asList(): List<Filter> = filters.values.flatten()

    override fun asMap(): Map<FilterType, Collection<Filter>> = filters

    /**
     * Track filter interactions like selecting, unselecting and resetting
     */
    private var tracked = EnumMap<FilterType, HashSet<Filter>>(FilterType::class.java)

    override fun setTrackedFilters(filterType: FilterType, filters: Collection<Filter>) {
        tracked[filterType] = filters.toHashSet()
    }

    override fun clearTrackedFilters() {
        tracked = EnumMap<FilterType, HashSet<Filter>>(FilterType::class.java)
    }

    override fun clearTrackedFilters(filterType: FilterType) {
        tracked[filterType]?.clear()
    }

    override fun trackFilter(filterType: FilterType, filter: Filter, selected: Boolean) {
        if (selected) {
            (tracked[filterType] ?: hashSetOf<Filter>().also { tracked[filterType] = it }).add(
                filter
            )
        } else {
            tracked[filterType]?.remove(filter)
        }
    }

    override fun trackedFilters(): Map<FilterType, Collection<Filter>> = tracked

    override fun saveTrackedFilters() {
        filters = tracked.clone()
    }
}