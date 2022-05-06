package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "inquiries")
data class NewInquiry(
    @field:Json(name = "inquiry_type") var inquiryType: String = "",
    @field:Json(name = "reason") var reason: String = "",
    @field:Json(name = "venue_type") var venueType: String? = null,
    @field:Json(name = "venue_name") var venueName: String? = null,
    @field:Json(name = "body") var body: String? = "",
) : Resource(), Serializable


