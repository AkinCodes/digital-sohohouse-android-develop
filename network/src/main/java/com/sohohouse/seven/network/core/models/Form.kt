package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

@JsonApi(type = "forms")
data class Form(
    @field:Json(name = "form_type") var formType: String = "",
    @field:Json(name = "public_key") var publicKey: String = "",
    @field:Json(name = "venue") var venue: HasOne<Venue> = HasOne(),
    @field:Json(name = "fields") var paymentFormFields: List<PaymentFormFields>? = null,
) : Resource()
