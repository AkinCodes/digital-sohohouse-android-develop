package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "communication_preferences")
data class CommunicationPreference(
    @field:Json(name = "house_seven_opt_in") var houseSevenOptIn: Boolean = false,
    @field:Json(name = "members_affiliates_opt_in") var membersAffiliatesOptIn: Boolean = false,
    @field:Json(name = "members_update_opt_in") var membersUpdateOptIn: Boolean = false,
    @field:Json(name = "all_opt_out") var allOptOut: Boolean = false,
) : Resource(), Serializable
