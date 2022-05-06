package com.sohohouse.seven.apponboarding

import android.os.Bundle
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.dagger.Injectable

class AppOnboardingActivity : BaseMVVMActivity<AppOnboardingViewModel>(), Injectable {

    override val viewModelClass: Class<AppOnboardingViewModel>
        get() = AppOnboardingViewModel::class.java

    override fun getContentLayout(): Int {
        return R.layout.activity_app_onboarding
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        navigateToNext(AppOnboardScreen.INTRO)
        viewModel.setScreenName(name = AnalyticsManager.Screens.OnboardWelcome.name)
    }

    fun navigateToNext(currentScreen: AppOnboardScreen) {
        viewModel.navigateOnboardingScreen(this, currentScreen)
    }
}
