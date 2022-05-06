package com.sohohouse.seven.authentication

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.R
import com.sohohouse.seven.accountstatus.AccountStatusActivity
import com.sohohouse.seven.apponboarding.AppOnboardingActivity
import com.sohohouse.seven.apponboarding.data.OnboardingDataActivity
import com.sohohouse.seven.apponboarding.optinrecommendations.LandingOptInRecommendationsActivity
import com.sohohouse.seven.apponboarding.terms.OnboardingTermsActivity
import com.sohohouse.seven.authentication.NotificationType.*
import com.sohohouse.seven.book.BookFragment.Companion.EXPLORE_NOTIFICATION_EVENT_ID
import com.sohohouse.seven.branding.AppIconActivity
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder.APPS_SCHEME
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder.APP_AUTHORITY
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder.AUTHORITY_PROFILE
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder.PATH_MY_PROFILE
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder.PATH_PROFILE
import com.sohohouse.seven.common.user.AnalyticsConsent
import com.sohohouse.seven.common.user.MembershipStatus.CHASING
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.user.UserSessionManager
import com.sohohouse.seven.connect.message.chat.ChatActivity
import com.sohohouse.seven.housepay.terms.HousePayTermActivity
import com.sohohouse.seven.intro.IntroActivity
import com.sohohouse.seven.intro.profile.PrepopulateProfileActivity
import com.sohohouse.seven.main.MainActivity
import com.sohohouse.seven.main.MainActivity.Companion.NOTIFICATION_ERROR
import com.sohohouse.seven.memberonboarding.induction.booking.InductionBookingActivity
import com.sohohouse.seven.splash.forceupdate.ForceUpdateActivity
import com.sohohouse.seven.splash.maintenance.ServerMaintenanceActivity
import com.sohohouse.seven.welcome.WelcomeActivity

class AuthenticationFlowManager constructor(
    private val userSessionManager: UserSessionManager,
    private val userManager: UserManager,
    private val featureFlags: FeatureFlags
) {
    fun navigateFrom(
        context: Context,
        chaseComplete: Boolean = false,
        payload: String = "",
        notificationType: NotificationType = NOT_NOTIFICATION,
        launcherComponentEnabled: Boolean = true,
        reviewProfile: Boolean = false
    ): Intent = when {
        !userSessionManager.isLoggedIn -> Intent(context, WelcomeActivity::class.java)
        !launcherComponentEnabled -> Intent(context, AppIconActivity::class.java)
        featureFlags.checkEmailVerified && !userManager.isEmailVerified -> VerifyAccountActivity.getIntent(
            context,
            userManager.accountId
        )
        !userManager.didConsentTermsConditions -> Intent(
            context,
            OnboardingTermsActivity::class.java
        )
        !userManager.didConsentHousePayTermsConditions
                && !userManager.isAppOnboardingComplete
                && featureFlags.housePay -> Intent(
            context,
            HousePayTermActivity::class.java
        )
        userManager.analyticsConsent == AnalyticsConsent.NONE -> Intent(
            context,
            OnboardingDataActivity::class.java
        )
        !userManager.canAccessApp -> Intent(context, AccountStatusActivity::class.java)
        userManager.subscriptionType == SubscriptionType.NONE -> MainActivity.getIntent(context)
        userManager.membershipStatus == CHASING && !chaseComplete -> Intent(
            context,
            AccountStatusActivity::class.java
        )
        userManager.isAppOnboardingNeeded -> Intent(context, AppOnboardingActivity::class.java)
        !userManager.hasSeenOnboardingWelcomeScreen -> Intent(context, IntroActivity::class.java)
        userManager.shouldShowLandingOptInRecommendations -> Intent(
            context,
            LandingOptInRecommendationsActivity::class.java
        )
        !userManager.isInducted -> Intent(context, InductionBookingActivity::class.java)
        notificationType == EVENT || notificationType == ERROR -> navigateFromEventNotification(
            payload,
            notificationType,
            context
        )
        notificationType == MESSAGE || notificationType == NEW_MESSAGE_REQUEST -> navigateFromMessageNotification(
            context,
            payload
        )
        FeatureFlags.PREPOPULATE_PROFILE && userManager.confirmProfile -> Intent(
            context,
            PrepopulateProfileActivity::class.java
        )
        reviewProfile -> {
            MainActivity.getCleanIntent(context).apply {
                if (payload.contains(AUTHORITY_PROFILE)) this.data = Uri.parse(payload)
                else {
                    // TODO improve me: Set up a proper deeplink to trigger profile viewer
                    this.data = Uri.Builder().scheme(APPS_SCHEME)
                        .authority(APP_AUTHORITY)
                        .appendPath(PATH_PROFILE)
                        .appendPath(PATH_MY_PROFILE)
                        .build()
                }
            }
        }
        else -> MainActivity.getIntent(context)
    }

    private fun navigateFromEventNotification(
        eventId: String,
        type: NotificationType,
        context: Context
    ): Intent {
        return MainActivity.getIntent(context, R.id.menu_book).apply {
            when (type) {
                EVENT -> putExtra(EXPLORE_NOTIFICATION_EVENT_ID, eventId)
                ERROR -> putExtra(NOTIFICATION_ERROR, true)
                else -> {
                }
            }
        }
    }

    private fun navigateFromMessageNotification(context: Context, channelUrl: String): Intent {
        return ChatActivity.newIntentViaURL(context, channelUrl, "")
    }

    fun navigateToForceUpdate(context: Context): Intent =
        Intent(context, ForceUpdateActivity::class.java)

    fun navigateToServerMaintenance(context: Context): Intent =
        Intent(context, ServerMaintenanceActivity::class.java)

}
