package com.sohohouse.seven.common.user

import androidx.annotation.StringRes

import com.sohohouse.seven.R

enum class MembershipStatus constructor(
    @field:StringRes
    var resString: Int
) {
    APPROVED(R.string.more_membership_status_approved_label),
    CURRENT(R.string.more_membership_status_current_label),
    CHASING(R.string.more_membership_status_chasing_label),
    FROZEN(R.string.more_membership_status_frozen_label),
    EXPIRED(R.string.more_membership_status_expired_label),
    SUSPENDED(R.string.more_membership_status_suspended_label),
    RESIGNED(R.string.more_membership_status_resigned_label),
    INACTIVE(R.string.more_membership_status_inactive_label),
    NONE(R.string.membership_status_none)
}
