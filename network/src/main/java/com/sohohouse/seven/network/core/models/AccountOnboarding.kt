package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "account_onboardings")
data class AccountOnboarding(
    @field:Json(name = "house_member_onboarded_at") var houseMemberOnboardingAt: String? = null,
    @field:Json(name = "friends_member_onboarded_at") var friendsMemberOnboardedAt: String? = null,
) : Resource(), Serializable