package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasMany
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

import java.io.Serializable
import java.util.*

@JsonApi(type = "memberships")
data class Membership(
    @field:Json(name = "membership_type") private var _membershipType: String? = "",
    @field:Json(name = "subscription_type") private var _subscriptionType: String? = "",
    @field:Json(name = "name") private var _name: String? = "",
    @field:Json(name = "code") private var _code: String? = "",
    @field:Json(name = "status") private var _status: String? = "",
    @field:Json(name = "start_date") var startDate: Date = Date(),
    @field:Json(name = "end_date") var endDate: Date = Date(),
    @field:Json(name = "renewal_date") var renewalDate: Date = Date(),
    @field:Json(name = "inducted_at") var inductedAt: Date? = null,
    @field:Json(name = "staff_type") private var _staffType: String? = "",
    @field:Json(name = "is_active") var isActive: Boolean = false,
    @field:Json(name = "is_active_plus") var isActivePlus: Boolean = false,
    @field:Json(name = "is_founder") var isFounder: Boolean = false,
    @field:Json(name = "account") var account: HasOne<Account> = HasOne(),
    @field:Json(name = "accessible_venues") var accessibleVenuesResource: HasMany<Venue> = HasMany(),
) : Resource(), Serializable {
    val membershipType: String
        get() = _membershipType ?: ""
    val subscriptionType: String
        get() = _subscriptionType ?: ""
    val name: String
        get() = _name ?: ""
    val code: String
        get() = _code ?: ""
    val status: String
        get() = _status ?: ""
    val staffType: String
        get() = _staffType ?: ""
}