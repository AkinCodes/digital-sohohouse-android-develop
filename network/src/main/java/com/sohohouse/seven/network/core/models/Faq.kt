package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "faq")
data class Faq(
    @field:Json(name = "question") var question: String = "",
    @field:Json(name = "answer") var answer: String = "",
) : Resource(), Serializable


