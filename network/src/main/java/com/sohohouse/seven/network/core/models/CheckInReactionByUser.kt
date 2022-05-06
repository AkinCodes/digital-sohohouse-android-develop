package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

@JsonApi(type = "checkin_reactions")
data class CheckInReactionByUser(
    @field:Json(name = "created_at") val createdAt: String = "",
    @field:Json(name = "profile") val _profile: HasOne<Profile> = HasOne(),
    @field:Json(name = "icon") var _reaction: HasOne<CheckinReactionIcons> = HasOne(),
) : Resource() {
    val profile: Profile? get() = _profile.get(document)
    val icon: Reaction get() = Reaction.valueOf(_reaction.get().id)
}