package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "hotels")
data class Hotel(
    @field:Json(name = "room_booking_url") var roomBookingUrl: String = "",
    @field:Json(name = "totals_inclusive_of_tax") var _totalsInclusiveOfTax: Boolean? = false,
    @field:Json(name = "friends_and_family") var friendsAndFamily: Boolean? = false,
    @field:Json(name = "venue") var venue: HasOne<Venue>? = HasOne(),
) : Resource(), Serializable {
    val totalsInclusiveOfTax: Boolean get() = _totalsInclusiveOfTax == true
}


