package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "chat_tokens")
data class SendBirdTokenRequest(
    @field:Json(name = "token_type") var tokenType: String = "message",
) : Resource(), Serializable