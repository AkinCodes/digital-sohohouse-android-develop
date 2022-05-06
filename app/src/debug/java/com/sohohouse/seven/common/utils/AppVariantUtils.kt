package com.sohohouse.seven.common.utils

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import com.sohohouse.seven.BuildConfig
import com.sohohouse.seven.base.BaseActivity

object AppVariantUtils {
    private const val DEBUG_BUILD_TYPE = "debug"

    @SuppressLint("SourceLockedOrientationActivity")
    infix fun lockActivityOrientation(activity: BaseActivity) {
        // for any non-debug builds (i.e QA) lock the orientation
        // release has it's own AppVariantUtils object
        if (BuildConfig.BUILD_TYPE != DEBUG_BUILD_TYPE) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}