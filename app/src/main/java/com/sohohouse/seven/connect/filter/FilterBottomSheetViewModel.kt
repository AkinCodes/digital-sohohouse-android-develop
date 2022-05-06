package com.sohohouse.seven.connect.filter

import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import kotlinx.coroutines.CoroutineDispatcher

abstract class FilterBottomSheetViewModel constructor(
    private val filterManager: FilterManager,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher) {

    override fun onCleared() {
        super.onCleared()
        filterManager.clearTrackedFilters()
    }

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.FilterBottomSheet.name)
    }

    abstract fun onApplyFilters()

    abstract fun onCancelled()
}
