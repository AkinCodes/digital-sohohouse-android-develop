package com.sohohouse.seven.connect.message.chat

import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.LiveEvent
import com.sohohouse.seven.common.analytics.AnalyticsManager
import javax.inject.Inject


class ChatViewModel @Inject constructor(
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager) {

    val fullScreenMutableLiveData = LiveEvent<Pair<String?, String?>>()

    fun goFullScreen(urlPair: Pair<String?, String?>) {
        fullScreenMutableLiveData.value = urlPair
    }

}