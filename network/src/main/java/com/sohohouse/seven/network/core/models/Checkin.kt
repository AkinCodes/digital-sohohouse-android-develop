package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import moe.banana.jsonapi2.*
import java.io.Serializable
import java.util.*

@JsonApi(type = "checkins")
data class Checkin(
    @field:Json(name = "status") var status: String = "",
    @field:Json(name = "created_at") var createdAt: Date? = null,
    @field:Json(name = "profile") var profileResource: HasOne<Profile> = HasOne(),
    @field:Json(name = "venues") var venueResource: HasMany<Venue> = HasMany(),
    @field:Json(name = "replies") var repliesResource: HasMany<Checkin> = HasMany(),
    @field:Json(name = "parent_id") var parentId: String? = null,
    @field:Json(name = "tags") var tags: PostTags? = null,
    @field:Json(name = "user_reaction") private var _userReaction: HasOne<CheckInReactionByUser> = HasOne(),
) : Resource(), Serializable {
    val profile: Profile? get() = profileResource.get(document)
    val replies get() = repliesResource.get(document)
    val venues: List<Venue> get() = venueResource.get(document) ?: emptyList()
    val reactions: CheckinMeta get() = getReactions(meta)
    val userReaction: CheckInReactionByUser? get() = _userReaction.get(document)

    private fun getReactions(meta: JsonBuffer<Any>): CheckinMeta {
        val adapter = Moshi.Builder().build().adapter(CheckinMeta::class.java)
        return (meta as JsonBuffer<CheckinMeta>).get(adapter)
    }
}

@JsonClass(generateAdapter = true)
data class CheckinMeta(
    @field:Json(name = "reactions") var reactions: Map<Reaction, Int> = mapOf(),
)