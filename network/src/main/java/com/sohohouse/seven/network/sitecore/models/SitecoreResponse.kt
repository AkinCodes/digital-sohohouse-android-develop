package com.sohohouse.seven.network.sitecore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SitecoreResponse(
    @Json(name = "sitecore") val sitecore: SiteCore,
)