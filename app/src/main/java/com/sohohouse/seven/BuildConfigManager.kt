package com.sohohouse.seven

import android.content.Context
import android.content.SharedPreferences
import com.sohohouse.seven.common.utils.BuildVariantConfig.AUTH_APPLICATION_ID
import com.sohohouse.seven.common.utils.BuildVariantConfig.AUTH_CLIENT_SECRET
import com.sohohouse.seven.common.utils.BuildVariantConfig.AUTH_HOSTNAME
import com.sohohouse.seven.common.utils.BuildVariantConfig.CORE_API_HOSTNAME
import com.sohohouse.seven.common.utils.BuildVariantConfig.FORCE_UPDATE_HOSTNAME
import com.sohohouse.seven.common.utils.BuildVariantConfig.SEND_BIRD_APP_KEY
import com.sohohouse.seven.common.utils.BuildVariantConfig.SEND_BIRD_BASE_URL
import com.sohohouse.seven.common.utils.BuildVariantConfig.SOHO_FORGOT_PW_URL
import com.sohohouse.seven.common.utils.BuildVariantConfig.SOHO_WEB_HOSTNAME
import com.sohohouse.seven.common.utils.BuildVariantConfig.STAGING_AUTH_APPLICATION_ID
import com.sohohouse.seven.common.utils.BuildVariantConfig.STAGING_AUTH_CLIENT_SECRET
import com.sohohouse.seven.common.utils.BuildVariantConfig.STAGING_AUTH_HOSTNAME
import com.sohohouse.seven.common.utils.BuildVariantConfig.STAGING_CORE_API_HOSTNAME
import com.sohohouse.seven.common.utils.BuildVariantConfig.STAGING_FORCE_UPDATE_HOSTNAME
import com.sohohouse.seven.common.utils.BuildVariantConfig.STAGING_SEND_BIRD_APP_KEY
import com.sohohouse.seven.common.utils.BuildVariantConfig.STAGING_SEND_BIRD_BASE_URL
import com.sohohouse.seven.common.utils.BuildVariantConfig.STAGING_SOHO_FORGOT_PW_URL
import com.sohohouse.seven.common.utils.BuildVariantConfig.STAGING_SOHO_WEB_HOSTNAME

class BuildConfigManager(context: Context) {

    private val BUILD_CONFIGURATION = "buildConfiguration"
    private val IS_STAGING_KEY = "isStaging"

    private val sharedpreferences: SharedPreferences =
        context.getSharedPreferences(BUILD_CONFIGURATION, 0)

    val isCurrentlyStaging: Boolean
        get() {
            return coreHostName.equals(STAGING_CORE_API_HOSTNAME)
        }

    var isStaging: Boolean
        get() {
            return sharedpreferences.getBoolean(IS_STAGING_KEY, BuildConfig.DEBUG)
        }
        set(value) {
            val editor = sharedpreferences.edit()
            editor?.putBoolean(IS_STAGING_KEY, value)
            editor?.apply()
        }

    val authHostName: String
        get() {
            if (!BuildConfig.DEBUG) return AUTH_HOSTNAME
            return if (isStaging) STAGING_AUTH_HOSTNAME else AUTH_HOSTNAME
        }

    val coreHostName: String
        get() {
            if (!BuildConfig.DEBUG) return CORE_API_HOSTNAME
            return if (isStaging) STAGING_CORE_API_HOSTNAME else CORE_API_HOSTNAME
        }

    val sendBirdBaseUrl: String
        get() {
            if (!BuildConfig.DEBUG) return SEND_BIRD_BASE_URL
            return if (isStaging) STAGING_SEND_BIRD_BASE_URL else SEND_BIRD_BASE_URL
        }

    val sendBirdAppKey: String
        get() {
            if (!BuildConfig.DEBUG) return SEND_BIRD_APP_KEY
            return if (isStaging) STAGING_SEND_BIRD_APP_KEY else SEND_BIRD_APP_KEY
        }

    val webHostName: String
        get() {
            if (!BuildConfig.DEBUG) return SOHO_WEB_HOSTNAME
            return if (isStaging) STAGING_SOHO_WEB_HOSTNAME else SOHO_WEB_HOSTNAME
        }

    val forceUpdateHostName: String
        get() {
            if (!BuildConfig.DEBUG) return FORCE_UPDATE_HOSTNAME
            return if (isStaging) STAGING_FORCE_UPDATE_HOSTNAME else FORCE_UPDATE_HOSTNAME
        }

    val clientSecret: String
        get() {
            if (!BuildConfig.DEBUG) return AUTH_CLIENT_SECRET
            return if (isStaging) STAGING_AUTH_CLIENT_SECRET else AUTH_CLIENT_SECRET
        }

    val applicationId: String
        get() {
            if (!BuildConfig.DEBUG) return AUTH_APPLICATION_ID
            return if (isStaging) STAGING_AUTH_APPLICATION_ID else AUTH_APPLICATION_ID
        }

    val forgotPasswordUrl: String
        get() {
            if (!BuildConfig.DEBUG) return SOHO_FORGOT_PW_URL
            return if (isStaging) STAGING_SOHO_FORGOT_PW_URL else SOHO_FORGOT_PW_URL
        }


}