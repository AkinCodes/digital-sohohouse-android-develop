package com.sohohouse.seven.fcm.mappers

import com.google.firebase.messaging.RemoteMessage
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.getValue
import com.sohohouse.seven.common.navigation.NavigationScreen
import com.sohohouse.seven.fcm.MessageParams
import org.json.JSONObject
import javax.inject.Inject

class SendBirdNotificationPayloadMapper @Inject constructor() : NotificationPayloadMapper {
    override fun mapToMessageParams(remoteMessage: RemoteMessage): MessageParams {
        val sendBird = JSONObject(remoteMessage.data["sendbird"] ?: "")
        val channel = sendBird["channel"] as JSONObject

        return MessageParams(
            channel["channel_url"] as String,
            sendBird["push_title"] as String,
            sendBird["message"] as String,
            remoteMessage.data.getValue(BundleKeys.NOTIFICATION_TRIGGER, ""),
            NavigationScreen.MESSAGES.value,
            remoteMessage.data.getValue(BundleKeys.MESSAGE_SID, ""),
            remoteMessage.data.getValue(BundleKeys.NOTIFICATION_ID, ""),
            remoteMessage.priority
        )
    }
}