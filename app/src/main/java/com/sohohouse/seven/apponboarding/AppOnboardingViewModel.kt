package com.sohohouse.seven.apponboarding

import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.BaseActivity
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.dateToString
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.isSuccessful
import com.sohohouse.seven.network.core.models.AccountOnboarding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AppOnboardingViewModel @javax.inject.Inject constructor(
    val userManager: UserManager,
    analyticsManager: AnalyticsManager,
    private val onboardingFlowManager: AppOnboardingFlowManager,
    private val sohoApiService: SohoApiService,
    private val dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager) {

    fun navigateOnboardingScreen(activity: BaseActivity, currentScreen: AppOnboardScreen) {
        if (isValidScreenAndSubscriptionType(currentScreen)) {
            val houseMemberOnboardedDate =
                if (userManager.subscriptionType == SubscriptionType.EVERY) Date() else null
            val friendsMemberOnboardedDate =
                if (userManager.subscriptionType == SubscriptionType.FRIENDS) Date() else null

            viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
                val result = sohoApiService.patchAccountOnboarding(
                    AccountOnboarding(
                        convertDateToString(houseMemberOnboardedDate),
                        convertDateToString(friendsMemberOnboardedDate)
                    )
                )
                if (result.isSuccessful()) userManager.isAppOnboardingComplete = true
            }
        }
        onboardingFlowManager.navigateToNext(activity, currentScreen)
    }

    private fun convertDateToString(date: Date?): String {
        return date?.dateToString() ?: ""
    }

    private fun isValidScreenAndSubscriptionType(currentScreen: AppOnboardScreen): Boolean {
        return AppOnboardScreen.HOUSE_BOARD_POST == currentScreen || (SubscriptionType.FRIENDS == userManager.subscriptionType && AppOnboardScreen.WELCOME == currentScreen)
    }
}
