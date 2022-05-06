package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "interests")
data class Interest(
    @field:Json(name = "name") var name: String? = "",
    @field:Json(name = "category_name") var category: String? = "",
) : Resource(), Serializable