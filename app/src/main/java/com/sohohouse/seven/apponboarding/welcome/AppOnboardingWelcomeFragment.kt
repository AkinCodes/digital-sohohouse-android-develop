package com.sohohouse.seven.apponboarding.welcome

import android.os.Bundle
import android.view.View
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.apponboarding.AppOnboardScreen
import com.sohohouse.seven.apponboarding.AppOnboardingActivity
import com.sohohouse.seven.base.BaseFragment
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.databinding.FragmentAppOnboardingWelcomeBinding

class AppOnboardingWelcomeFragment : BaseFragment() {

    override val contentLayoutId get() = R.layout.fragment_app_onboarding_welcome

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        App.appComponent.analyticsManager.setScreenName(
            requireActivity().localClassName,
            AnalyticsManager.Screens.OnboardWelcome.name
        )

        with(FragmentAppOnboardingWelcomeBinding.bind(view).welcomeCard) {
            setup(
                R.string.app_onboarding_welcome_title,
                R.string.app_onboarding_welcome_supporting,
                R.string.app_onboarding_welcome_cta
            )

            clicks({
                val activity = activity as AppOnboardingActivity
                activity.navigateToNext(AppOnboardScreen.WELCOME)
            })
        }
    }
}