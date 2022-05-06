package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "notification_subscriptions")
data class NotificationSubscription(
    @field:Json(name = "action") var action: String = "",
    @field:Json(name = "resource") var event: HasOne<Event> = HasOne(),
) : Resource(), Serializable {
    companion object {
        const val OPEN_FOR_BOOKING_ACTION = "open_for_booking"
    }
}


