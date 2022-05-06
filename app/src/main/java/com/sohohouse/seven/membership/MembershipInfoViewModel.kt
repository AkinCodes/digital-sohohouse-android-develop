package com.sohohouse.seven.membership

import androidx.lifecycle.ViewModel
import android.os.Bundle
import com.sohohouse.seven.common.analytics.AnalyticsManager
import javax.inject.Inject

class MembershipInfoViewModel @Inject constructor(private val eventTracking: AnalyticsManager) :
    ViewModel() {

    fun trackEvent(action: AnalyticsManager.Action, params: Bundle?) {
        eventTracking.logEventAction(action, params)
    }

}