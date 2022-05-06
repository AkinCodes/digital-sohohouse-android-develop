package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "static_pages")
data class StaticPages(
    @field:Json(name = "body") var body: String = "",
) : Resource(), Serializable


