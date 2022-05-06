package com.sohohouse.seven.branding

import com.sohohouse.seven.R
import com.sohohouse.seven.common.apihelpers.SohoWebHelper.THEME_DARK
import com.sohohouse.seven.common.apihelpers.SohoWebHelper.THEME_LIGHT
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager

class ThemeManager(private val userManager: UserManager) {

    val baseTheme: Int = R.style.BaseTheme

    val darkTheme: Int
        get() = when {
            userManager.isStaff -> R.style.HouseTheme_Dark_Staff
            SubscriptionType.FRIENDS == userManager.subscriptionType -> R.style.FriendsTheme_Dark
            SubscriptionType.CONNECT == userManager.subscriptionType -> R.style.HouseTheme_Dark_Connect
            else -> R.style.HouseTheme_Dark
        }

    val lightTheme: Int
        get() = when (userManager.subscriptionType) {
            SubscriptionType.FRIENDS -> R.style.FriendsTheme_Light
            else -> R.style.HouseTheme_Light
        }

    val bottomSheetDarkTheme: Int
        get() = when (userManager.subscriptionType) {
            SubscriptionType.FRIENDS -> R.style.FriendsTheme_Dark_BottomSheet
            else -> R.style.HouseTheme_Dark_BottomSheet
        }

    val bottomSheetLightTheme: Int
        get() = when (userManager.subscriptionType) {
            SubscriptionType.FRIENDS -> R.style.FriendsTheme_Light_BottomSheet
            else -> R.style.HouseTheme_Light
        }

    val webTheme: String
        get() = when (userManager.subscriptionType) {
            SubscriptionType.FRIENDS -> THEME_LIGHT
            else -> THEME_DARK
        }
}