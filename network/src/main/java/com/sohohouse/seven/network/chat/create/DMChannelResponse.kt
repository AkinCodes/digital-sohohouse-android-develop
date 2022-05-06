package com.sohohouse.seven.network.chat.create

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable


@JsonApi(type = "message_channels")
data class DMChannelResponse(
    @field:Json(name = "id") val channelId: String? = null,
    @field:Json(name = "url") val channelUrl: String? = null,
) : Resource(), Serializable
