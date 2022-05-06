package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.notification.NotificationGroup
import retrofit2.Call

class PatchNotificationGroupRequest(private val notificationGroup: NotificationGroup) :
    CoreAPIRequest<NotificationGroup> {

    override fun createCall(api: CoreApi): Call<out NotificationGroup> {
        return api.patchNotificationGroup(notificationGroup.id, notificationGroup)
    }
}