package com.sohohouse.seven.accountstatus

import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.MembershipStatus
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.LogoutUtil
import javax.inject.Inject

class AccountStatusViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val flowManager: AccountStatusFlowManager,
    private val logoutUtil: LogoutUtil,
    private val userManager: UserManager
) : BaseViewModel(analyticsManager) {

    val paymentUpdateUrl: String get() = userManager.paymentUpdateUrl
    val membershipStatus: MembershipStatus get() = userManager.membershipStatus

    fun navigateFrom(activity: AccountStatusActivity, membershipStatus: MembershipStatus) {
        flowManager.navigateFrom(activity, membershipStatus)
    }

    fun navigateFromPrimaryButton(accountStatusActivity: AccountStatusActivity) {
        flowManager.navigateFromPrimaryButton(accountStatusActivity)
    }

    fun navigateFromSecondaryButton(accountStatusActivity: AccountStatusActivity) {
        flowManager.navigateFromSecondaryButton(accountStatusActivity)
    }

    fun logout(shouldGoToSignIn: Boolean) {
        logoutUtil.logout(shouldGoToSignIn)
    }


}