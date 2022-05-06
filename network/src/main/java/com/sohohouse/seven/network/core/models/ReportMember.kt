package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

@JsonApi(type = "inquiries")
data class ReportMember(
    @field:Json(name = "inquiry_type") val inquiryType: String = "membership_enquiry",
    @field:Json(name = "reason") val reason: String = "house_connect_report",
    @field:Json(name = "body") val body: String = "",
) : Resource()
