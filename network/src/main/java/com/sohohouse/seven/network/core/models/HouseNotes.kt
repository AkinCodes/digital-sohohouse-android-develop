package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasMany
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable
import java.util.*

@JsonApi(type = "house_notes")
data class HouseNotes(
    @field:Json(name = "region") private var _region: String? = "",
    @field:Json(name = "is_featured") private var _isFeatured: Boolean? = false,
    @field:Json(name = "header_image_small_png") private var _headerImageSmallPng: String? = "",
    @field:Json(name = "header_image_medium_png") private var _headerImageMediumPng: String? = "",
    @field:Json(name = "header_image_large_png") private var _headerImageLargePng: String? = "",
    @field:Json(name = "header_image_xlarge_png") private var _headerImageXlargePng: String? = "",
    @field:Json(name = "header_image_alt_text") private var _headerImageAltText: String? = "",
    @field:Json(name = "header_video_image_small") private var _headerVideoImageSmall: String? = "",
    @field:Json(name = "header_video_image_medium") private var _headerVideoImageMedium: String? = "",
    @field:Json(name = "header_video_image_large") private var _headerVideoImageLarge: String? = "",
    @field:Json(name = "header_video_image_xlarge") private var _headerVideoImageXlarge: String? = "",
    @field:Json(name = "header_video_url") private var _headerVideoUrl: String? = "",
    @field:Json(name = "header_video_start_at") private var _headerVideoStartAt: String? = "",
    @field:Json(name = "header_video_show_controls") private var _headerVideoShowControls: Boolean? = false,
    @field:Json(name = "header_video_layout") private var _headerVideoLayout: String? = "",
    @field:Json(name = "title") private var _title: String? = "",
    @field:Json(name = "header_line") private var _headerLine: String? = "",
    @field:Json(name = "short_description") private var _shortDescription: String? = "",
    @field:Json(name = "publish_date") var publishDate: Date = Date(),
    @field:Json(name = "author") private var _author: String? = "",
    @field:Json(name = "footer_note") private var _footerNote: String? = "",
    @field:Json(name = "credits_block") private var _creditsBlock: String? = "",
    @field:Json(name = "readable_url") private var _readableUrl: String? = "",
    @field:Json(name = "body") var body: List<Body>? = null,
    @field:Json(name = "venues") var venues: HasMany<Resource> = HasMany(),
    @field:Json(name = "content_categories") var contentCategories: HasMany<ContentCategory> = HasMany(),
    @field:Json(name = "sections") var sections: HasMany<Resource> = HasMany(),
    @field:Json(name = "article_url_slug") var slug: String = "",
) : Resource(), Serializable {
    val region: String
        get() = _region ?: ""
    val isFeatured: Boolean
        get() = _isFeatured ?: false
    val headerImageSmallPng: String
        get() = _headerImageSmallPng ?: ""
    val headerImageMediumPng: String
        get() = _headerImageMediumPng ?: ""
    val headerImageLargePng: String
        get() = _headerImageLargePng ?: ""
    val headerImageXlargePng: String
        get() = _headerImageXlargePng ?: ""
    val headerImageAltText: String
        get() = _headerImageAltText ?: ""
    val headerVideoImageSmall: String
        get() = _headerVideoImageSmall ?: ""
    val headerVideoImageMedium: String
        get() = _headerVideoImageMedium ?: ""
    val headerVideoImageLarge: String
        get() = _headerVideoImageLarge ?: ""
    val headerVideoImageXlarge: String
        get() = _headerVideoImageXlarge ?: ""
    val headerVideoUrl: String
        get() = _headerVideoUrl ?: ""
    val headerVideoStartAt: Int
        get() = _headerVideoStartAt?.replace("s", "")
            .takeIf { !it.isNullOrEmpty() }?.toInt() ?: 0
    val headerVideoShowControls: Boolean
        get() = _headerVideoShowControls ?: false
    val headerVideoLayout: VideoLayoutType
        get() = VideoLayoutType.values().filter { it.value == _headerVideoLayout }
            .takeIf { it.isNotEmpty() }?.first() ?: VideoLayoutType.INLINE_CROPPED
    val title: String
        get() = _title ?: ""
    val headerLine: String
        get() = _headerLine ?: ""
    val shortDescription: String
        get() = _shortDescription ?: ""
    val author: String
        get() = _author ?: ""
    val footerNote: String
        get() = _footerNote ?: ""
    val creditsBlock: String
        get() = _creditsBlock ?: ""
    val readableUrl: String
        get() = _readableUrl ?: ""

    //TODO undo
    override fun getId(): String {
        return slug
    }
}

data class Body(
    @field:Json(name = "text_block") var textBlock: String = "",
    @field:Json(name = "body_image_title") var bodyImageTitle: String = "",
    @field:Json(name = "body_image_url_small_png") var bodyImageUrlSmallPng: String? = "",
    @field:Json(name = "body_image_url_medium_png") var bodyImageUrlMediumPng: String? = "",
    @field:Json(name = "body_image_url_large_png") var bodyImageUrlLargePng: String? = "",
    @field:Json(name = "body_image_alt_text") var bodyImageAltText: String = "",
    @field:Json(name = "body_image_caption") var bodyImageCaption: String = "",
    @field:Json(name = "body_pull_quote") var bodyPullQuote: String = "",
) : Serializable

