package com.sohohouse.seven.common.error

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.network.core.BaseApiService

@Deprecated(message = "Use FirebaseCrashlytics.getInstance() directly")
object ErrorReporter : BaseApiService.NetworkErrorReporter {
    override fun logException(exception: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(exception)
    }
}