package com.sohohouse.seven.intro.adapter

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sohohouse.seven.R
import com.sohohouse.seven.common.analytics.AnalyticsManager.Action
import com.sohohouse.seven.common.analytics.AnalyticsManager.Action.*
import com.sohohouse.seven.common.user.SubscriptionType

sealed class IntroItem {
    open val action: Action = Action.None
}

data class IntroLanding constructor(
    val welcomeHeader: String,
    val welcomeMessage: String,
    val membershipId: String,
    val shortCode: String?,
    val subscriptionType: SubscriptionType,
    val membershipDisplayName: Int?,
    val memberName: String,
    val houseLogoUrl: String?,
    val profileImageUrl: String?,
    val loyaltyId: String? = null,
    val isStaff: Boolean = false
) : IntroItem() {
    override val action: Action = OnboardingWelcomeNext
}

abstract class IntroGuide constructor(
    @StringRes val header: Int,
    @StringRes val description: Int,
    @DrawableRes val image: Int? = null
) : IntroItem()

object Privacy : IntroGuide(
    R.string.intro_privacy,
    R.string.intro_privacy_description
) {
    override val action: Action = OnboardingPrivacyNext
}

object DressCode : IntroGuide(
    R.string.intro_dresscode,
    R.string.intro_dresscode_description
) {
    override val action: Action = OnboardingDressCodeNext
}

object MembershipCard : IntroGuide(
    R.string.intro_membership_card,
    R.string.intro_membership_card_description
) {
    override val action: Action = OnboardingMembershipCardNext
}

object MobileFree : IntroGuide(
    R.string.intro_mobile_free,
    R.string.intro_mobile_free_description
) {
    override val action: Action = OnboardingMobileFreeNext
}

object HouseVisit : IntroGuide(
    R.string.intro_house_visit,
    R.string.intro_house_visit_description
) {
    override val action: Action = OnboardingHouseVisitNext
}

object GuestInvite : IntroGuide(
    R.string.intro_invite_guest,
    R.string.intro_invite_guest_description
) {
    override val action: Action = OnboardingGuestInviteNext
}

object KidsFriendly : IntroGuide(
    R.string.intro_children,
    R.string.intro_children_description
) {
    override val action: Action = OnboardingChildrenNext
}

object Billing : IntroGuide(
    R.string.intro_billing,
    R.string.intro_billing_description
) {
    override val action: Action = OnboardingBillingNext
}

object StayWithUs : IntroGuide(
    R.string.intro_stay_with_us,
    R.string.intro_stay_with_us_description,
    R.drawable.friends_stay
) {
    override val action: Action = OnboardingStayWithUsNext
}

object SpacesForFriends : IntroGuide(
    R.string.intro_spaces_for_friends,
    R.string.intro_spaces_for_friends_description,
    R.drawable.friends_spaces
) {
    override val action: Action = OnboardingSFSpacesNext
}

object MemberBenefits : IntroGuide(
    R.string.intro_member_benefits,
    R.string.intro_member_benefits_description,
    R.drawable.friends_benefits
) {
    override val action: Action = OnboardingMemberBenefitsNext
}