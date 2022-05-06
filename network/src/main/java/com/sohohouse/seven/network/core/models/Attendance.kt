package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable
import java.util.*

@JsonApi(type = "attendances")
data class Attendance(
    @field:Json(name = "first_visit") var firstVisit: Boolean? = null,
    @field:Json(name = "created_at") var createdAt: Date? = null,
    @field:Json(name = "venue") var venueResource: HasOne<Venue>? = null,
) : Resource(), Serializable