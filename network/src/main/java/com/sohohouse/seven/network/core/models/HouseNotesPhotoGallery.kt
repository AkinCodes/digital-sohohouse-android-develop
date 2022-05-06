package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

const val HOUSE_NOTES_PHOTO_GALLERY = "house_notes_photo_galleries"

enum class GalleryLayoutType(val value: String) {
    LIST("list"),
    CAROUSEL("carousel")
}

@JsonApi(type = HOUSE_NOTES_PHOTO_GALLERY)
data class HouseNotesPhotoGallery(
    @field:Json(name = "credits") var credits: String? = "",
    @field:Json(name = "collaboration_credits") var collaborationCredits: String? = "",
    @field:Json(name = "layout") var layout: String? = "",
    @field:Json(name = "photos") var photos: List<Photo>? = listOf(),
) : Resource(), Serializable


data class Photo(
    @field:Json(name = "image_url_small") var imageUrlSmall: String? = "",
    @field:Json(name = "image_url_medium") var imageUrlMedium: String? = "",
    @field:Json(name = "image_url_large") var ImageUrlLarge: String? = "",
    @field:Json(name = "image_url_xlarge") var imageUrlXlarge: String? = "",
    @field:Json(name = "image_alt_text") var imageAltText: String? = "",
    @field:Json(name = "caption") private var _caption: String? = "",
    @field:Json(name = "credits") private var _credits: String? = "",
) : Serializable {
    val caption: String
        get() = _caption ?: ""
    val credits: String
        get() = _credits ?: ""
}