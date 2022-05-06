package com.sohohouse.seven.network.sitecore.models

import com.sohohouse.seven.network.sitecore.models.template.Template
import com.squareup.moshi.Json

data class HouseNotesResponse(
    @Json(name = "value") val value: List<Template>,
)