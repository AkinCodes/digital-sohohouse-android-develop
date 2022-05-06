package com.sohohouse.seven.network.core.models.notification

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasMany
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

@JsonApi(type = "notification_groups")
data class NotificationGroup(
    @field:Json(name = "notifications") var notifications: HasMany<Notification> = HasMany(),
    @field:Json(name = "seen") var seen: Boolean = false,
    @field:Json(name = "dismissed") var dismissed: Boolean = false,
    @field:Json(name = "new") var new: Boolean = false,
) : Resource() {

    constructor(
        id: String,
        seen: Boolean = false,
        dismissed: Boolean = false,
        new: Boolean = false,
    )
            : this(seen = seen, dismissed = dismissed, new = new) {
        this.id = id
        this.type = "notification_groups"
    }

}