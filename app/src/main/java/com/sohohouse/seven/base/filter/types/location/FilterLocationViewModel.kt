package com.sohohouse.seven.base.filter.types.location

import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerChildItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerParentItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FilterLocationViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel(analyticsManager) {

    private val _eventsFlow = MutableStateFlow<UiEvent>(UiEvent.Empty)
    val eventsFlow = _eventsFlow.asStateFlow()

    fun onDataReady(
        favouriteHousesData: List<LocationRecyclerChildItem>,
        allHousesData: List<LocationRecyclerParentItem>
    ) {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            _eventsFlow.value = UiEvent.SetUpRecyclerView(favouriteHousesData, allHousesData)
        }
    }

    sealed class UiEvent {
        object Empty : UiEvent()
        data class SetUpRecyclerView(
            val favouriteHousesData: List<LocationRecyclerChildItem>,
            val allHousesData: List<LocationRecyclerParentItem>
        ) : UiEvent()
    }
}