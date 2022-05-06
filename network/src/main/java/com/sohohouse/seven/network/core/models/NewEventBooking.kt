package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "bookings")
data class NewEventBooking(
    @field:Json(name = "guests") var guests: List<Guests>? = null,
    @field:Json(name = "payment_card") var paymentCard: HasOne<Card>? = null,
    @field:Json(name = "event") var event: HasOne<Event> = HasOne(),
) : Resource(), Serializable

data class Guests(
    @field:Json(name = "name") var name: String? = "",
    @field:Json(name = "email") var email: String? = "",
    @field:Json(name = "phone") var phone: String? = "",
) : Serializable
