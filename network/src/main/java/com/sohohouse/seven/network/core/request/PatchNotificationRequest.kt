package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.notification.Notification
import retrofit2.Call

class PatchNotificationRequest(private val notification: Notification) :
    CoreAPIRequest<Notification> {

    override fun createCall(api: CoreApi): Call<out Notification> {
        return api.patchNotification(notification.id, notification)
    }
}