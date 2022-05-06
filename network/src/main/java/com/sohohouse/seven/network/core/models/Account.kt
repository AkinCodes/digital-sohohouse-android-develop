package com.sohohouse.seven.network.core.models

import com.sohohouse.seven.network.common.safeGet
import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasMany
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

import java.io.Serializable
import java.util.*

@JsonApi(type = "accounts")
data class Account(
    @field:Json(name = "email") private var _email: String? = "",
    @field:Json(name = "address") var address: Address? = Address(),
    @field:Json(name = "calendar_subscription_url") private var _calendarSubscriptionUrl: String? = "",
    @field:Json(name = "membership_payment_url") private var _membershipPaymentUrl: String? = "",
    @field:Json(name = "membership_pass_url") private var _membershipPassUrl: String? = "",
    @field:Json(name = "analytics_consent") var analyticsConsent: Boolean? = false,
    @field:Json(name = "terms_conditions_consent") var termsConditionsConsent: Boolean? = false,
    @field:Json(name = "house_pay_terms_consent") var housePayTermsConsent: Boolean? = false,
    @field:Json(name = "phone_number") private var _phoneNumber: String? = "",
    @field:Json(name = "can_access_app") private var _canAccessApp: Boolean? = false,
    @field:Json(name = "loyalty_id") var loyaltyId: String? = null,
    @field:Json(name = "short_code") var shortCode: String? = null,
    @field:Json(name = "date_of_birth") var dateOfBirth: Date? = null,
    @field:Json(name = "joined_on") var joinedOn: Date? = null,
    @field:Json(name = "venue_id") private var _venueId: String? = "",
    @field:Json(name = "profile") private var profileIdentifier: HasOne<Profile>? = HasOne(),
    @field:Json(name = "communication_preferences") var _communicationPreferences: HasOne<CommunicationPreference>? = HasOne(),
    @field:Json(name = "membership") var membershipResource: HasOne<Membership>? = HasOne(),
    @field:Json(name = "local_house") var localHouseResource: HasOne<Venue>? = HasOne(),
    @field:Json(name = "favorite_venues") var favoriteVenuesResource: HasMany<Venue>? = HasMany(),
    @field:Json(name = "favorite_content_categories") var favoriteCategoriesResource: HasMany<ContentCategory>? = HasMany(),
    @field:Json(name = "latest_attendance") private var _attendance: HasOne<Attendance>? = HasOne(),
    @field:Json(name = "dismissed_update_profile") var dismissedUpdateProfile: Boolean = false,
    @field:Json(name = "dismissed_update_profile_image") var dismissedUpdateProfileImage: Boolean = false,
    @field:Json(name = "email_verified") var emailVerified: Boolean = false,
    @field:Json(name = "features") var features: HasMany<Feature> = HasMany(),
) : Resource(), Serializable {

    val email: String
        get() = _email ?: ""
    val calendarSubscriptionUrl: String
        get() = _calendarSubscriptionUrl ?: ""
    val membershipPaymentUrl: String
        get() = _membershipPaymentUrl ?: ""
    val membershipPassUrl: String
        get() = _membershipPassUrl ?: ""
    val canAccessApp: Boolean
        get() = _canAccessApp ?: false
    val phoneNumber: String
        get() = _phoneNumber ?: ""
    val venueId: String
        get() = _venueId ?: ""
    val profile: Profile?
        get() = profileIdentifier?.safeGet(document)
    val communicationPreferences: CommunicationPreference?
        get() = _communicationPreferences?.safeGet(document)

    val membership: Membership?
        get() = membershipResource?.safeGet(document)

    val localHouse: Venue?
        get() = localHouseResource?.safeGet(document)
    val favoriteVenues: List<Venue>
        get() = favoriteVenuesResource?.safeGet(document) ?: listOf()
    val favoriteContentCategories: List<ContentCategory>
        get() = favoriteCategoriesResource?.safeGet(document) ?: listOf()
    val attendance: Attendance? get() = _attendance?.safeGet(document)
}

data class Address(
    @field:Json(name = "lines") var lines: List<String>? = null,
    @field:Json(name = "postal_code") var postalCode: String? = "",
    @field:Json(name = "locality") var locality: String? = "",
    @field:Json(name = "country") var country: String? = "",
) : Serializable



