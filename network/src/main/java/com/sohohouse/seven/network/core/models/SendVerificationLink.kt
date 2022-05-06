package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable

@JsonApi(type = "account_verification_emails")
data class SendVerificationLink(
    @field:Json(name = "account") var account: HasOne<Account> = HasOne(),
) : Resource(), Serializable