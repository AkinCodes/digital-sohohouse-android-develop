package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "device_notification_preferences")
data class DeviceNotificationPreferences(
    @field:Json(name = "category") var category: String = "",
    @field:Json(name = "title") var title: String = "",
    @field:Json(name = "description") var description: String = "",
    @field:Json(name = "enabled") var enabled: Boolean = false,
    @field:Json(name = "marketing_cloud") var marketingCloud: MarketingCloud = MarketingCloud(),
) : Resource(), Serializable

data class MarketingCloud(
    @field:Json(name = "tag") var tag: String = "",
    @field:Json(name = "tag_indicates_disabled") var tag_indicates_disabled: Boolean = false,
) : Serializable