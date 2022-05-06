//
//  This file is generated, DO NOT MODIFY
//  EventBooking.kt
//  DigitalHouse
//
//  Copyright © 2018 BNOTIONS. All rights reserved.
//

package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable
import java.util.*

@JsonApi(type = "bookings")
data class EventBooking(
    @field:Json(name = "member_id") var memberId: String = "",
    @field:Json(name = "held_until") var heldUntil: Date? = null,
    @field:Json(name = "booking_type") var bookingType: String = "",
    @field:Json(name = "state") var state: String? = "",
    @field:Json(name = "failure") var failure: Failure? = null,
    @field:Json(name = "number_of_guests") var numberOfGuests: Int? = 0,
    @field:Json(name = "event") var _event: HasOne<Event>? = null,
    @field:Json(name = "venue") var _venue: HasOne<Venue>? = null,
    @field:Json(name = "transaction_auth_html") var transactionAuthHtml: String? = null,
) : Resource(), Serializable, Booking {

    val event: Event?
        get() = _event?.get(document)

    val timeZone: String?
        get() = _venue?.get(document)?.timeZone

    val venue: Venue
        get() = _venue?.get(document) ?: Venue()

    override val startsAt: Date?
        get() = event?.startsAt
    override val bookingId: String?
        get() = _event?.get(document)?.id
}

data class Failure(
    @field:Json(name = "code") var code: String = "",
) : Serializable


