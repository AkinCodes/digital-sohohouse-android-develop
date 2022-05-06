package com.sohohouse.seven.network.sitecore.models

import com.squareup.moshi.Json

data class SitecorePlaceholders(@Json(name = "digital-house-main") val digitalHouseMain: List<SitecoreComponent> = emptyList())