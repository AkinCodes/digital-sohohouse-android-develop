package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable


@JsonApi(type = "wallet")
data class Wallet(
    @field:Json(name = "device_id") val deviceId: String? = null,
    @field:Json(name = "icare_number") val icareNumber: String? = null,
    @field:Json(name = "updated_at") val updatedAt: String? = null,
    @field:Json(name = "authentication_email") val authenticationEmail: String? = null,
    @field:Json(name = "profile_id") val profileId: String? = null,
    @field:Json(name = "global_id") val globalId: String? = null,
    @field:Json(name = "created_at") val createdAt: String? = null,
) : Resource(), Serializable
