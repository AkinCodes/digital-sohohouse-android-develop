package com.sohohouse.seven.intro.adapter

import com.sohohouse.seven.base.GenericAdapter.ViewHolder
import com.sohohouse.seven.databinding.ViewHolderOnboardingWelcomeBinding

class IntroLandingViewHolder(private val binding: ViewHolderOnboardingWelcomeBinding) :
    ViewHolder<IntroLanding>(binding.root) {

    override fun bind(item: IntroLanding) {
        with(binding) {
            header.text = item.welcomeHeader
            welcomeMessage.text = item.welcomeMessage

            membershipCard.setMembership(
                subscriptionType = item.subscriptionType,
                membershipDisplayName = item.membershipDisplayName,
                memberName = item.memberName,
                membershipId = item.membershipId,
                shortCode = item.shortCode,
                profileImageUrl = item.profileImageUrl,
                loyaltyId = item.loyaltyId,
                isStaff = item.isStaff,
            )
        }
    }
}
