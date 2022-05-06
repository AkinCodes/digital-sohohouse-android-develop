package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable
import java.util.*

@JsonApi(type = "memberships")
data class UpdateMembership(
    @field:Json(name = "inducted_at") var inductedAt: Date = Date(),
) : Resource(), Serializable


