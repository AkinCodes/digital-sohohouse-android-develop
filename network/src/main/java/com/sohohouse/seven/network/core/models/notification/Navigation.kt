package com.sohohouse.seven.network.core.models.notification

import com.squareup.moshi.Json

class Navigation {
    @Json(name = "screen_name") var screenName: String? = null
    @Json(name = "resource_id") var resourceId: String? = null
}