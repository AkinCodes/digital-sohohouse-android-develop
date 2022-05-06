package com.sohohouse.seven.home.houseboard.repo

import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.CoreRequestFactory
import com.sohohouse.seven.network.core.models.notification.Notification
import com.sohohouse.seven.network.core.models.notification.NotificationGroup
import com.sohohouse.seven.network.core.request.GetNotificationsRequest
import com.sohohouse.seven.network.core.request.PatchNotificationGroupRequest
import com.sohohouse.seven.network.core.request.PatchNotificationRequest
import javax.inject.Inject

interface NotificationsRepo {

    fun getNotifications(): Either<ServerError, List<Notification>>

    fun patch(notification: Notification): Either<ServerError, Notification>

    fun patch(notificationGroup: NotificationGroup): Either<ServerError, NotificationGroup>
}

class NotificationsRepoImpl @Inject constructor(private val coreRequestFactory: CoreRequestFactory) :
    NotificationsRepo {

    override fun getNotifications(): Either<ServerError, List<Notification>> {
        return coreRequestFactory.createV2(GetNotificationsRequest())
    }

    override fun patch(notification: Notification): Either<ServerError, Notification> {
        return coreRequestFactory.createV2(PatchNotificationRequest(notification))
    }

    override fun patch(notificationGroup: NotificationGroup): Either<ServerError, NotificationGroup> {
        return coreRequestFactory.createV2(PatchNotificationGroupRequest(notificationGroup))
    }

}