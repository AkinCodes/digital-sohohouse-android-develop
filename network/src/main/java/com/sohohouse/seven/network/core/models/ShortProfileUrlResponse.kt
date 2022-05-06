package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "short_profile_urls")
class ShortProfileUrlResponse(
    @field:Json(name = "short_url") var shortUrl: String = "",
) : Resource(), Serializable