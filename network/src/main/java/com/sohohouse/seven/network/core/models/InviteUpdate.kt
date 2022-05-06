package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "invites")
data class InviteUpdate(
    @field:Json(name = "guest_name") var guestName: String = "",
) : Resource(), Serializable