package com.sohohouse.seven.guests

import com.sohohouse.seven.BuildConfigManager
import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.house.HouseType
import com.sohohouse.seven.common.house.HouseType.*
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.SubscriptionType.*
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.network.core.models.Account
import com.sohohouse.seven.network.core.models.Venue
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GuestListHelper @Inject constructor(
    private val featureFlags: FeatureFlags,
    private val stringProvider: StringProvider,
    private val buildConfigManager: BuildConfigManager,
) {

    fun buildHouseUIItem(venue: Venue, enabled: Boolean): GuestListFormHouseItem {
        return GuestListFormHouseItem(
            venue.id,
            venue.name,
            venue.buildAddress(singleLine = true),
            venue.venueIcons.darkPng,
            enabled
        )
    }

    fun buildDateUIItem(date: Date, enabled: Boolean): GuestListFormDateItem {
        return GuestListFormDateItem(
            R.string.label_date,
            date.getDayAndMonthFormattedDate(),
            enabled
        )
    }

    fun canInviteGuests(
        account: Account,
        subscriptionType: SubscriptionType,
        venues: VenueList
    ): Boolean {
        if (!account.isFeatureEnabled(FeatureFlags.Ids.FEATURE_ID_GUEST_REGISTRATION)) return false

        if (venues.any { it.venueType == STUDIO.name && it.isOpenForBusiness() }) return true

        if (subscriptionType == FRIENDS) return false

        val membership = account.membership

        val isLocalMember = account.subscriptionType == LOCAL

        val accessibleVenues =
            membership?.accessibleVenuesResource?.map { venues.findById(it.id) } ?: emptyList()

        for (venue in accessibleVenues) {
            val venueType = venue?.venueType?.asEnumOrDefault<HouseType>(null) ?: continue
            when (venueType) {
                HOUSE -> if (venue.isOpenForBusiness()) return true
                HouseType.CWH -> if (!isLocalMember) return true
                else -> {
                }//do nothing
            }
        }

        return false
    }


    fun buildShareMessage(inviteId: String, vanueName: String): String {
        val message = stringProvider.getString(R.string.share_guest_invite_message, vanueName)
        val link = "${buildConfigManager.webHostName}/invite/$inviteId"
        return "$message\n$link"
    }

}