package com.sohohouse.seven.welcome

import android.content.Context
import android.content.Intent
import com.sohohouse.seven.authentication.AuthenticationActivity
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.LiveEvent
import com.sohohouse.seven.common.analytics.AnalyticsManager
import javax.inject.Inject

class WelcomeViewModel @Inject constructor(analyticsManager: AnalyticsManager) :
    BaseViewModel(analyticsManager) {

    private val _navigation = LiveEvent<Intent>()

    val navigation: LiveEvent<Intent>
        get() = _navigation

    fun onClickSignIn(context: Context) {
        analyticsManager.logEventAction(AnalyticsManager.Action.SignIn)
        _navigation.postValue(Intent(context, AuthenticationActivity::class.java))
    }

    override fun onScreenViewed() {
        super.onScreenViewed()
        setScreenNameInternal(AnalyticsManager.Screens.Welcome.name)
    }
}