package com.sohohouse.seven.onboarding.benefits

import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.user.SubscriptionType

interface BenefitAdapterItem : DiffItem {
    val title: Int
    val subtitle: Int
    val itemType: Int

    companion object {
        const val ITEM_TYPE_BENEFIT_HEADER = 0
        const val ITEM_TYPE_BENEFIT_ITEM = 1
        const val ITEM_TYPE_MEMBERSHIP_CARD = 2
    }
}

data class BenefitHeaderItem(override val title: Int = R.string.onboarding_benefit_header) :
    BenefitAdapterItem {
    // Ignored
    override val subtitle: Int = 0

    override val itemType: Int = BenefitAdapterItem.ITEM_TYPE_BENEFIT_HEADER
}

sealed class BenefitItem : BenefitAdapterItem {

    override val itemType: Int = BenefitAdapterItem.ITEM_TYPE_BENEFIT_ITEM

    abstract class Houses(
        override val title: Int = R.string.onboarding_benefits_houses_title,
        override val subtitle: Int
    ) : BenefitItem() {
        class EveryHouse : Houses(subtitle = R.string.onboarding_benefits_every_house)

        class LocalHouse(val houseName: String) :
            Houses(subtitle = R.string.onboarding_benefits_local_house)
    }

    data class Events(
        override val title: Int = R.string.onboarding_benefits_events_title,
        override val subtitle: Int = R.string.onboarding_benefits_events
    ) : BenefitItem()

    data class RoomBookings(
        override val title: Int = R.string.onboarding_benefits_room_bookings_title,
        override val subtitle: Int = R.string.onboarding_benefits_room_bookings
    ) : BenefitItem()

    data class WellBeing(
        override val title: Int = R.string.onboarding_benefits_wellbeing_title,
        override val subtitle: Int = R.string.onboarding_benefits_wellbeing
    ) : BenefitItem()

    data class FoodBeverage(
        override val title: Int = R.string.onboarding_benefits_food_beverage_title,
        override val subtitle: Int = R.string.onboarding_benefits_food_beverage
    ) : BenefitItem()

    data class Spa(
        override val title: Int = R.string.onboarding_benefits_spa_title,
        override val subtitle: Int
    ) : BenefitItem()

    data class SohoHome(
        override val title: Int = R.string.onboarding_benefits_soho_home_title,
        override val subtitle: Int
    ) : BenefitItem()
}

data class MembershipCardItem(
    val subscriptionType: SubscriptionType,
    val membershipDisplayName: Int?,
    val memberName: String,
    val membershipId: String,
    val shortCode: String? = null,
    val profileImageUrl: String? = null,
    val loyaltyId: String? = null,
    val isStaff: Boolean = false
) : BenefitAdapterItem {
    override val title: Int
        get() = 0
    override val subtitle: Int
        get() = 0
    override val itemType: Int
        get() = BenefitAdapterItem.ITEM_TYPE_MEMBERSHIP_CARD

}