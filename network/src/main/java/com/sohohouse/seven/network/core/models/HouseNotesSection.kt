//
//  Copyright © 2018 BNOTIONS. All rights reserved.
//

package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

const val HOUSE_NOTES_SECTION = "house_notes_sections"

@JsonApi(type = HOUSE_NOTES_SECTION)
data class HouseNotesSection(
    @field:Json(name = "text_block") private var _textBlock: String? = "",
    @field:Json(name = "image_title") private var _imageTitle: String? = "",
    @field:Json(name = "image_url_small") private var _imageUrlSmallPng: String? = "",
    @field:Json(name = "image_url_medium") private var _imageUrlMediumPng: String? = "",
    @field:Json(name = "image_url_large") private var _imageUrlLargePng: String? = "",
    @field:Json(name = "image_alt_text") private var _imageAltText: String? = "",
    @field:Json(name = "image_caption") private var _imageCaption: String? = "",
    @field:Json(name = "pull_quote") private var _pullQuote: String? = "",
) : Resource(), Serializable {
    val textBlock: String
        get() = _textBlock ?: ""
    val imageTitle: String
        get() = _imageTitle ?: ""
    val imageUrlSmallPng: String
        get() = _imageUrlSmallPng ?: ""
    val imageUrlMediumPng: String
        get() = _imageUrlMediumPng ?: ""
    val imageUrlLargePng: String
        get() = _imageUrlLargePng ?: ""
    val imageAltText: String
        get() = _imageAltText ?: ""
    val imageCaption: String
        get() = _imageCaption ?: ""
    val pullQuote: String
        get() = _pullQuote ?: ""
}
