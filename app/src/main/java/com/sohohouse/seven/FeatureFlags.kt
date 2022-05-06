package com.sohohouse.seven

import com.sohohouse.seven.common.interactors.AccountInteractor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeatureFlags @Inject constructor(
    private val accountInteractor: AccountInteractor,
    private val buildConfigManager: BuildConfigManager,
) {

    companion object {
        const val TRAFFIC_LIGHTS_SHOW_CONNECTIONS_ONLY = true
        const val PREPOPULATE_PROFILE = true
    }

    object Ids {
        const val FEATURE_ID_GUEST_REGISTRATION = "guest_registration"
        const val FEATURE_ID_HOUSE_PAY = "house_pay"
    }

    object UxCam {
        const val UXCAM_IDENTITY_TRACKING_ENABLED = "UXCAM_IDENTITY_TRACKING_ENABLED"
        const val UXCAM_UNAUTHENTICATED_USER_IDENTITY = "Unauthenticated User"
    }

    var checkEmailVerified: Boolean = false ///TODO re-enable when ready
    //        get() = !BuildConfig.DEBUG || !buildConfigManager.isCurrentlyStaging

    var guestRegistration: Boolean = false
        get() {
            return accountFeatures()?.any {
                it.id == Ids.FEATURE_ID_GUEST_REGISTRATION
            } ?: false
        }

    private fun accountFeatures() = accountInteractor
        .userAccount
        ?.features
        ?.toList()

    val housePay: Boolean
        get() = BuildConfig.DEBUG || buildConfigManager.isStaging
    //    {
    //        return accountFeatures()?.any {
    //            it.id == Ids.FEATURE_ID_HOUSE_PAY
    //        } ?: false
    //    }

    var benefitsFilterByCity: Boolean = true

    var googlePay = false   //for now

    val psd2Payments: Boolean
        get() = BuildConfig.DEBUG || buildConfigManager.isStaging
}