package com.sohohouse.seven.network.chat.invite

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "message_invites")
data class SentMessageRequest(
    @field:Json(name = "channel_url")
    var channelUrl: String = "",
    @field:Json(name = "receiver_ids")
    val receiverIds: List<String> = emptyList(),
) : Resource(), Serializable