package com.sohohouse.seven.connect.noticeboard

import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.analytics.FilterEventParam
import com.sohohouse.seven.connect.filter.FilterBottomSheetViewModel
import com.sohohouse.seven.connect.filter.FilterManager
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class NoticeboardFilterFragmentViewModel @Inject constructor(
    val filterManager: FilterManager,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher
) : FilterBottomSheetViewModel(filterManager, analyticsManager, dispatcher) {

    override fun onApplyFilters() {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.NoticeboardFilterConfirm,
            FilterEventParam.withFilters(filterManager.asMap())
        )
    }

    override fun onCancelled() {
        analyticsManager.logEventAction(AnalyticsManager.Action.NoticeboardFilterClose)
    }
}