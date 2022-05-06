package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "cards")
data class Card(
    @field:Json(name = "purpose") var purpose: String = "",
    @field:Json(name = "is_primary") var isPrimary: Boolean = false,
    @field:Json(name = "status") var status: String = "",
    @field:Json(name = "last_four") var lastFour: String = "",
    @field:Json(name = "expiry") var expiry: String = "",
    @field:Json(name = "card_type") var cardType: String = "",
    @field:Json(name = "venue") var venue: HasOne<Venue> = HasOne(),
) : Resource(), Serializable


