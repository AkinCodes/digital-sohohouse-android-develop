package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "availability_statuses")
data class ProfileAvailabilityStatus(
    @field:Json(name = "status") var status: String = "",
    @field:Json(name = "profile") var profile: HasOne<Profile>? = HasOne(),
    @field:Json(name = "venue") var venueResource: HasOne<Venue>? = HasOne(),
) : Resource(), Serializable