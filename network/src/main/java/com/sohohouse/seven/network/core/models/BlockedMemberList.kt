package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable


@JsonApi(type = "blocked_members_lists")
data class BlockedMemberList(
    @field:Json(name = "profile_id") val profileId: String? = null,
    @field:Json(name = "unblocked_profile_id") val unblockedProfileId: String? = null,
    @field:Json(name = "blocked_profile_id") val blockedProfileId: String? = null,
    @field:Json(name = "blocked_members") val blockedMembers: List<String>? = emptyList(),
) : Resource(), Serializable
