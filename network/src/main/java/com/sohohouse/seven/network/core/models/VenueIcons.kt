package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import java.io.Serializable

data class VenueIcons(
    @Json(name = "light_png") var lightPng: String = "",
    @Json(name = "light_svg") var lightSvg: String = "",
    @Json(name = "dark_png") var darkPng: String = "",
    @Json(name = "dark_svg") var darkSvg: String = "",
) : Serializable