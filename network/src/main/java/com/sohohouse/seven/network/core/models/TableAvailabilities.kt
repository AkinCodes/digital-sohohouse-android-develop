package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

const val TABLE_BOOKINGS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm"

@JsonApi(type = "availabilities")
data class TableAvailabilities(
    @Json(name = "restaurant_id") var restaurant_id: String? = null,
    @Json(name = "party_size") var party_size: Int? = null,
    @Json(name = "time_slots") var time_slots: List<TimeSlot>? = null,
) : Resource(), Serializable

data class TimeSlot(
    @Json(name = "date_time") var date_time: String? = null,
    @Json(name = "extra_attribute") var extra_attribute: String? = null,
) : Serializable

@JsonApi(type = "table_locks")
data class SlotLock(
    @Json(name = "date_time") var date_time: String? = null,
    @Json(name = "party_size") var party_size: Int? = null,
    @Json(name = "restaurant") var restaurant: HasOne<RestaurantInfo>? = null,
    @Json(name = "extra_attribute") var extra_attribute: String? = "default",
    @Json(name = "token") var token: String? = null,
    @Json(name = "expires_at") var expires_at: String? = null,
) : Resource(), Serializable

@JsonApi(type = "table_bookings")
data class TableReservation(
    @Json(name = "special_request") var special_request: String? = null,
    @Json(name = "terms_consent") var terms_consent: Boolean? = null,
    @Json(name = "guest_consent") var guest_consent: Boolean? = null,
    @Json(name = "phone") var phone: Phone? = null,
    @Json(name = "table_lock") var table_lock: HasOne<SlotLock>? = null,
    @field:Json(name = "restaurant") var _restaurant: HasOne<RestaurantInfo>? = null,
    @field:Json(name = "venue") var _venue: HasOne<Venue>? = null,
    @Json(name = "party_size") var party_size: Int = 0,
    @Json(name = "status") var status: String = "",
    @Json(name = "confirmation_number") var confirmation_number: Int = 0,
    @Json(name = "date_time") var date_time: String = "",
    @Json(name = "email") var email: String = "",
    @Json(name = "first_name") var first_name: String = "",
    @Json(name = "last_name") var last_name: String = "",
) : Resource(), Booking, Serializable {
    val restaurant: RestaurantInfo?
        get() = _restaurant?.get(document)
    val venue: Venue?
        get() = _venue?.get(document)
    val dateTime: Date
        get() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault()).parse(date_time)

    override val startsAt: Date
        get() = dateTime

    override val bookingId: String?
        get() = _restaurant?.get(document)?.id
}

data class Phone(var number: String? = null, var country_code: String? = null) : Serializable