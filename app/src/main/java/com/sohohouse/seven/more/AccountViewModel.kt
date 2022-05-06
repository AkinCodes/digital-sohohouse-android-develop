package com.sohohouse.seven.more

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.apihelpers.SohoWebHelper.KickoutType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.LogoutUtil
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.common.views.webview.openWebView
import com.sohohouse.seven.connect.mynetwork.MyConnectionsActivity
import com.sohohouse.seven.more.AccountMenu.*
import com.sohohouse.seven.profile.NotAvailable
import com.sohohouse.seven.profile.ProfileRepository
import com.sohohouse.seven.profile.share.ShareProfileBottomSheet
import com.sohohouse.seven.profile.view.ProfileViewerFragment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val viewInfo: AccountViewInfo,
    private val logoutUtil: LogoutUtil,
    private val userManager: UserManager,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel(analyticsManager, dispatcher),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    private val flowManager = AccountFlowManager()

    private val _profile: MutableLiveData<ProfileItem> = MutableLiveData()
    val profile: LiveData<ProfileItem>
        get() = _profile

    init {
        fetchAccount()
    }

    override fun onScreenViewed() {
        super.onScreenViewed()
        notifyProfileChange(_profile.value?.copy(imageUrl = userManager.profileImageURL) ?: return)
    }

    fun getViewInfo(): List<AccountMenu> = viewInfo.menus

    fun fetchAccount() {
        viewModelScope.launch(viewModelContext) {
            setLoading()
            profileRepository.getMyAccountWithProfileV2()
                .ifValue {
                    notifyProfileChange(
                        ProfileItem(
                            it.profile ?: return@ifValue,
                            NotAvailable,
                            null
                        )
                    )
                }
                .ifError { showError() }
            setIdle()
        }
    }

    fun onPreferenceClick(activity: FragmentActivity, key: String?): Boolean {
        logEvent(key)

        when (key) {
            CONTACT_US.key -> {
                openWebView(activity.supportFragmentManager, KickoutType.CONTACT_SUPPORT)
                return true
            }
            TERMS_AND_POLICIES.key -> {
                openWebView(activity.supportFragmentManager, KickoutType.TERMS_AND_POLICIES)
                return true
            }
            FAQS.key -> {
                openWebView(activity.supportFragmentManager, KickoutType.FAQS)
                return true
            }
            FAQS_FRIENDS.key -> {
                openWebView(activity.supportFragmentManager, KickoutType.FAQS_FRIENDS)
                return true
            }
            PROFILE.key, LANDING_VIEW_PROFILE.key -> {
                ProfileViewerFragment.withProfile(
                    profile = _profile.value ?: return true,
                    skipCollapsed = true
                ).showSafe(activity.supportFragmentManager, ProfileViewerFragment.TAG)
                return true
            }
            MY_NETWORK.key -> {
                activity.startActivity(Intent(activity, MyConnectionsActivity::class.java))
                return true
            }
            LANDING_SHARE_PROFILE.key -> {
                ShareProfileBottomSheet().showSafe(
                    activity.supportFragmentManager,
                    ShareProfileBottomSheet.TAG
                )
            }
        }

        flowManager.transitionFrom(activity, key)?.let { intent ->
            activity.startActivity(intent)
            return true
        }
        return false
    }

    fun logout() {
        logoutUtil.logout()
    }

    private fun notifyProfileChange(profile: ProfileItem) {
        _profile.postValue(profile)
    }

    private fun logEvent(key: String?) {
        when (key) {
            PROFILE.key -> {
                analyticsManager.logEventAction(AnalyticsManager.Action.AccountViewProfile)
            }
            LANDING_VIEW_PROFILE.key -> {
                analyticsManager.logEventAction(AnalyticsManager.Action.AccountLandingViewProfile)
            }
            MEMBERSHIP_DETAILS.key -> {
                analyticsManager.logEventAction(AnalyticsManager.Action.AccountMembership)
            }
            PAYMENT_METHODS.key -> {
                analyticsManager.logEventAction(AnalyticsManager.Action.AccountPaymentMethods)
            }
            BOOKINGS.key -> {
                analyticsManager.logEventAction(AnalyticsManager.Action.AccountBookings)
            }
            HOUSE_PREFERENCES.key -> {
                analyticsManager.logEventAction(AnalyticsManager.Action.AccountFavouriteHouses)
            }
            SETTINGS.key -> {
                analyticsManager.logEventAction(AnalyticsManager.Action.AccountNotificationPreferences)
            }
            CONTACT_US.key -> {
                analyticsManager.logEventAction(AnalyticsManager.Action.AccountContactUs)
            }
            TERMS_AND_POLICIES.key -> {
                analyticsManager.logEventAction(AnalyticsManager.Action.AccountTermsAndPolicies)
            }
            GUEST_INVITATIONS.key -> {
                analyticsManager.logEventAction(
                    AnalyticsManager.Action.AccountGuestLandingOpen,
                    AnalyticsManager.HouseGuest.buildParams(membershipType = userManager.membershipType)
                )
            }
            LANDING_SHARE_PROFILE.key -> {
                analyticsManager.logEventAction(AnalyticsManager.Action.AccountLandingShareProfile)
            }
//            ADD_HOUSE_PAY.key -> {
//                analyticsManager.logEventAction(AnalyticsManager.Action)
//            }
        }
    }
}