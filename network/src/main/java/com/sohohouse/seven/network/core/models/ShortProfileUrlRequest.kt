package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "short_profile_urls")
class ShortProfileUrlRequest(
    @field:Json(name = "profile_id") var profileId: String = "",
) : Resource(), Serializable