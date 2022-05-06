package com.sohohouse.seven.housenotes.detail.sitecore

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.sohohouse.seven.base.DiffItem

sealed class HouseNoteDetailItem(val itemType: HouseNoteDetailSitecoreItemType) : DiffItem

data class HouseNoteDetailHeaderImageItem constructor(val url: String) :
    HouseNoteDetailItem(HouseNoteDetailSitecoreItemType.HEADER_IMAGE)

data class HouseNoteDetailHeaderItem constructor(
    val location: String,
    val title: String,
    val subtitle: String,
    val author: String,
    val articleDate: String
) : HouseNoteDetailItem(HouseNoteDetailSitecoreItemType.HEADER)

data class HouseNoteDetailCreditItem(val credits: String) :
    HouseNoteDetailItem(HouseNoteDetailSitecoreItemType.CREDIT)

data class HouseNoteDetailGridImageBlockItem constructor(
    val url: String,
    val caption: String,
    val aspectRatio: Float
) : HouseNoteDetailItem(HouseNoteDetailSitecoreItemType.GRID_IMAGE_BLOCK)

data class HouseNoteDetailIBodyImageBlockItem constructor(
    val url: String,
    val aspectRatio: Float,
    val caption: String
) : HouseNoteDetailItem(HouseNoteDetailSitecoreItemType.BODY_IMAGE_BLOCK)

data class HouseNoteDetailImageCaptionBlockItem(val caption: String) :
    HouseNoteDetailItem(HouseNoteDetailSitecoreItemType.CAPTION_BLOCK)

data class HouseNoteDetailImageCarouselBlockItem(
    val credit: String,
    val collaboration: String,
    val images: List<Image>
) : HouseNoteDetailItem(HouseNoteDetailSitecoreItemType.IMAGE_CAROUSEL_BLOCK) {
    data class Image constructor(
        val imageCaption: String,
        val imageCredit: String,
        val imageLarge: String,
        val inverseAspectRatio: Float,
        val tag: String? = null
    ) : DiffItem {
        override val key: Any?
            get() = imageLarge
    }
}

data class HouseNoteDetailInformationItem(val text: String) :
    HouseNoteDetailItem(HouseNoteDetailSitecoreItemType.INFORMATION)

data class HouseNoteDetailQuoteBlockItem(val quote: String) :
    HouseNoteDetailItem(HouseNoteDetailSitecoreItemType.QUOTE_BLOCK)

data class HouseNoteDetailTextBlockItem constructor(val paragraph: String) :
    HouseNoteDetailItem(HouseNoteDetailSitecoreItemType.TEXT_BLOCK)

data class HouseNoteDetailTitleBlockItem(val title: String) :
    HouseNoteDetailItem(HouseNoteDetailSitecoreItemType.TITLE_BLOCK)

data class HouseNoteDetailHeaderTextBlockItem(
    val text: String,
    val headingStyle: HeadingStyle,
    val alignment: Int
) : HouseNoteDetailItem(HouseNoteDetailSitecoreItemType.HEADER_TEXT_BLOCK) {
    enum class HeadingStyle {
        h1,
        h2,
        h3
    }
}

data class HouseNoteDetailYoutubeVideoBlockItem constructor(
    val videoUrl: String,
    val youtubeVideoId: String,
    val startTime: Int,
    val showControls: Boolean,
    var thumbnail: String? = "",
    var videoTime: Int = startTime,
    val layout: String,    //TODO enum
    val tracker: YouTubePlayerTracker = YouTubePlayerTracker(),
    var isStarted: Boolean = false,
    val isSoundlessLoop: Boolean = false
) : HouseNoteDetailItem(
    itemType = when (layout) {
        "cropped" -> HouseNoteDetailSitecoreItemType.CROPPED_YT_VIDEO
        else -> HouseNoteDetailSitecoreItemType.FULL_BLEED_YT_VIDEO
    }
)

data class HouseNoteDetailVimeoVideoBlockItem constructor(
    val videoUrl: String,
    val startTime: Int,
    var thumbnail: String? = "",
    var videoTime: Int = startTime,
    val layout: String,    //TODO enum
    var isStarted: Boolean = false,
    val isSoundlessLoop: Boolean = false
) : HouseNoteDetailItem(
    itemType = when (layout) {
        "cropped" -> HouseNoteDetailSitecoreItemType.CROPPED_VIMEO_VIDEO
        else -> HouseNoteDetailSitecoreItemType.FULL_BLEED_VIMEO_VIDEO
    }
)

enum class HouseNoteDetailSitecoreItemType {
    HEADER,
    HEADER_IMAGE,
    TEXT_BLOCK,
    TITLE_BLOCK,
    BODY_IMAGE_BLOCK,
    CAPTION_BLOCK,
    QUOTE_BLOCK,
    FULL_BLEED_YT_VIDEO,
    CROPPED_YT_VIDEO,
    IMAGE_CAROUSEL_BLOCK,
    GRID_IMAGE_BLOCK,
    INFORMATION,
    CREDIT,
    HEADER_VIDEO,
    HEADER_TEXT_BLOCK,
    CROPPED_VIMEO_VIDEO,
    FULL_BLEED_VIMEO_VIDEO
}