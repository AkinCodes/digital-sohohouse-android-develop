package com.sohohouse.sendbird

import com.sendbird.android.SendBird
import com.sendbird.android.SendBirdException
import com.sendbird.android.User
import com.sohohouse.seven.network.chat.model.MiniProfile
import com.sohohouse.seven.network.core.models.SendBirdToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SendBirdHelper {

    fun disableAutoAccept() {
        SendBird.setChannelInvitationPreference(false) {}
    }

    suspend fun connect(
        userData: MiniProfile,
        accessToken: SendBirdToken,
        coContext: CoroutineContext,
        onConnect: (user: User?) -> Unit,
    ) {
        return suspendCancellableCoroutine { cont ->
            SendBird.connect(
                userData.profileId, accessToken.token
            ) { user: User?, ex: SendBirdException? ->
                if (user != null && ex == null) {
                    onConnect(user)

                    CoroutineScope(coContext).launch {
                        updateUserData(userData, user)
                    }
                    if (cont.isActive) cont.resume(Unit)
                } else {
                    if (cont.isActive) cont.resumeWithException(generateEmptyUserException(ex))
                }
            }
        }
    }

    private fun generateEmptyUserException(ex: SendBirdException?): SendBirdException {
        val message = "[Returned user is null]"
        return ex?.let {
            SendBirdException(
                "${it.message} => $message",
                it.code
            )
        } ?: SendBirdException(message)
    }

    private suspend fun updateUserData(userData: MiniProfile, user: User) {
        val metaMap = mapOf(
            "staff" to userData.isStaff.toString(),
            "profile_image_url" to userData.profileImageUrl
        )
        return suspendCoroutine { cont ->
            user.updateMetaData(metaMap) { _, userUpdateEx ->
                if (userUpdateEx != null)
                    cont.resumeWithException(userUpdateEx)
            }

            SendBird.updateCurrentUserInfo(
                userData.nickName,
                userData.profileImageUrl
            ) { userUpdateEx2 ->
                if (userUpdateEx2 != null)
                    cont.resumeWithException(userUpdateEx2)
            }
        }
    }

    fun registerPushTokenForCurrentUser(token: String) {
        SendBird.registerPushTokenForCurrentUser(token) { _, _ ->
        }
    }
}