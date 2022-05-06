package com.sohohouse.seven.more.notifications.recyclerview

enum class MoreNotificationsAdapterItemType {
    TOP_SUPPORTING,
    HEADER,
    NOTIFICATION_OPTION,
    PLATFORM_SETTINGS
}

abstract class MoreNotificationsAdapterItem(val itemType: MoreNotificationsAdapterItemType)

class MoreNotificationsTopSupportingAdapterItem :
    MoreNotificationsAdapterItem(MoreNotificationsAdapterItemType.TOP_SUPPORTING)

class MoreNotificationsHeaderAdapterItem(val text: String) :
    MoreNotificationsAdapterItem(MoreNotificationsAdapterItemType.HEADER)

class MoreNotificationsNotificationOptionAdapterItem(
    val key: String, val subHeaderText: String,
    val supportingText: String,
    var startingValue: Boolean,
    val defaultState: Boolean = true,
    val hasEmailNotifications: Boolean = false,
    val emailKey: String = "",
    var emailState: Boolean = true
) : MoreNotificationsAdapterItem(MoreNotificationsAdapterItemType.NOTIFICATION_OPTION)

class MoreNotificationsPlatformSettingsAdapterItem
    : MoreNotificationsAdapterItem(MoreNotificationsAdapterItemType.PLATFORM_SETTINGS)