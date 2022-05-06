package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "forms")
data class PaymentForm(
    @field:Json(name = "form_type") var formType: String = "",
    @field:Json(name = "public_key") var publicKey: String = "",
    @field:Json(name = "venue") var venue: HasOne<Venue> = HasOne(),
    @field:Json(name = "fields") var paymentFormFields: List<PaymentFormFields>? = null,
) : Resource(), Serializable

data class PaymentFormFields(
    @field:Json(name = "field_id") var fieldId: String = "",
    @field:Json(name = "field_label") var fieldLabel: String = "",
    @field:Json(name = "field_type") var fieldType: String = "",
) : Serializable
