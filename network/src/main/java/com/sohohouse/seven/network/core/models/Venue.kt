//
//  This file is generated, DO NOT MODIFY
//  Venue.kt
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

const val JSON_API_TYPE_VENUES = "venues"

@JsonApi(type = JSON_API_TYPE_VENUES)
data class Venue(
    @field:Json(name = "name") private var _name: String? = "",
    @field:Json(name = "description") private var _description: String? = "",
    @field:Json(name = "venue_type") private var _venueType: String? = "",
    @field:Json(name = "address") var venueAddress: VenueAddress = VenueAddress(),
    @field:Json(name = "region") private var _region: String? = "",
    @field:Json(name = "country") private var _country: String? = "",
    @field:Json(name = "city") private var _city: String? = "",
    @field:Json(name = "phone_number") private var _phoneNumber: String? = "",
    @field:Json(name = "time_zone") private var _timeZone: String? = "",
    @field:Json(name = "location") var location: Location = Location(),
    @field:Json(name = "colors") var _venueColors: VenueColors = VenueColors(),
    @field:Json(name = "is_top_level") private var _isTopLevel: Boolean? = false,
    @field:Json(name = "is_active") private var _isActive: Boolean? = false,
    @field:Json(name = "operating_hours") var operatingHours: OperatingHours = OperatingHours(),
    @field:Json(name = "icons") var venueIcons: VenueIcons = VenueIcons(),
    @field:Json(name = "slug") private var _slug: String? = "",
    @field:Json(name = "house") var house: HasOne<House> = HasOne(),
    @field:Json(name = "restaurant") private var _restaurant: HasOne<RestaurantInfo> = HasOne(),
    @field:Json(name = "hotel") var hotel: HasOne<Hotel> = HasOne(),
    @field:Json(name = "parent") var _parent: HasOne<Venue>? = HasOne(),
    @field:Json(name = "active_parent_venue") var activeParentVenue: HasOne<Venue>? = HasOne(),
    @field:Json(name = "sca_required") var scaRequired: Boolean = false,
    @field:Json(name = "max_guests") var maxGuests: Int? = 1
) : Resource(), Serializable {

    @Transient
    var restaurants = ArrayList<Venue>()

    val name: String
        get() = _name ?: ""
    val description: String
        get() = _description ?: ""
    val venueType: String
        get() = _venueType ?: ""
    val region: String
        get() = _region ?: ""
    val country: String
        get() = _country ?: ""
    val phoneNumber: String
        get() = _phoneNumber ?: ""
    val timeZone: String
        get() = _timeZone ?: ""
    val venueColors: VenueColors
        get() = VenueColors(house = "#333333", dark = "#333333", light = _venueColors.light)
    val isTopLevel: Boolean
        get() = _isTopLevel ?: false
    val slug: String
        get() = _slug ?: ""
    val isActive: Boolean
        get() = _isActive ?: false
    val city: String
        get() = _city ?: ""
    val restaurant: RestaurantInfo?
        get() = _restaurant.get(document)
    val houseDetails: House
        get() = house.get(document)
    val parentId: String?
        get() = _parent?.get()?.id

}

data class VenueAddress(
    @field:Json(name = "lines") var lines: List<String>? = null,
    @Json(name = "postal_code") var postalCode: String? = "",
    @field:Json(name = "locality") var locality: String? = "",
    @field:Json(name = "country") var country: String? = "",
) : Serializable

data class Location(
    @field:Json(name = "longitude") var longitude: Float? = 0.toFloat(),
    @field:Json(name = "latitude") var latitude: Float? = 0.toFloat(),
) : Serializable

data class VenueColors(
    @field:Json(name = "house") var house: String = "",
    @field:Json(name = "dark") var dark: String = "",
    @field:Json(name = "light") var light: String = "",
) : Serializable

data class OperatingHours(
    @Json(name = "opens_at") var opensAt: String? = null,
    @Json(name = "closes_at") var closesAt: String? = null,
    @Json(name = "periods") var periods: List<Period>? = null,
) : Serializable

data class Period(
    @Json(name = "open") var venueOpen: VenueTime = VenueTime(),
    @Json(name = "close") var venueClose: VenueTime = VenueTime(),
) : Serializable

data class VenueTime(
    @Json(name = "day") var _day: Int = 0,
    @Json(name = "time") var time: String = "",
) : Serializable {
    val day: Day? get() = Day.forApiValue(_day)
}

enum class Day(val apiValue: Int) {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(0);

    companion object {
        fun forApiValue(apiValue: Int): Day? {
            return values().firstOrNull { it.apiValue == apiValue }
        }
    }
}
