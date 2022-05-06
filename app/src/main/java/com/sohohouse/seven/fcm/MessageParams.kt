package com.sohohouse.seven.fcm

import com.sohohouse.seven.common.extensions.asEnumOrDefault
import com.sohohouse.seven.common.navigation.NavigationTrigger

data class MessageParams(
    val id: String,
    val title: String,
    val alert: String,
    val trigger: String,
    val screenName: String,
    val sid: String,
    val notificationId: String,
    val priority: Int
) {
    val navigationTrigger: NavigationTrigger?
        get() = trigger.asEnumOrDefault<NavigationTrigger>(null)
}