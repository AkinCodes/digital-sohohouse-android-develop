package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import moe.banana.jsonapi2.HasMany
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable
import java.util.*

const val JSON_API_TYPE_GUEST_LISTS = "guest_lists"

@JsonApi(type = JSON_API_TYPE_GUEST_LISTS)
data class GuestList(
    var name: String = "",
    var date: Date? = null,
    var notes: String = "",
    @field:Json(name = "venue") private var _venue: HasOne<Venue> = HasOne(),
    @field:Json(name = "invites") private var _invites: HasMany<Invite> = HasMany(),
    @field:Json(name = "max_guests") var maxGuests: Int = 1,
) : Resource(), Serializable {

    val invites: List<Invite>
        get() = _invites.get(this.document)

    val venue: Venue
        get() = try {
            _venue.get(document)
        } catch (e: Exception) {
            Venue()
        }

}

@JsonApi(type = JSON_API_TYPE_GUEST_LISTS)
data class PostGuestList(
    private var name: String = "",
    private var date: String = "",
    private var notes: String = "",
    private var venue: HasOne<Venue> = HasOne(),
) : Resource(), Serializable

@JsonApi(type = "invites")
data class Invite(
    @field:Json(name = "guest_name") var guestName: String = "",
    @field:Json(name = "status") var status: String = "",
    @field:Json(name = "guest_list") var guestList: HasOne<GuestList> = HasOne(),
) : Resource(), Serializable

@JsonClass(generateAdapter = true)
data class GuestsMeta(@field:Json(name = "max_guests") var maxGuests: Int = 1) : Serializable