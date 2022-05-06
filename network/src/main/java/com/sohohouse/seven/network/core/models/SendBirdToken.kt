package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@JsonApi(type = "tokens")
data class SendBirdToken(
    @field:Json(name = "value") var token: String = "",
    @field:Json(name = "expires_at") var expiresAt: String = "",
) : Resource(), Serializable {
    val expirationDate: Long
        get() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            .parse(expiresAt)?.time ?: 0L
}