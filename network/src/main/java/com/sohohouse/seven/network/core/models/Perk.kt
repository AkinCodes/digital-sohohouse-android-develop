package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable
import java.util.*


@JsonApi(type = "perks")
data class Perk(
    @field:Json(name = "region") var region: String? = "",
    @field:Json(name = "header_image_small") var headerImageSmall: String? = "",
    @field:Json(name = "header_image_medium") var headerImageMedium: String? = "",
    @field:Json(name = "header_image_large") var headerImageLarge: String? = "",
    @field:Json(name = "header_image_xlarge") var headerImageXlarge: String? = "",
    @field:Json(name = "header_image_alt_text") var headerImageAltText: String? = "",
    @field:Json(name = "body_image_small") var bodyImageSmall: String? = "",
    @field:Json(name = "body_image_medium") var bodyImageMedium: String? = "",
    @field:Json(name = "body_image_large") var bodyImageLarge: String? = "",
    @field:Json(name = "body_image_xlarge") var bodyImageXlarge: String? = "",
    @field:Json(name = "body_image_alt_text") var bodyImageAltText: String? = "",
    @field:Json(name = "body_image_caption") var bodyImageCaption: String? = "",
    @field:Json(name = "title") var title: String? = "",
    @field:Json(name = "short_description") var shortDescription: String? = "",
    @field:Json(name = "body_title") var bodyTitle: String? = "",
    @field:Json(name = "body") var body: String? = "",
    @field:Json(name = "online_only") var onlineOnly: Boolean? = null,
    @field:Json(name = "expires_on") var expiresOn: Date? = null,
    @field:Json(name = "terms_and_conditions") var termsAndConditions: String? = "",
    @field:Json(name = "perk_url") var perkUrl: String? = "",
    @field:Json(name = "promotion_code") var promotionCode: String? = "",
    @field:Json(name = "venue") var venues: HasOne<Venue>? = HasOne(),
    @field:Json(name = "summary") var summary: String? = "",
    @field:Json(name = "city") var city: String? = "",
    @field:Json(name = "content_pillar") var contentPillar: String? = "",
    @field:Json(name = "benefit_type") var benefitType: String? = null,
) : Resource(), Serializable {

    companion object {
        const val PERK_RESTAURANT = "membershipCard"
        const val PERK_BEDROOM = "membershipRooms"
    }
}