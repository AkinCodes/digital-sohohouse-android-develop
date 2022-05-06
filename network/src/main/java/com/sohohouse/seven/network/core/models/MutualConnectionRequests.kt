package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import java.util.*

@JsonApi(type = "mutual_connection_requests")
data class MutualConnectionRequests(
    @field:Json(name = "message") override val message: String? = null,
    @field:Json(name = "created_at") override val createdAt: Date? = null,
    @field:Json(name = "state") override var state: String? = null,
    @field:Json(name = "sender") override val sender: HasOne<Profile> = HasOne(),
    @field:Json(name = "receiver") override val receiver: HasOne<Profile> = HasOne(),
) : Connection() {

    companion object {
        const val STATE_ACCEPTED = "accepted"
        const val STATE_HIDDEN = "hidden"
    }

}