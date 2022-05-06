package com.sohohouse.seven.common.utils

import android.content.pm.ActivityInfo
import com.sohohouse.seven.base.BaseActivity

object AppVariantUtils {
    @android.annotation.SuppressLint("SourceLockedOrientationActivity")
    infix fun lockActivityOrientation(activity: BaseActivity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}