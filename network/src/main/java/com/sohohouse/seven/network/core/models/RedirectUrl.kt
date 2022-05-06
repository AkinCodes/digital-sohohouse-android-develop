package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "redirect_urls")
data class RedirectUrl(
    @field:Json(name = "redirect_uri") var redirectUri: String = "",
    @field:Json(name = "url") var url: String = "",
) : Resource(), Serializable


