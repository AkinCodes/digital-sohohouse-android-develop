package com.sohohouse.seven.discover

import com.sohohouse.seven.R
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.discover.housenotes.HouseNotesFragment
import com.sohohouse.seven.discover.houses.HousesFragment
import com.sohohouse.seven.discover.benefits.BenefitsFragment
import javax.inject.Inject

class DiscoverViewInfo @Inject constructor(userManager: UserManager) {

    val screens: List<String> = when (userManager.subscriptionType) {
        SubscriptionType.FRIENDS -> listOf(BenefitsFragment.TAG, StudioSpacesFragment.TAG)
        else -> listOf(HouseNotesFragment.TAG, HousesFragment.TAG, BenefitsFragment.TAG)
    }

    val titles: List<Int> = when (userManager.subscriptionType) {
        SubscriptionType.FRIENDS -> listOf(
            R.string.home_nav_perks_label,
            R.string.home_nav_studio_spaces_label
        )
        else -> listOf(
            R.string.home_nav_house_notes_label,
            R.string.home_nav_houses_label,
            R.string.home_nav_perks_label
        )
    }

}