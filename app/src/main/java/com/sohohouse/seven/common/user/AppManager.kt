package com.sohohouse.seven.common.user

import com.sohohouse.seven.common.prefs.PrefsManager
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppManager @Inject constructor(
    private val prefManager: PrefsManager,
    val userManager: UserManager,
) {

    var iconType: IconType
        get() {
            if (userManager.isStaff) return IconType.STAFF
            if (userManager.subscriptionType == SubscriptionType.CONNECT) return IconType.CONNECT
            return getDefaultOrFriendsIcon()
        }
        set(value) {
            prefManager.iconType = value
            Timber.d("setIconType = ${value.name}")
        }

    private fun getDefaultOrFriendsIcon(): IconType {
        val iconFromPref = prefManager.iconType
        when (userManager.subscriptionType) {
            SubscriptionType.FRIENDS -> {
                return when (iconFromPref) {
                    IconType.DEFAULT -> iconFromPref
                    IconType.FRIENDS_V1 -> iconFromPref
                    else -> IconType.FRIENDS
                }
            }
            else -> {
                return when (iconFromPref) {
                    IconType.DEFAULT_V1 -> iconFromPref
                    IconType.DEFAULT_V2 -> iconFromPref
                    else -> IconType.DEFAULT
                }
            }
        }
    }

}