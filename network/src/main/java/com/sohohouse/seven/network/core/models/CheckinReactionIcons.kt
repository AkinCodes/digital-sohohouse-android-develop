package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "checkin_reaction_icons")
data class CheckinReactionIcons(
    @field:Json(name = "icon_url") var iconUrl: String = "",
    @field:Json(name = "default") var default: Boolean = false,
    @field:Json(name = "title") var title: String = "",
) : Resource(), Serializable