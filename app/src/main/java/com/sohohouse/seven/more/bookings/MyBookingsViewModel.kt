package com.sohohouse.seven.more.bookings

import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import javax.inject.Inject

class MyBookingsViewModel @Inject constructor(analyticsManager: AnalyticsManager) :
    BaseViewModel(analyticsManager) {
    fun logUpcomingBookingsTabSelected() {
        analyticsManager.logEventAction(AnalyticsManager.Action.BookingTabUpcoming)
    }

    fun logPastBookingsTabSelected() {
        analyticsManager.logEventAction(AnalyticsManager.Action.BookingTabHistory)
    }

}