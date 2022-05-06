package com.sohohouse.seven.onboarding.benefits

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.base.GenericAdapter
import com.sohohouse.seven.databinding.ViewHolderOnboardingBenefitsHeaderBinding
import com.sohohouse.seven.databinding.ViewHolderOnboardingBenefitsItemBinding

class OnboardingBenefitsAdapter : GenericAdapter<BenefitAdapterItem>() {

    override fun getItemViewType(position: Int) = items[position].itemType

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder<BenefitAdapterItem> {
        return when (viewType) {
            BenefitAdapterItem.ITEM_TYPE_BENEFIT_HEADER -> {
                val binding = ViewHolderOnboardingBenefitsHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                BenefitHeaderViewHolder(binding) as ViewHolder<BenefitAdapterItem>
            }
            BenefitAdapterItem.ITEM_TYPE_BENEFIT_ITEM -> {
                val binding = ViewHolderOnboardingBenefitsItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                BenefitItemViewHolder(binding) as ViewHolder<BenefitAdapterItem>
            }
            BenefitAdapterItem.ITEM_TYPE_MEMBERSHIP_CARD -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_membership_card_account, parent, false)
                MembershipCardViewHolder(itemView) as ViewHolder<BenefitAdapterItem>
            }
            else -> throw IllegalStateException("Unknown ItemViewType")
        }
    }

}
