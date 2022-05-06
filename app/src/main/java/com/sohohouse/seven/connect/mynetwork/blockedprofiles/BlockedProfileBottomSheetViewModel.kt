package com.sohohouse.seven.connect.mynetwork.blockedprofiles

import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import javax.inject.Inject

class BlockedProfileBottomSheetViewModel @Inject constructor(
    analyticsManager: AnalyticsManager
) :
    BaseViewModel(analyticsManager) {

    fun logAnalyticsActions(action: AnalyticsManager.Action) {
        analyticsManager.logEventAction(action)
    }

}