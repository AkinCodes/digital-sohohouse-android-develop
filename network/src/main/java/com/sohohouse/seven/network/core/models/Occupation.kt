package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "occupations")
data class Occupation(
    @field:Json(name = "name") var name: String? = null,
) : Resource(), Serializable