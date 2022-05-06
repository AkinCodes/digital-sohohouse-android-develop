package com.sohohouse.seven.common.extensions

import com.sohohouse.seven.R
import com.sohohouse.seven.common.interactors.model.UserVenue
import com.sohohouse.seven.common.user.MembershipStatus
import com.sohohouse.seven.common.user.MembershipType
import com.sohohouse.seven.common.user.SubscriptionType.NONE
import com.sohohouse.seven.network.core.models.Account
import com.sohohouse.seven.network.core.models.Venue

val Account.isFounder get() = membership?.isFounder ?: false
val Account.imageUrl get() = profile?.imageUrl
val Account.firstName get() = profile?.firstName
val Account.lastName get() = profile?.lastName
val Account.venueName get() = localHouse?.name
val Account.venueIcon get() = localHouse?.venueIcons?.lightPng
val Account.membershipStatus
    get() = membership?.status?.asEnumOrDefault(MembershipStatus.NONE) ?: MembershipStatus.NONE
val Account.membershipType
    get() = membership?.membershipType?.asEnumOrDefault(MembershipType.NONE) ?: MembershipType.NONE
val Account.subscriptionType
    get() = this.membership?.subscriptionType?.asEnumOrDefault(NONE) ?: NONE
val Account.accessibleVenuesIds
    get() = this.membership?.accessibleVenuesResource?.get()?.mapNotNull { it.id }
val Account.isStaff get() = profile?.isStaff == true

fun Venue?.createVenueVm(): UserVenue {
    return UserVenue(
        _id = this?.id,
        _name = this?.name,
        _venueIconURL = this?.venueIcons?.lightPng,
        _venueColor = this?.venueColors?.dark
    )
}

fun Account.isFeatureEnabled(featureId: String): Boolean {
    return features.map { it.id }.contains(featureId)
}

val Account.membershipDisplayName: Int?
    get() = if (membership?.code?.startsWith("CWH") == true) {
        R.string.subscription_type_cwh
    } else {
        membership?.subscriptionType?.asEnumOrDefault(NONE)?.resString
    }