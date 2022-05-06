package com.sohohouse.seven.connect.mynetwork

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import javax.inject.Inject

class MyConnectionsViewModel @Inject constructor(
    repo: ConnectionRepository,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager) {

    val numOfRequests: LiveData<Int> = repo.numberOfConnectRequests.asLiveData()

}