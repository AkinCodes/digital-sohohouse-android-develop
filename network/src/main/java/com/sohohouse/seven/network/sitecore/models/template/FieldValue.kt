package com.sohohouse.seven.network.sitecore.models.template

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FieldValue(
    @Json(name = "\$count") val count: Int = 0,
    @Json(name = "subheader") val subheader: String = "",
    @Json(name = "author") val author: String = "",
    @Json(name = "publishDate") val publishDate: String = "",
    @Json(name = "categories") val categories: String = "",
    @Json(name = "hidden") val hidden: String = "",
    @Json(name = "region") val region: String = "",
    @Json(name = "venues") val venues: String = "",
    @Json(name = "credits") val credits: String = "",
    @Json(name = "mainImage") val mainImage: String = "",
    @Json(name = "mainImage__Url") val mainImageUrl: String = "",
    @Json(name = "mainImage__Alt") val mainImageAlt: String = "",
    @Json(name = "thumbnailImage") val thumbnailImage: String = "",
    @Json(name = "thumbnailImage__Url") val thumbnailImageUrl: String = "",
    @Json(name = "thumbnailImage__Alt") val thumbnailImageAlt: String = "",
    @Json(name = "shortDescription") val shortDescription: String = "",
    @Json(name = "startTime") val startTime: String = "",
    @Json(name = "customImage") val customImage: String = "",
    @Json(name = "customImage__Url") val customImageUrl: String = "",
    @Json(name = "customImage__Alt") val customImageAlt: String = "",
    @Json(name = "mainVideo") val mainVideo: String = "",
    @Json(name = "hiddenVideoTitle") val hiddenVideoTitle: String = "",
    @Json(name = "membershipTiers") val membershipTiers: String = "",
    @Json(name = "title") internal val _title: String = "",
    @Json(name = "appNoteTitle") internal val _appNoteTitle: String = "",
) {
    val title: String
        get() = if (_appNoteTitle.isNotEmpty()) _appNoteTitle else _title
}