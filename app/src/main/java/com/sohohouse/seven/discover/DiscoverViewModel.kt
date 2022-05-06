package com.sohohouse.seven.discover

import android.net.Uri
import androidx.lifecycle.LiveData
import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.deeplink.DeeplinkRepo
import javax.inject.Inject

class DiscoverViewModel @Inject constructor(
    private val deeplinkRepo: DeeplinkRepo,
    private val featureFlags: FeatureFlags,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager) {

    val deeplink: LiveData<Uri>
        get() = deeplinkRepo.get()

    fun deleteDeeplink() {
        deeplinkRepo.delete()
    }

    fun isBenefitsCityFilterEnabled(): Boolean {
        return featureFlags.benefitsFilterByCity
    }

}