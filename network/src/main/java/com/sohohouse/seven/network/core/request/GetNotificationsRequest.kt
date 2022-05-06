package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.notification.Notification
import retrofit2.Call

class GetNotificationsRequest : CoreAPIRequest<List<Notification>> {

    companion object {
        private const val NOTIFICATION_GROUP = "notification_group"
    }

    override fun createCall(api: CoreApi): Call<out List<Notification>> {
        return api.getNotifications(include = NOTIFICATION_GROUP)
    }

}