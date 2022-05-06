package com.sohohouse.seven.intro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.R
import com.sohohouse.seven.authentication.AuthenticationFlowManager
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.analytics.AnalyticsManager.Parameters.TimeSpent
import com.sohohouse.seven.common.analytics.AnalyticsManager.Parameters.TotalTimeSpent
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.MembershipUtils
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.utils.ZipRequestsUtil
import com.sohohouse.seven.intro.adapter.*
import com.sohohouse.seven.network.core.models.Account
import com.sohohouse.seven.network.core.request.PatchMembershipAttributesRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class IntroViewModel @Inject constructor(
    private val accountInteractor: AccountInteractor,
    private val userManager: UserManager,
    private val stringProvider: StringProvider,
    private val flowManager: AuthenticationFlowManager,
    private val zipRequestsUtil: ZipRequestsUtil,
    analyticsManager: AnalyticsManager,
    private val dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    private val _items: MutableLiveData<List<IntroItem>> = MutableLiveData()
    val items: LiveData<List<IntroItem>>
        get() = _items

    val subscriptionType: SubscriptionType
        get() = userManager.subscriptionType

    private val _intent: MutableLiveData<Intent> = MutableLiveData()
    val intent: LiveData<Intent>
        get() = _intent

    private val mapTimeSpent: HashMap<Int, Long> = hashMapOf()

    private var lastTimestamp: Long = 0

    fun getUserProfile() {
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            setLoadingState(LoadingState.Loading)

            accountInteractor.getCompleteAccountV2().fold(
                ifError = { showError() },
                ifEmpty = { showError() },
                ifValue = { account ->
                    val items = mutableListOf<IntroItem>()
                    items.add(createCardItem(account))
                    items.addAll(createGuidelines(account.subscriptionType))
                    _items.postValue(items)
                }
            )
            setLoadingState(LoadingState.Idle)
        }
    }

    private fun createCardItem(account: Account): IntroItem {
        return IntroLanding(
            welcomeHeader = getWelcomeTitle(
                account.subscriptionType,
                account.firstName ?: ""
            ),
            welcomeMessage = getWelcomeMessage(
                account.subscriptionType,
                account.localHouse?.name ?: ""
            ),
            membershipId = MembershipUtils.formatMembershipNumber(account.id),
            shortCode = account.shortCode,
            subscriptionType = account.subscriptionType,
            membershipDisplayName = account.membershipDisplayName,
            memberName = stringProvider.getString(R.string.more_membership_name_label)
                .replaceBraces(account.firstName ?: "", account.lastName ?: ""),
            houseLogoUrl = account.venueIcon,
            profileImageUrl = account.imageUrl,
            loyaltyId = account.loyaltyId,
            isStaff = account.isStaff == true
        )
    }

    private fun createGuidelines(subscriptionType: SubscriptionType): List<IntroItem> {
        return when (subscriptionType) {
            SubscriptionType.FRIENDS -> listOf(StayWithUs, SpacesForFriends, MemberBenefits)
            else -> emptyList()
        }
    }

    private fun getWelcomeTitle(subscriptionType: SubscriptionType, firstName: String): String {
        return when (subscriptionType) {
            SubscriptionType.FRIENDS -> stringProvider.getString(R.string.onboarding_welcome_friends)
                .replaceBraces(firstName)
            else -> stringProvider.getString(R.string.onboarding_welcome)
        }
    }

    private fun getWelcomeMessage(subscriptionType: SubscriptionType, venueName: String): String {
        return when (subscriptionType) {
            SubscriptionType.EVERY,
            SubscriptionType.EVERY_PLUS -> stringProvider.getString(R.string.onboarding_welcome_membership_every_house)
            SubscriptionType.LOCAL -> stringProvider.getString(R.string.onboarding_welcome_membership_local_house)
                .replaceBraces(venueName, venueName)
            SubscriptionType.CWH -> stringProvider.getString(R.string.onboarding_welcome_membership_cwh)
            SubscriptionType.FRIENDS -> stringProvider.getString(R.string.onboarding_welcome_membership_friends)
            else -> ""
        }
    }

    fun onReload(context: Context) {
        if (_items.value.isNullOrEmpty()) {
            getUserProfile()
        } else {
            onCompleteIntro(context)
        }
    }

    fun onCompleteIntro(context: Context) {
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            userManager.hasSeenOnboardingWelcomeScreen = true
            userManager.hasSeenOnboardingBenefitsScreen = true
            userManager.isAppOnboardingNeeded = false

            when (subscriptionType) {
                SubscriptionType.NONE,
                SubscriptionType.FRIENDS,
                SubscriptionType.CWH -> {
                    setLoadingState(LoadingState.Loading)
                    zipRequestsUtil.issueApiCall(
                        PatchMembershipAttributesRequest(
                            inductedAt = Date()
                        )
                    ).fold(
                        ifError = { showError() },
                        ifValue = {
                            if (it.inductedAt == null) {
                                showError()
                            } else {
                                userManager.isInducted = it.inductedAt != null
                                _intent.postValue(flowManager.navigateFrom(context))
                            }
                        },
                        ifEmpty = { showError() }
                    )
                    setLoadingState(LoadingState.Idle)
                }
                else -> {
                    _intent.postValue(flowManager.navigateFrom(context))
                }
            }
        }
    }

    fun logTimeSpent(position: Int, includeTotalTimeSpent: Boolean = false) {
        val timestamp = System.currentTimeMillis()
        _items.value?.getOrNull(position)?.let { item ->
            val timeSpent = timestamp - lastTimestamp
            mapTimeSpent[position] = timeSpent

            analyticsManager.logEventAction(item.action, Bundle().apply {
                putLong(TimeSpent.value, timeSpent)
                if (includeTotalTimeSpent) {
                    putLong(TotalTimeSpent.value, mapTimeSpent.values.sum())
                }
            })
        }

        lastTimestamp = timestamp
    }
}