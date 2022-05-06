package com.sohohouse.seven.more

import com.sohohouse.seven.BuildConfig
import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import javax.inject.Inject

class AccountViewInfo @Inject constructor(
    userManager: UserManager
) {

    val menus: List<AccountMenu> = when (userManager.subscriptionType) {
        SubscriptionType.FRIENDS -> mutableListOf(
            AccountMenu.MEMBERSHIP_DETAILS,
            AccountMenu.GUEST_INVITATIONS,
            AccountMenu.PAYMENT_METHODS,
            AccountMenu.BOOKINGS,
            AccountMenu.SETTINGS,
            AccountMenu.CONTACT_US,
            AccountMenu.TERMS_AND_POLICIES,
            AccountMenu.FAQS_FRIENDS,
            AccountMenu.LOGOUT
        )
        else -> mutableListOf(
            AccountMenu.MEMBERSHIP_DETAILS,
            AccountMenu.MY_NETWORK,
            AccountMenu.GUEST_INVITATIONS,
            AccountMenu.PAYMENT_METHODS,
            AccountMenu.BOOKINGS,
            AccountMenu.HOUSE_PREFERENCES,
            AccountMenu.SETTINGS,
            AccountMenu.CONTACT_US,
            AccountMenu.TERMS_AND_POLICIES,
            AccountMenu.FAQS,
            AccountMenu.LOGOUT
        )
    }.apply {
        if (BuildConfig.DEBUG) {
            //TODO add if FeatureFlags.housePay to add menu item
            //  add(3, AccountMenu.HOUSE_PAY)
            add(AccountMenu.APP_VERSION)
        }

    }
}