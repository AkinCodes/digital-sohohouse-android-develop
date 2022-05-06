package com.sohohouse.seven.fcm.mappers

import com.google.firebase.messaging.RemoteMessage
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.dagger.qualifier.FcmNotificationMapper
import com.sohohouse.seven.common.dagger.qualifier.SendBirdNotificationMapper
import com.sohohouse.seven.common.dagger.qualifier.SfmcNotificationMapper
import com.sohohouse.seven.common.extensions.getValue
import com.sohohouse.seven.fcm.MessageParams
import javax.inject.Inject

interface NotificationPayloadMapper {

    fun mapToMessageParams(remoteMessage: RemoteMessage): MessageParams

}

class AllNotificationPayloadMapper @Inject constructor(
    @FcmNotificationMapper private val fcmMapper: NotificationPayloadMapper,
    @SfmcNotificationMapper private val sfmcMapper: NotificationPayloadMapper,
    @SendBirdNotificationMapper private val sendBirdsMapper: NotificationPayloadMapper
) : NotificationPayloadMapper {

    companion object {
        const val SOURCE_SFCM = "SFMC"
    }

    override fun mapToMessageParams(remoteMessage: RemoteMessage): MessageParams {
        return when {
            remoteMessage.data.containsKey("sendbird") -> {
                sendBirdsMapper.mapToMessageParams(remoteMessage)
            }
            remoteMessage.data.getValue(BundleKeys.MESSAGE_SID, "") == SOURCE_SFCM -> {
                sfmcMapper.mapToMessageParams(remoteMessage)
            }
            else -> fcmMapper.mapToMessageParams(remoteMessage)
        }
    }
}