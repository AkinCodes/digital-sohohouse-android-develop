package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "bookings")
data class PatchBookingState(
    @field:Json(name = "state") var state: String? = "",
    @field:Json(name = "events") var eventState: HasOne<PatchBookingEventState> = HasOne(),
) : Resource(), Serializable

@JsonApi(type = "events")
data class PatchBookingEventState(
    @field:Json(name = "id") var _id: String? = "",
) : Resource(), Serializable