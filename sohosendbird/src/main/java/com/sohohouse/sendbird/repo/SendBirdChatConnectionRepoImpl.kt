package com.sohohouse.sendbird.repo

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sendbird.android.User
import com.sohohouse.sendbird.SendBirdHelper
import com.sohohouse.sendbird.data.ConnectionState
import com.sohohouse.seven.network.chat.ChatConnectionRepo
import com.sohohouse.seven.network.chat.model.MiniProfile
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.isSuccessful
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch

class SendBirdChatConnectionRepoImpl(
    private val sohoApiService: SohoApiService,
    private val sendBirdHelper: SendBirdHelper
) : ChatConnectionRepo {

    private var alreadyRunning = false
    private var tokenJob: Job = Job()

    private var connectionState = ConnectionState.notConnected()
    private var currentUser: User? = null

    override suspend fun connect(userData: MiniProfile) {
        if (!(alreadyRunning || connectionState.isValid)) {
            alreadyRunning = true
            CoroutineScope(currentCoroutineContext() + tokenJob).launch {
                val sendBirdProdAccessToken = sohoApiService.getSendBirdAccessToken()
                val accessToken =
                    if (sendBirdProdAccessToken.isSuccessful()) {
                        sendBirdProdAccessToken.response
                    } else {
                        alreadyRunning = false
                        tokenJob.cancel()
                        onGetTokenFail(sendBirdProdAccessToken)
                        throw RuntimeException("Sendbird: Unable to get Sendbird token")
                    }

                tokenJob.cancel()
                sendBirdHelper.disableAutoAccept()

                return@launch sendBirdHelper.connect(
                    userData,
                    accessToken,
                    currentCoroutineContext()
                ) { user ->
                    connectionState = ConnectionState(
                        token = accessToken.token,
                        expiresAt = accessToken.expirationDate
                    )
                    currentUser = user
                }
            }
        }

        tokenJob.join()
    }

    private fun onGetTokenFail(sendBirdProdAccessToken: ApiResponse.Error) {
        FirebaseCrashlytics.getInstance().apply {
            setCustomKey("Sendbird code", sendBirdProdAccessToken.code.toString())
            setCustomKey(
                "Sendbird message",
                sendBirdProdAccessToken.message.toString()
            )
            setCustomKey(
                "Sendbird response",
                sendBirdProdAccessToken.response.toString()
            )
            setCustomKey(
                "Sendbird error message",
                sendBirdProdAccessToken.firstErrorCode().toString()
            )
        }
    }

    override fun registerPushTokenForCurrentUser(token: String) {
        sendBirdHelper.registerPushTokenForCurrentUser(token)
    }

}