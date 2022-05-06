package com.sohohouse.seven.common.user

import androidx.annotation.StringRes
import com.sohohouse.seven.R

enum class SubscriptionType(@StringRes var resString: Int) {
    LOCAL(R.string.subscription_type_local),
    EVERY(R.string.subscription_type_every),
    EVERY_PLUS(R.string.subscription_type_every_plus),
    FRIENDS(R.string.subscription_type_friends),
    CWH(R.string.subscription_type_cwh),
    CONNECT(R.string.subscription_type_connect),
    NONE(R.string.subscription_type_non_member);
}