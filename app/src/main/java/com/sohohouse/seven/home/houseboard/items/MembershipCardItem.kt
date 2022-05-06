package com.sohohouse.seven.home.houseboard.items

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.user.SubscriptionType

data class MembershipCardItem(
    val subscriptionType: SubscriptionType,
    val membershipDisplayName: Int?,
    val memberName: String,
    val membershipId: String,
    val shortCode: String? = null,
    val profileImageUrl: String? = null,
    val loyaltyId: String? = null,
    val isStaff: Boolean = false
) : DiffItem {
    override val key: Any?
        get() = javaClass
}