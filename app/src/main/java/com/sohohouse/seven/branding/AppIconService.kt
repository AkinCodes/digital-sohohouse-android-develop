package com.sohohouse.seven.branding

import android.app.IntentService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.sohohouse.seven.branding.launcher.*
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.asEnumOrDefault
import com.sohohouse.seven.common.user.IconType
import com.sohohouse.seven.common.user.SubscriptionType

class AppIconService : IntentService("AppIconService") {

    override fun onHandleIntent(intent: Intent?) {
        val type =
            intent?.getStringExtra(BundleKeys.ICON_TYPE)?.asEnumOrDefault<IconType>(null) ?: return
        when (type) {
            IconType.STAFF -> setComponentEnabledSetting(staff = PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            IconType.FRIENDS -> setComponentEnabledSetting(friends = PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            IconType.FRIENDS_V1 -> setComponentEnabledSetting(friendsV1 = PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            IconType.CONNECT -> setComponentEnabledSetting(connect = PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            IconType.DEFAULT_V1 -> setComponentEnabledSetting(defaultV1 = PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            IconType.DEFAULT_V2 -> setComponentEnabledSetting(defaultV2 = PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            else -> setComponentEnabledSetting(default = PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
        }

    }

    private fun setComponentEnabledSetting(
        default: Int = PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        fullMember: Int = PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        friends: Int = PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        connect: Int = PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        guest: Int = PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        staff: Int = PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        defaultV1: Int = PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        defaultV2: Int = PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        friendsV1: Int = PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
    ) {
        changeAppIcon(DefaultLauncherAlias::class.java, default)
        changeAppIcon(SohoHouseLauncherAlias::class.java, fullMember)
        changeAppIcon(FriendsLauncherAlias::class.java, friends)
        changeAppIcon(ConnectLauncherAlias::class.java, connect)
        changeAppIcon(GuestLauncherAlias::class.java, guest)
        changeAppIcon(StaffLauncherAlias::class.java, staff)
        changeAppIcon(DefaultLauncherAliasV1::class.java, defaultV1)
        changeAppIcon(DefaultLauncherAliasV2::class.java, defaultV2)
        changeAppIcon(FriendsLauncherAliasV1::class.java, friendsV1)
    }

    private fun changeAppIcon(aliasClass: Class<*>, enableState: Int) {
        packageManager.setComponentEnabledSetting(
            ComponentName(baseContext, aliasClass),
            enableState, PackageManager.DONT_KILL_APP
        )
    }

    companion object {
        internal const val UPDATE_ICON = "com.sohohouse.seven.UPDATE_ICON"

        fun isComponentEnabled(
            context: Context,
            subscriptionType: SubscriptionType,
            isStaff: Boolean
        ): Boolean {
            val componentName = when {
                isStaff -> ComponentName(context, StaffLauncherAlias::class.java)
                SubscriptionType.CONNECT == subscriptionType -> ComponentName(
                    context,
                    ConnectLauncherAlias::class.java
                )
                SubscriptionType.NONE == subscriptionType -> ComponentName(
                    context,
                    GuestLauncherAlias::class.java
                )
                SubscriptionType.FRIENDS == subscriptionType -> return checkAppIconForFriendsSubscription(
                    context
                )
                else -> return checkAppIconForRegularSubscription(context)
            }
            return context.packageManager.getComponentEnabledSetting(componentName) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        }

        private fun checkAppIconForFriendsSubscription(context: Context): Boolean {
            listOf(
                ComponentName(context, DefaultLauncherAlias::class.java),
                ComponentName(context, DefaultLauncherAliasV1::class.java),
                ComponentName(context, DefaultLauncherAliasV2::class.java),
                ComponentName(context, StaffLauncherAlias::class.java),
                ComponentName(context, ConnectLauncherAlias::class.java),
                ComponentName(context, GuestLauncherAlias::class.java),
            )
                .forEach {
                    if (context.packageManager.getComponentEnabledSetting(it) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) return false
                }
            return true
        }

        private fun checkAppIconForRegularSubscription(context: Context): Boolean {
            listOf(
                ComponentName(context, StaffLauncherAlias::class.java),
                ComponentName(context, FriendsLauncherAlias::class.java),
                ComponentName(context, ConnectLauncherAlias::class.java),
                ComponentName(context, GuestLauncherAlias::class.java),
                ComponentName(context, FriendsLauncherAliasV1::class.java),
            )
                .forEach {
                    if (context.packageManager.getComponentEnabledSetting(it) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) return false
                }
            return true
        }

    }
}