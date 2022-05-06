package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "accounts")
data class AccountUpdate(
    @field:Json(name = "analytics_consent")
    var analyticsConsent: Boolean? = false,
    @field:Json(name = "terms_conditions_consent")
    var termsConditionsConsent: Boolean? = false,
    @field:Json(name = "house_pay_terms_consent")
    var housePayTermsConditionsConsent: Boolean? = false,
) : Resource(), Serializable


