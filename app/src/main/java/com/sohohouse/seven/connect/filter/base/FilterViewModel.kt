package com.sohohouse.seven.connect.filter.base

import androidx.lifecycle.*
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.connect.filter.FilterManager
import com.sohohouse.seven.network.base.error.ServerError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class FilterViewModel(
    protected val filterType: FilterType,
    protected val repo: FilterRepository,
    protected val filterManager: FilterManager,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel(analyticsManager, dispatcher) {

    protected open val _error: MutableLiveData<ServerError> = MutableLiveData()

    protected val filters = filterManager.get(filterType).toHashSet()

    val error: LiveData<ServerError>
        get() = _error

    var mode: FilterMode = FilterMode.FILTERS

    protected val _items = MediatorLiveData<List<Filterable>>().apply {
        addSource(repo.items) { onItemsChanged(it, filters.toTypedArray()) }
    }

    val items: LiveData<List<Filterable>>
        get() = _items

    fun load(filters: Array<Filter>? = null) {
        onFilterChanged(
            if (FilterMode.FILTERS == mode) this.filters.toTypedArray() else filters ?: emptyArray()
        )
    }

    open fun getFilters(): List<Filter> {
        return when (mode) {
            FilterMode.FILTERS -> filterManager.get(filterType)
            FilterMode.TAGGING -> filters.toList()
        }
    }

    fun resetFilters() {
        if (FilterMode.FILTERS == mode) {
            filterManager.clear(filterType)
        }
        filters.clear()
        onFilterChanged(emptyArray())
    }

    fun saveFilters() {
        filterManager.set(filterType, filters.toList())
    }

    private fun onItemsChanged(items: List<Filterable>, filters: Array<Filter> = emptyArray()) {
        viewModelScope.launch(viewModelContext) {
            buildItemsWithFilters(resetItems(items), filters)
        }
    }

    private fun resetItems(items: List<Filterable>): List<Filterable> {
        return items.map { filterable ->
            when (filterable) {
                is SectionItem -> filterable.copy(items = filterable.items.map { it.copy() })
                is FilterItem -> filterable.copy()
            }
        }
    }

    private fun onFilterChanged(filters: Array<Filter>) {
        this.filters.clear()
        this.filters.addAll(filters)

        viewModelScope.launch(viewModelContext) {
            val items = _items.value?.map { filterable ->
                when (filterable) {
                    is SectionItem -> filterable.copy(items = filterable.items.map {
                        it.copy(
                            selected = false
                        )
                    })
                    is FilterItem -> filterable.copy(selected = false)
                }
            } ?: return@launch

            buildItemsWithFilters(items, filters)
        }
    }

    protected open fun buildItemsWithFilters(items: List<Filterable>, filters: Array<Filter>) {
        val ids = filters.map { filter -> filter.id }
        for (filterable in items) {
            if (filterable !is SectionItem) continue
            filterable.items.forEach { it.selected = ids.contains(it.id) }
        }
        _items.postValue(items)
    }

    protected fun onError(error: ServerError) {
        Timber.d(error.toString())
        _error.postValue(error)
    }

    fun setFilterSelected(filter: Filter, selected: Boolean) {
        if (selected) {
            if (FilterMode.TAGGING == mode) {
                filters.clear()
            }
            filters.add(filter)
        } else {
            filters.remove(filter)
        }
    }

}