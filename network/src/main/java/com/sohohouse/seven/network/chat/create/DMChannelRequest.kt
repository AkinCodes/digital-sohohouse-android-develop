package com.sohohouse.seven.network.chat.create

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable


@JsonApi(type = "message_channels")
data class DMChannelRequest(
    @field:Json(name = "profile_ids") val profileIds: List<String>? = emptyList(),
) : Resource(), Serializable
