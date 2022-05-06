package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable
import java.util.*

@JsonApi(type = "room_bookings")
data class RoomBooking(
    @field:Json(name = "first_name") var firstName: String? = "",
    @field:Json(name = "last_name") var lastName: String? = "",
    @field:Json(name = "email") var email: String? = "",
    @field:Json(name = "phone_number") var phoneNumber: String? = "",
    @field:Json(name = "room_add_ons") var roomAddOns: String? = "",
    @field:Json(name = "reference_num") var referenceNum: String? = "",
    @field:Json(name = "num_of_adults") var numAdults: Int? = 0,
    @field:Json(name = "num_of_children") var numChildren: Int? = 0,
    @field:Json(name = "start_date") var startDate: Date? = null,
    @field:Json(name = "end_date") var endDate: Date? = null,
    @field:Json(name = "rate_plan_type") var ratePlanType: String? = "",
    @field:Json(name = "tax_price_cents") var taxPriceCents: Int? = 0,
    @field:Json(name = "subtotal_price_cents") var subtotalPriceCents: Int? = 0,
    @field:Json(name = "total_price_cents") var totalPriceCents: Int? = 0,
    @field:Json(name = "nightly_average_price_cents") var nightlyAvgPriceCents: Int? = 0,
    @field:Json(name = "deposit_price_cents") var depositPriceCents: Int? = 0,
    @field:Json(name = "balance_price_cents") var balancePriceCents: Int? = 0,
    @field:Json(name = "currency_code") var currencyCode: String? = "",
    @field:Json(name = "cancellable_until") var cancellableUntil: Date? = null,
    @field:Json(name = "cancelled") var cancelled: Boolean = false,
    @field:Json(name = "room") var room: HasOne<Room>? = HasOne(),
    @field:Json(name = "payment_card") var paymentCard: HasOne<Card>? = HasOne(),
    @field:Json(name = "hotel") var hotel: HasOne<Hotel>? = HasOne(),
) : Resource(), Serializable, Booking {
    override val startsAt: Date?
        get() = startDate
    override val bookingId: String?
        get() = id

    val roomBookingUrl: String?
        get() = hotel?.get(document)?.roomBookingUrl
}