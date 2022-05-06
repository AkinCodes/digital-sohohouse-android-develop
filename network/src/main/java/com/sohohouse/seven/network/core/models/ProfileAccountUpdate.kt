package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "accounts")
data class ProfileAccountUpdate(
    @field:Json(name = "phone_number") var phoneNumber: String? = "",
) : Resource(), Serializable