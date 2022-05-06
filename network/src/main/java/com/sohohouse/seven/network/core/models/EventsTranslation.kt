package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "event_translations")
data class EventsTranslation(
    @field:Json(name = "description") var description: String = "",
    @field:Json(name = "name") var name: String = "",
    @field:Json(name = "locale") var locale: String = "",
) : Resource(), Serializable


