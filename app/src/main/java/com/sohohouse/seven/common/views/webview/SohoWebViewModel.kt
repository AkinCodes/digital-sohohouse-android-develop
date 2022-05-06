package com.sohohouse.seven.common.views.webview

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.branding.ThemeManager
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.apihelpers.SohoWebHelper
import javax.inject.Inject

class SohoWebViewModel @Inject constructor(
    private val themeManager: ThemeManager,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager),
    ErrorViewStateViewModel by ErrorViewStateViewModelImpl(),
    Loadable.ViewModel by Loadable.ViewModelImpl() {

    private val _uri: MutableLiveData<Uri> = MutableLiveData()

    val uri: LiveData<Uri>
        get() = _uri

    fun loadUrl(type: SohoWebHelper.KickoutType, id: String? = null, url: String? = "") {
        setScreenName(type)

        _uri.postValue(SohoWebHelper.getWebViewFormatted(type, id, url, themeManager.webTheme))
    }

    fun isSecureUrl(type: SohoWebHelper.KickoutType?): Boolean {
        return SohoWebHelper.isSecureUrl(type)
    }

    private fun setScreenName(eventTypePosition: SohoWebHelper.KickoutType) {
        when (eventTypePosition) {
            SohoWebHelper.KickoutType.FAQS -> setScreenNameInternal(AnalyticsManager.Screens.FAQ.name)
            SohoWebHelper.KickoutType.PRIVACY_POLICY -> setScreenNameInternal(AnalyticsManager.Screens.PrivacyPolicies.name)
            SohoWebHelper.KickoutType.TERMS_CONDITIONS -> setScreenNameInternal(AnalyticsManager.Screens.TermsConditions.name)
            else -> {
            }
        }
    }
}