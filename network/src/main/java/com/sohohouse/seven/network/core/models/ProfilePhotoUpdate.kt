package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "profiles")
data class ProfilePhotoUpdate(
    @field:Json(name = "profile_image") val profilePhotoData: String = "",
) : Resource(), Serializable