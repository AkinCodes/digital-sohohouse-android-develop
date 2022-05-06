package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "inquiries")
data class Inquiry(
    @field:Json(name = "case_number") var caseNumber: String = "",
) : Resource(), Serializable


