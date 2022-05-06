package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "restaurants")
class RestaurantInfo : Resource(), Serializable {
    @field:Json(name = "booking_partner_id")
    val bookingPartnerId: String? = null

    @field:Json(name = "restaurant_url")
    val restaurantUrl: String? = null

    @field:Json(name = "restaurant_description")
    val restaurantDescription: String? = null

    @field:Json(name = "booking_partner_name")
    val bookingPartnerName: String? = null

    @field:Json(name = "max_number_of_seats_per_booking")
    val maxNumberOfSeatsPerBooking: Int? = null

    @field:Json(name = "special_notes")
    val specialNotes: String? = null

    @field:Json(name = "restaurant_images")
    val restaurantImages: Array<ImageSet>? = null

    @field:Json(name = "house_image_set")
    val houseImageSet: ImageSet? = null

    @field:Json(name = "header_image_set")
    val headerImageSet: ImageSet? = null

    @field:Json(name = "menus")
    val menus: Array<Menu>? = null

    fun getImage(): String = restaurantImages?.firstOrNull()?.largePng ?: ""
}

class Menu : Serializable {
    @Json(name = "menu_name")
    var menuName: String? = null

    @Json(name = "menu_url")
    var menuUrl: String? = null

    @Json(name = "menu_image")
    var menuImage: ImageSet? = null
}

class EADImageSet : Serializable {
    @Json(name = "1_1_fill_crop")
    var fillCrop: String? = null

    @Json(name = "alt_text")
    var altText: String? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EADImageSet

        if (fillCrop != other.fillCrop) return false
        if (altText != other.altText) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fillCrop?.hashCode() ?: 0
        result = 31 * result + (altText?.hashCode() ?: 0)
        return result
    }


}