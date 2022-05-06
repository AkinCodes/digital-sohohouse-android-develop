package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import java.util.*

@JsonApi(type = "connections")
data class Connections(
    @field:Json(name = "message") override val message: String? = null,
    @field:Json(name = "created_at") override val createdAt: Date? = null,
    @field:Json(name = "state") override val state: String? = null,
    @field:Json(name = "sender") override val sender: HasOne<Profile> = HasOne(),
    @field:Json(name = "receiver") override val receiver: HasOne<Profile> = HasOne(),
) : Connection()