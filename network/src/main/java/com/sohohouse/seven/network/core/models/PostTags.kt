package com.sohohouse.seven.network.core.models

import com.google.gson.annotations.JsonAdapter
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable
import java.util.ArrayList

@JsonClass(generateAdapter = true)
class PostTags(
    @Json(name = "city") var city: List<String>? = null,
    @Json(name = "theme") var theme: List<String>? = null,
) : Serializable {

    constructor() : this(ArrayList(), ArrayList())

    constructor(city: String?, theme: String?): this() {
        if (city != null && city.isNotEmpty()) {
            this.city = listOf(city)
        }
        if (theme != null && theme.isNotEmpty()) {
            this.theme = listOf(theme)
        }
    }
}