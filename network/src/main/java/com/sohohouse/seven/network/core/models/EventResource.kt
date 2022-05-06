package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "resources")
data class EventResource(
    @field:Json(name = "instructor") var instructor: String? = "",
    @field:Json(name = "resource_type") var resourceType: String = "",
    @field:Json(name = "resource_meta") var resourceMeta: HasOne<EventInstructor> = HasOne(),
) : Resource(), Serializable


