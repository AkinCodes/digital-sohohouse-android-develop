package com.sohohouse.seven.fcm.mappers

import com.google.firebase.messaging.RemoteMessage
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.getValue
import com.sohohouse.seven.fcm.MessageParams
import javax.inject.Inject

class FcmNotificationMapper @Inject constructor() : NotificationPayloadMapper {
    override fun mapToMessageParams(remoteMessage: RemoteMessage): MessageParams {
        return MessageParams(
            id = remoteMessage.data.getValue(BundleKeys.ID, ""),
            title = remoteMessage.notification?.title ?: "",
            alert = remoteMessage.notification?.body ?: "",
            trigger = remoteMessage.data.getValue(BundleKeys.NOTIFICATION_TRIGGER, ""),
            screenName = remoteMessage.data.getValue(BundleKeys.NOTIFICATION_SCREEN_NAME, ""),
            sid = remoteMessage.data.getValue(BundleKeys.MESSAGE_SID, ""),
            notificationId = remoteMessage.data.getValue(BundleKeys.NOTIFICATION_ID, ""),
            priority = remoteMessage.priority
        )
    }
}