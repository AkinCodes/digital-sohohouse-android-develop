package com.sohohouse.seven.onboarding.benefits

import android.view.View
import com.sohohouse.seven.base.GenericAdapter
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.common.extensions.replaceBraces
import com.sohohouse.seven.common.views.MembershipCardView
import com.sohohouse.seven.databinding.ViewHolderOnboardingBenefitsHeaderBinding
import com.sohohouse.seven.databinding.ViewHolderOnboardingBenefitsItemBinding

class BenefitHeaderViewHolder(val binding: ViewHolderOnboardingBenefitsHeaderBinding) :
    GenericAdapter.ViewHolder<BenefitHeaderItem>(binding.root) {

    override fun bind(item: BenefitHeaderItem) {
        binding.headerTitle.setText(item.title)
    }
}

class BenefitItemViewHolder(val binding: ViewHolderOnboardingBenefitsItemBinding) :
    GenericAdapter.ViewHolder<BenefitItem>(binding.root) {

    override fun bind(item: BenefitItem) = with(binding) {
        itemTitle.setText(item.title)
        if (item is BenefitItem.Houses.LocalHouse) {
            itemSubtitle.text = getString(item.subtitle).replaceBraces(item.houseName)
        } else itemSubtitle.setText(item.subtitle)
    }
}

class MembershipCardViewHolder(itemView: View) :
    GenericAdapter.ViewHolder<MembershipCardItem>(itemView) {

    override fun bind(item: MembershipCardItem) {
        (itemView as MembershipCardView).setMembership(
            subscriptionType = item.subscriptionType,
            membershipDisplayName = item.membershipDisplayName,
            memberName = item.memberName,
            membershipId = item.membershipId,
            shortCode = item.shortCode,
            profileImageUrl = item.profileImageUrl,
            loyaltyId = item.loyaltyId,
            isStaff = item.isStaff
        )
    }

}
