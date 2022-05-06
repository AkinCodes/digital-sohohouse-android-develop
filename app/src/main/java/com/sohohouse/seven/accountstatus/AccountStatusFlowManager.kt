package com.sohohouse.seven.accountstatus

import com.sohohouse.seven.R
import com.sohohouse.seven.authentication.AuthenticationFlowManager
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.MembershipStatus
import com.sohohouse.seven.common.user.UserSessionManager
import com.sohohouse.seven.common.utils.LogoutUtil
import com.sohohouse.seven.common.views.FullScreenPromptFragment
import javax.inject.Inject

class AccountStatusFlowManager @Inject constructor(
    private val userSessionManager: UserSessionManager,
    private val analyticsManager: AnalyticsManager,
    private val authenticationFlowManager: AuthenticationFlowManager,
    private val logoutUtil: LogoutUtil
) {

    private lateinit var membershipStatus: MembershipStatus

    fun navigateFrom(activity: AccountStatusActivity, membershipStatus: MembershipStatus) {
        this.membershipStatus = membershipStatus
        val fragment =
            when (membershipStatus) {
                MembershipStatus.CHASING -> {
                    analyticsManager.track(AnalyticsEvent.Authentication.Login.PaymentOverdue)
                    FullScreenPromptFragment.createInstance(
                        headerText = activity.getString(R.string.overdue_header),
                        supportingText = activity.getString(R.string.overdue_supporting),
                        primaryBtnText = activity.getString(R.string.overdue_cta),
                        secondaryBtnText = activity.getString(R.string.overdue_later_cta),
                        secondaryBtnVisibility = true
                    )
                }
                MembershipStatus.EXPIRED -> {
                    analyticsManager.track(AnalyticsEvent.Authentication.Login.Expired)
                    FullScreenPromptFragment.createInstance(
                        headerText = activity.getString(R.string.expired_header),
                        supportingText = activity.getString(R.string.expired_supporting),
                        primaryBtnText = activity.getString(R.string.expired_cta),
                        secondaryBtnText = activity.getString(R.string.locked_account_logout_cta),
                        secondaryBtnVisibility = true
                    )
                }
                MembershipStatus.FROZEN -> {
                    analyticsManager.track(AnalyticsEvent.Authentication.Login.MembershipOnHold)
                    FullScreenPromptFragment.createInstance(
                        headerText = activity.getString(R.string.frozen_header),
                        supportingText = activity.getString(R.string.frozen_supporting),
                        primaryBtnText = activity.getString(R.string.frozen_cta),
                        secondaryBtnText = activity.getString(R.string.locked_account_logout_cta),
                        secondaryBtnVisibility = true
                    )
                }
                MembershipStatus.SUSPENDED -> {
                    analyticsManager.track(AnalyticsEvent.Authentication.Login.MembershipSuspended)
                    FullScreenPromptFragment.createInstance(
                        headerText = activity.getString(R.string.suspended_header),
                        supportingText = activity.getString(R.string.suspended_supporting),
                        primaryBtnText = activity.getString(R.string.suspended_cta),
                        secondaryBtnText = activity.getString(R.string.locked_account_logout_cta),
                        secondaryBtnVisibility = true
                    )
                }
                MembershipStatus.RESIGNED -> {
                    analyticsManager.track(AnalyticsEvent.Authentication.Login.Resigned)
                    FullScreenPromptFragment.createInstance(
                        headerText = activity.getString(R.string.resigned_header),
                        supportingText = activity.getString(R.string.resigned_supporting),
                        primaryBtnText = activity.getString(R.string.resigned_cta),
                        secondaryBtnText = activity.getString(R.string.locked_account_logout_cta),
                        secondaryBtnVisibility = true
                    )
                }
                MembershipStatus.NONE -> {
                    FullScreenPromptFragment.createInstance(
                        headerText = activity.getString(R.string.no_membership_header),
                        supportingText = activity.getString(R.string.no_membership_supporting),
                        primaryBtnText = activity.getString(R.string.no_membership_cta),
                        secondaryBtnVisibility = false
                    )
                }
                else -> {
                    analyticsManager.track(AnalyticsEvent.Authentication.Login.AccountNotActive)
                    FullScreenPromptFragment.createInstance(
                        headerText = activity.getString(R.string.inactive_header),
                        supportingText = activity.getString(R.string.inactive_supporting),
                        primaryBtnText = activity.getString(R.string.inactive_cta),
                        secondaryBtnText = activity.getString(R.string.locked_account_logout_cta),
                        secondaryBtnVisibility = true
                    )
                }
            }
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.account_status_container, fragment).commit()
    }

    fun navigateFromPrimaryButton(activity: AccountStatusActivity) {
        when (membershipStatus) {
            MembershipStatus.CHASING -> {
                analyticsManager.track(AnalyticsEvent.Authentication.PaymentOverdue.UpdatePayment)
                activity.updatePayment()
            }
            MembershipStatus.NONE -> {
                activity.logOut()
            }
            else -> {
                analyticsManager.track(AnalyticsEvent.Authentication.MembershipOnHold.ContactSupport)
                activity.contactSupport()
            }
        }
    }

    fun navigateFromSecondaryButton(activity: AccountStatusActivity) {
        if (membershipStatus == MembershipStatus.CHASING) {
            analyticsManager.track(AnalyticsEvent.Authentication.PaymentOverdue.Continue)
            val intent = authenticationFlowManager.navigateFrom(activity, chaseComplete = true)
            activity.startActivity(intent)
        } else {
            logoutUtil.logout(shouldGoToSignIn = true)
        }
        activity.finish()
    }
}