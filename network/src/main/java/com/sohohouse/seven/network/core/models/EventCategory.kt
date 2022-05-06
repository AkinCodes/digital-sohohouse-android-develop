package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "event_categories")
data class EventCategory(
    @field:Json(name = "name") var name: String = "",
    @field:Json(name = "event_types") var eventTypes: List<String>? = null,
    @field:Json(name = "icon") var icon: Icon? = null,
) : Resource(), Serializable

data class Icon(
    @field:Json(name = "png") var png: String = "",
    @field:Json(name = "svg") var svg: String = "",
) : Serializable


