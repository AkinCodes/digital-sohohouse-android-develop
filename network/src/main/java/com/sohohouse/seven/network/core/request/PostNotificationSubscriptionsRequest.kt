package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.NotificationSubscription
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.ObjectDocument
import retrofit2.Call

class PostNotificationSubscriptionsRequest(private val action: String, private val event: Event) :
    CoreAPIRequest<NotificationSubscription> {

    override fun createCall(api: CoreApi): Call<out NotificationSubscription> {
        val document = ObjectDocument<NotificationSubscription>()
        document.set(NotificationSubscription(action, HasOne(event)))
        return api.postNotificationSubscriptions(document)
    }
}