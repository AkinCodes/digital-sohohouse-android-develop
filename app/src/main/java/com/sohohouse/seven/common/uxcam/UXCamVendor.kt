package com.sohohouse.seven.common.uxcam

import com.sohohouse.seven.BuildConfig
import com.sohohouse.seven.common.utils.BuildVariantConfig
import com.uxcam.UXCam
import com.uxcam.datamodel.UXConfig
import timber.log.Timber

class UXCamVendor {

    private val TAG = "UXCamVendor"

    fun setUp() {
        //Initialize UXCam
        val key =
            if (BuildConfig.DEBUG) BuildVariantConfig.STAGING_UX_CAM_APP_KEY else BuildVariantConfig.UX_CAM_APP_KEY
        if (!UXCam.isRecording()) {
            val config = UXConfig.Builder(key)
                .enableAutomaticScreenNameTagging(false)
                .enableImprovedScreenCapture(false)
                .build()
            UXCam.optOutOverall()
            UXCam.startWithConfiguration(config)
        }
    }

    private fun initializeUXCam(key: String) {
        if (!UXCam.isRecording()) {
            val config = UXConfig.Builder(key)
                .enableAutomaticScreenNameTagging(false)
                .enableImprovedScreenCapture(false)
                .build()
            UXCam.optOutOverall()
            UXCam.startWithConfiguration(config)
        }
    }

    fun checkOptInStatusAndStartSession() {
        if (!UXCam.optInOverallStatus()) {
            UXCam.optInOverall()
            UXCam.startNewSession()
        } else if (!UXCam.isRecording()) {
            UXCam.startNewSession()
        } else {
            Timber.d(TAG, "session is already being recorded")
        }
    }

    fun setScreenName(screenName: String) {
        UXCam.tagScreenName(screenName)
    }

    fun finishRecording() {
        UXCam.stopSessionAndUploadData()
    }

    fun cancelRecording() {
        UXCam.cancelCurrentSession()
    }
}