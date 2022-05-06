package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ImageSet(
    @Json(name = "medium_png") var mediumPng: String? = null,
    @Json(name = "large_png") var largePng: String? = null,
    @Json(name = "xlarge_png") var xlargePng: String? = null,
) : Serializable