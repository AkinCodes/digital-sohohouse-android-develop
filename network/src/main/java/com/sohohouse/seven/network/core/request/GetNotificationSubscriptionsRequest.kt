package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.NotificationSubscription
import retrofit2.Call

class GetNotificationSubscriptionsRequest(
    private val resourceType: String? = null,
    private val resourceId: String? = null,
    private val action: String? = null,
) : CoreAPIRequest<List<NotificationSubscription>> {
    override fun createCall(api: CoreApi): Call<out List<NotificationSubscription>> {
        return api.getNotificationSubscriptions(resourceType, resourceId, action)
    }
}