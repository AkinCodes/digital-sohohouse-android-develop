package com.sohohouse.seven.common.views.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import com.sohohouse.seven.App.Companion.buildConfigManager
import com.sohohouse.seven.BuildConfig
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.replaceBraces
import com.sohohouse.seven.debug.DebugActivity

class AppVersionPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : Preference(context, attrs, defStyleAttr, defStyleRes) {

    init {
        isPersistent = false

        val version = if (buildConfigManager.isCurrentlyStaging) {
            "${BuildConfig.VERSION_NAME} - ${DebugActivity.STAGING_STRING_KEY}"
        } else {
            BuildConfig.VERSION_NAME
        }
        val title = context.getString(R.string.more_app_version_label).replaceBraces(version)
        setTitle(title)
    }
}