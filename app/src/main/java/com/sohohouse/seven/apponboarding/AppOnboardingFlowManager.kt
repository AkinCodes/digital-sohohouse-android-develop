package com.sohohouse.seven.apponboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sohohouse.seven.R
import com.sohohouse.seven.apponboarding.houseboard.OnboardingHouseBoardFragment
import com.sohohouse.seven.apponboarding.houseboardpost.OnboardingHouseBoardPostFragment
import com.sohohouse.seven.apponboarding.housepreferences.OnboardingHousePreferencesFragment
import com.sohohouse.seven.apponboarding.welcome.AppOnboardingWelcomeFragment
import com.sohohouse.seven.common.user.SubscriptionType.*
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.intro.IntroActivity
import javax.inject.Inject
import javax.inject.Singleton

enum class AppOnboardScreen {
    INTRO, WELCOME, TAILOR, HOUSE_BOARD, HOUSE_BOARD_POST, NOTIFICATION,
}

@Singleton
class AppOnboardingFlowManager @Inject constructor(
    private val userManager: UserManager
) {
    fun navigateToNext(activity: AppCompatActivity, currentScreen: AppOnboardScreen?) {
        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (userManager.subscriptionType) {
            EVERY_PLUS, CWH, EVERY, LOCAL -> {
                when (currentScreen) {
                    AppOnboardScreen.INTRO -> replaceFragment(
                        activity,
                        AppOnboardingWelcomeFragment()
                    )
                    AppOnboardScreen.WELCOME -> navigate(
                        activity,
                        currentScreen,
                        OnboardingHousePreferencesFragment()
                    )
                    AppOnboardScreen.TAILOR -> navigate(
                        activity,
                        currentScreen,
                        OnboardingHouseBoardFragment()
                    )
                    AppOnboardScreen.HOUSE_BOARD -> navigate(
                        activity,
                        currentScreen,
                        OnboardingHouseBoardPostFragment()
                    )
                    AppOnboardScreen.HOUSE_BOARD_POST -> navigateToNewOnboardingActivity(activity)
                }
            }
            FRIENDS -> {
                when (currentScreen) {
                    AppOnboardScreen.INTRO -> replaceFragment(
                        activity,
                        AppOnboardingWelcomeFragment()
                    )
                    AppOnboardScreen.WELCOME -> navigateToNewOnboardingActivity(activity)
                }
            }
        }
    }

    private fun replaceFragment(activity: AppCompatActivity, nextFragment: Fragment) {
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.onboarding_container, nextFragment).commit()
    }

    private fun navigate(activity: AppCompatActivity, current: AppOnboardScreen, next: Fragment) {
        activity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.onboarding_container, next)
            .addToBackStack(current.name)
            .commit()
    }

    private fun navigateToNewOnboardingActivity(activity: AppCompatActivity) {
        activity.startActivity(Intent(activity, IntroActivity::class.java))
        activity.finish()
    }
}
