package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "recommendations")
data class RecommendationDto(
    @Json(name = "city") var city: String? = null,
    @Json(name = "firstname") var firstname: String? = null,
    @Json(name = "industry") var industry: String? = null,
    @Json(name = "interests") var interests: List<String>? = null,
    @Json(name = "lastname") var lastname: String? = null,
    @field:Json(name = "local_house") var localHouse: String? = null,
    @Json(name = "profession") var profession: String? = null,
    @Json(name = "reasons") var reasons: List<String>? = null,
    @field:Json(name = "reco_rank") var recoRank: Double? = null,
    @Json(name = "score") var score: Double? = null,
    @Json(name = "timestamp") var timestamp: String? = null,
    @field:Json(name = "url_profile") var profileUrl: String? = null,
) : Resource(), Serializable