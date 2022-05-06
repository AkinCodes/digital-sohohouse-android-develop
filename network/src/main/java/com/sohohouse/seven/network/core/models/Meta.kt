package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Meta(
    @field:Json(name = "total_items") var totalItems: Int = 0,
    @field:Json(name = "page") var page: Int,
    @field:Json(name = "per_page") var perPage: Int,
    @field:Json(name = "total_pages") var totalPages: Int,
    @field:Json(name = "estimated_total") var estimatedTotal: Int? = 0,
)
