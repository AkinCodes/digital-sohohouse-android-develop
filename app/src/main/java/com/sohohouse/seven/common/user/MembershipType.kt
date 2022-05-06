package com.sohohouse.seven.common.user

import androidx.annotation.StringRes

import com.sohohouse.seven.R

enum class MembershipType(@StringRes val resString: Int) {
    REGULAR(R.string.membership_type_regular),
    U27(R.string.membership_type_u27),
    CHILD(R.string.membership_type_child),
    NONE(R.string.membership_type_none)
}
