package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "resource_meta")
data class EventInstructor(
    @field:Json(name = "instructor") var instructor: String? = "",
    @field:Json(name = "seats") var seats: List<Seat>? = null,
) : Resource(), Serializable

data class Seat(
    @field:Json(name = "id") var id: String? = "",
    @field:Json(name = "available") var available: Boolean? = false,
) : Serializable


