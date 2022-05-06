//
//  Copyright © 2018 BNOTIONS. All rights reserved.
//

package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

const val HOUSE_NOTES_VIDEO = "house_notes_videos"

enum class VideoLayoutType(val value: String) {
    INLINE_CROPPED("inline_cropped"),
    FULL_BLEED("full_bleed"),
    FULL_BLEED_SOUNDLESS_LOOP("full_bleed_soundless_loop")
}

@JsonApi(type = HOUSE_NOTES_VIDEO)
data class HouseNotesVideo(
    @field:Json(name = "url") private var _url: String? = "",
    @field:Json(name = "start_at") private var _startAt: String? = "0",
    @field:Json(name = "show_controls") var showControls: Boolean = false,
    @field:Json(name = "video_layout") private var _videoLayout: String = "",
    @field:Json(name = "video_image_small") private var _imageSmall: String? = "",
    @field:Json(name = "video_image_large") private var _imageLarge: String? = "",
) : Resource(), Serializable {
    val url: String
        get() = _url ?: ""
    val startAt: Int
        get() = _startAt?.replace("s", "")
            .takeIf { !it.isNullOrEmpty() }?.toInt() ?: 0
    val videoLayout: VideoLayoutType
        get() = VideoLayoutType.values().filter { it.value == _videoLayout }
            .takeIf { it.isNotEmpty() }?.first() ?: VideoLayoutType.INLINE_CROPPED
    val imageSmall: String
        get() = _imageSmall ?: ""
    val imageLarge: String
        get() = _imageLarge ?: ""
}
