package com.sohohouse.seven.onboarding.benefits

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.MembershipUtils
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.main.MainActivity
import com.sohohouse.seven.memberonboarding.induction.booking.InductionBookingActivity
import com.sohohouse.seven.network.core.models.Account
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class OnboardingBenefitsViewModel @Inject constructor(
    private val accountInteractor: AccountInteractor,
    private val userManager: UserManager,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher,
    private val stringProvider: StringProvider
) : BaseViewModel(analyticsManager, dispatcher),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    private val _items = MutableLiveData<List<BenefitAdapterItem>>()
    val items: LiveData<List<BenefitAdapterItem>>
        get() = _items

    private val _navigation = MutableLiveData<Intent>()
    val navigation: LiveData<Intent>
        get() = _navigation

    fun getBenefits() {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)

            accountInteractor.getCompleteAccountV2().fold(
                ifError = { showError() },
                ifValue = { onSuccess(it) },
                ifEmpty = { _items.postValue(emptyList()) })
            setLoadingState(LoadingState.Idle)
        }
    }

    fun onClickContinue(context: Context) {
        userManager.hasSeenOnboardingBenefitsScreen = true

        val subscriptionType = accountInteractor.getCompleteAccountV2().fold(
            ifError = { showError().run { return } },
            ifEmpty = { showError().run { return } },
            ifValue = { it.subscriptionType })

        val intent = when {
            subscriptionType == SubscriptionType.NONE ||
                    subscriptionType == SubscriptionType.FRIENDS ||
                    subscriptionType == SubscriptionType.CWH -> {
                // set true to skip induction, no requirement for now
                userManager.isInducted = true
                userManager.isNotificationDialogComplete = true
                Intent(context, MainActivity::class.java)
            }
            !userManager.isInducted -> InductionBookingActivity.getIntent(context)
            else -> Intent(context, MainActivity::class.java)
        }
        _navigation.postValue(intent)
    }

    private fun onSuccess(account: Account) {
        val subscriptionType = account.subscriptionType
        when (subscriptionType) {
            SubscriptionType.NONE -> {
                _items.postValue(emptyList())
            }
            SubscriptionType.FRIENDS -> {
                _items.postValue(
                    listOf(
                        buildMembershipCardData(account),
                        BenefitHeaderItem(),
                        BenefitItem.FoodBeverage(
                            R.string.onboarding_benefits_food_beverage_title,
                            R.string.onboarding_benefits_food_beverage_friends
                        ),
                        BenefitItem.Spa(
                            R.string.onboarding_benefits_spa_title,
                            R.string.onboarding_benefits_spa_friends
                        ),
                        BenefitItem.Events(
                            R.string.onboarding_benefits_events_title,
                            R.string.onboarding_benefits_events_friends
                        ),
                        BenefitItem.RoomBookings(
                            R.string.onboarding_benefits_room_bookings_title,
                            R.string.onboarding_benefits_room_bookings_friends
                        ),
                        BenefitItem.SohoHome(
                            R.string.onboarding_benefits_soho_home_title,
                            R.string.onboarding_benefits_soho_home_friends
                        )
                    )
                )
            }
            else -> {
                val localHouseName = account.localHouse?.name ?: ""
                _items.postValue(
                    listOf(
                        buildMembershipCardData(account),
                        BenefitHeaderItem(),
                        if (subscriptionType == SubscriptionType.LOCAL) {
                            BenefitItem.Houses.LocalHouse(localHouseName)
                        } else {
                            BenefitItem.Houses.EveryHouse()
                        },
                        BenefitItem.Events(),
                        BenefitItem.RoomBookings(),
                        BenefitItem.WellBeing(),
                        BenefitItem.FoodBeverage()
                    )
                )
            }
        }
    }

    private fun buildMembershipCardData(account: Account): MembershipCardItem {
        return MembershipCardItem(
            subscriptionType = account.subscriptionType,
            membershipDisplayName = account.membershipDisplayName,
            memberName = stringProvider.getString(R.string.more_membership_name_label)
                .replaceBraces(
                    account.firstName ?: "",
                    account.lastName ?: ""
                ),
            membershipId = MembershipUtils.formatMembershipNumber(account.id),
            shortCode = account.shortCode,
            profileImageUrl = account.profile?.imageUrl,
            loyaltyId = account.loyaltyId,
            isStaff = account.isStaff
        )
    }
}
