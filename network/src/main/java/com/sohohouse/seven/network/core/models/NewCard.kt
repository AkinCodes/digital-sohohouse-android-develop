package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "cards")
data class NewCard(
    @field:Json(name = "form") var form: HasOne<PaymentForm> = HasOne(),
    @field:Json(name = "card_payload") var cardPayload: String = "",
) : Resource(), Serializable


