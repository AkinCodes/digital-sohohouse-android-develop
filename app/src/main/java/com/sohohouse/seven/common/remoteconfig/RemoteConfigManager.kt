package com.sohohouse.seven.common.remoteconfig

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.sohohouse.seven.BuildConfig
import com.sohohouse.seven.R
import timber.log.Timber

class RemoteConfigManager {
    private val TAG = "RemoteConfigManager"

    lateinit var remoteConfig: FirebaseRemoteConfig

    fun init(eventListener: EventListener) {
        remoteConfig = getFirebaseRemoteConfig(eventListener)
    }

    private fun getFirebaseRemoteConfig(eventListener: EventListener): FirebaseRemoteConfig {

        val remoteConfig = Firebase.remoteConfig

        val configSettings = remoteConfigSettings {
            if (BuildConfig.DEBUG) {
                minimumFetchIntervalInSeconds = 3600 // Kept 3600 for quick debug
            }
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetchAndActivate().addOnCompleteListener {
            Timber.d(TAG, "addOnCompleteListener")
            eventListener.onCompleteListener()
        }

        return remoteConfig
    }

    interface EventListener {
        fun onCompleteListener()
    }
}