package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CursorMeta(
    @field:Json(name = "page") var page: Page,
    @field:Json(name = "count_per_venue") var countPerVenue: Map<String, Int> = mapOf(),
)

@JsonClass(generateAdapter = true)
data class Page(
    @field:Json(name = "cursor") var cursor: Cursor,
)

@JsonClass(generateAdapter = true)
data class Cursor(
    @field:Json(name = "first") var first: String? = null,
    @field:Json(name = "last") var last: String? = null,
    @field:Json(name = "next") var next: String? = null,
    @field:Json(name = "prev") var prev: String? = null,
)
