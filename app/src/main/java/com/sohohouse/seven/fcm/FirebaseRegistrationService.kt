package com.sohohouse.seven.fcm

import com.google.firebase.messaging.FirebaseMessaging
import com.sohohouse.seven.common.error.ErrorReporter
import com.sohohouse.seven.common.utils.LogoutUtil
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.models.notification.DeviceRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRegistrationService @Inject constructor(private val sohoApiService: SohoApiService) {

    fun registerFcmToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener {
                when {
                    it.isSuccessful -> {
                        registerFCMTokenWithSH(it.result)
                    }
                    else -> {
                        val message = "Error getting FCM token"
                        Timber.e(it.exception?.message ?: message)
                        ErrorReporter.logException(
                            it.exception ?: Throwable(message)
                        )
                    }
                }
            }
    }

    fun unregisterFcmToken() {
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.tag(LogoutUtil::class.java.simpleName)
                    .d("Successfully deleted firebase token")
            } else {
                Timber.tag(LogoutUtil::class.java.simpleName).e("Failed to delete firebase token")
            }
        }
    }

    private fun registerFCMTokenWithSH(tokenResult: String) {
        CoroutineScope(Dispatchers.IO).launch {
            sohoApiService.registerDeviceForFCM(DeviceRegistration(tokenResult))
                .let {
                    when (it) {
                        is ApiResponse.Success<*> -> {
                            Timber.d("FCM token successfully registered with BE")
                        }
                        is ApiResponse.Error -> {
                            Timber.e("Failed to register FCM token with BE: ${it.message}")
                            ErrorReporter.logException(Throwable(it.message))
                        }
                    }
                }
        }
    }

}