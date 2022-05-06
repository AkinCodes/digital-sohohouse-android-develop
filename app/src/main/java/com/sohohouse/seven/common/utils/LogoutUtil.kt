package com.sohohouse.seven.common.utils

import android.content.Context
import android.content.Intent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.book.filter.BookFilterManager
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.dagger.appComponent
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.user.UserSessionManager
import com.sohohouse.seven.welcome.WelcomeActivity
import com.uxcam.UXCam
import javax.inject.Inject


class LogoutUtil @Inject constructor(
    private val context: Context,
    private val analyticsManager: AnalyticsManager,
//                                     private val houseManager: HouseManager,  //FIXME cyclical dependency
    private val bookFilterManager: BookFilterManager,
//                                     private val exploreCategoryManager: ExploreCategoryManager,  //FIXME cyclical dependency
//                                     private val categoryInteractor: CategoryInteractor,  //FIXME cyclical dependency
//                                     private val profileRepository: ProfileRepository,    //FIXME cyclical dependency
    private val userSessionManager: UserSessionManager,
    private val userManager: UserManager
) {

    fun logout(shouldGoToSignIn: Boolean = true) {

        resetCurrentData()

        if (shouldGoToSignIn) {
            val intent = Intent(context, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    private fun resetCurrentData() {
        MarketingCloudSdk.requestSdk { sdk ->
            // Check is needed for testing, the notificationManager can actually be null and cause npe
            @Suppress("SENSELESS_COMPARISON")
            if (sdk.notificationManager != null) {
                sdk.notificationManager.setShouldShowNotificationListener { false }
            }
        }
        analyticsManager.logEventAction(AnalyticsManager.Action.LogoutConfirm)
        bookFilterManager.clearData()
        appComponent.exploreCategoryManager.clearData() //TODO use injected dependency when cyclical dependency issue resolved
        appComponent.categoryInteractor.clearData() //TODO use injected dependency when cyclical dependency issue resolved
        userManager.clearData()
        appComponent.chatChannelsRepo.clear()
        userSessionManager.logout()
        FirebaseCrashlytics.getInstance().setUserId("")
        appComponent.firebaseRegistrationService.unregisterFcmToken()

        context.cacheDir?.deleteRecursively()
        context.externalCacheDir?.deleteRecursively()
        UXCam.stopSessionAndUploadData()
        UXCam.setUserIdentity(FeatureFlags.UxCam.UXCAM_UNAUTHENTICATED_USER_IDENTITY)
    }

}