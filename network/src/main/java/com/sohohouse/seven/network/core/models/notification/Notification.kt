package com.sohohouse.seven.network.core.models.notification

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import org.threeten.bp.ZonedDateTime

@JsonApi(type = "notifications")
data class Notification(
    @field:Json(name = "title") var title: String? = null,
    @field:Json(name = "body") var body: String? = null,
    @field:Json(name = "image_url") var imageUrl: String? = null,
    @field:Json(name = "trigger") var trigger: String? = null,
    @field:Json(name = "navigation") var navigation: Navigation? = null,
    @field:Json(name = "created_at") private var _createdAt: String? = null,
    @field:Json(name = "seen") var seen: Boolean = false,
    @field:Json(name = "persistent") var persistent: Boolean? = null,
    @field:Json(name = "dismissed") var dismissed: Boolean = false,
    @field:Json(name = "complete") var complete: Boolean = false,
    @field:Json(name = "new") var new: Boolean = false,
    @field:Json(name = "notification_group") private var _notificationGroup: HasOne<NotificationGroup>? = null,
) : Resource() {

    constructor(
        id: String,
        seen: Boolean = false,
        dismissed: Boolean = false,
        complete: Boolean = false,
    )
            : this(seen = seen, dismissed = dismissed, complete = complete) {
        this.type = "notifications"
        this.id = id
    }

    @delegate:Transient
    val createdAt: ZonedDateTime by lazy { ZonedDateTime.parse(_createdAt) }

    @delegate:Transient
    val notificationGroup: NotificationGroup? by lazy { _notificationGroup?.get(document) }

}