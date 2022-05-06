package com.sohohouse.seven.connect.match

import android.os.Bundle
import androidx.core.os.bundleOf
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.connect.filter.FilterBottomSheetViewModel
import com.sohohouse.seven.connect.filter.FilterManager
import com.sohohouse.seven.connect.filter.base.FilterType
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class RecommendationListFilterViewModel @Inject constructor(
    val filterManager: FilterManager,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher
) : FilterBottomSheetViewModel(filterManager, analyticsManager, dispatcher) {

    override fun onApplyFilters() {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.RecommendationsFilterConfirm,
            getBundleOfFilters()
        )
    }

    override fun onCancelled() {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.RecommendationsFilterClose,
            getBundleOfFilters()
        )
    }

    fun getBundleOfFilters(): Bundle {
        return bundleOf(
            "recommendation_industry" to filterManager.get(FilterType.INDUSTRY_FILTER),
            "recommendation_location" to filterManager.get(FilterType.CITY_FILTER),
            "recommendation_interest" to filterManager.get(FilterType.TOPIC_FILTER)
        )
    }

}