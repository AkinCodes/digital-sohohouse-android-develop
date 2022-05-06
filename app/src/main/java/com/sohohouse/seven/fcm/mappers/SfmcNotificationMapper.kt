package com.sohohouse.seven.fcm.mappers

import com.google.firebase.messaging.RemoteMessage
import com.sohohouse.seven.fcm.MessageParams
import javax.inject.Inject

class SfmcNotificationMapper @Inject constructor() : NotificationPayloadMapper {
    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_ALERT = "alert"
        private const val KEY_TRIGGER = "trigger"
        private const val KEY_SCREEN_NAME = "screenName"
        private const val KEY_SID = "_sid"
        private const val KEY_ID = "id"
        private const val KEY_NOTIFICATION_ID = "notificationId"
    }

    override fun mapToMessageParams(remoteMessage: RemoteMessage): MessageParams {
        return MessageParams(
            id = remoteMessage.data[KEY_ID] ?: "",
            title = remoteMessage.data[KEY_TITLE] ?: "",
            alert = remoteMessage.data[KEY_ALERT] ?: "",
            trigger = remoteMessage.data[KEY_TRIGGER] ?: "",
            screenName = remoteMessage.data[KEY_SCREEN_NAME] ?: "",
            sid = remoteMessage.data[KEY_SID] ?: "",
            notificationId = remoteMessage.data[KEY_NOTIFICATION_ID] ?: "",
            priority = remoteMessage.priority
        )
    }
}

