package com.sohohouse.seven.home.houseboard.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.views.MembershipCardView
import com.sohohouse.seven.home.houseboard.items.MembershipCardItem

class MembershipCardViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.item_membership_card_houseboard, parent, false)
) {

    fun bind(item: MembershipCardItem) {
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
