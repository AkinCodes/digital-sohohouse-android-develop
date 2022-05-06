package com.sohohouse.seven.common.utils

import android.app.Application
import android.content.Context

@Suppress("UNUSED_PARAMETER")
object LeakCanaryVariantUtils {

    infix fun isAnalyzerProcess(context: Context): Boolean {
        // no leak canary
        return false
    }

    infix fun install(application: Application) {
        // do nothing
    }

    infix fun watch(watchedReference: Any) {
        // do nothing
    }
}