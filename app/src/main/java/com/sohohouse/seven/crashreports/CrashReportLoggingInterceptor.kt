package com.sohohouse.seven.crashreports

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.common.user.UserManager
import okhttp3.logging.HttpLoggingInterceptor

class CrashReportLogger(private val userManager: UserManager) : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        if (userManager.didConsentAnalytics) {
            FirebaseCrashlytics.getInstance().log(message)
        }
    }

}