package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "films")
data class EventsFilm(
    @field:Json(name = "description") var description: String? = "",
    @field:Json(name = "director") var director: String? = "",
    @field:Json(name = "cast") var cast: String? = "",
    @field:Json(name = "distributor") var distributor: String? = "",
    @field:Json(name = "year") var year: Int? = 0,
    @field:Json(name = "running_time") var runningTime: Int? = 0,
    @field:Json(name = "country") var country: String? = "",
    @field:Json(name = "certificate") var certificate: String? = "",
    @field:Json(name = "subtitles") var subtitles: String? = "",
    @field:Json(name = "language") var language: String? = "",
) : Resource(), Serializable


