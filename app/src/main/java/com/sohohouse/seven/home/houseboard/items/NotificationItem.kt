package com.sohohouse.seven.home.houseboard.items

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.navigation.NavigationScreen
import com.sohohouse.seven.common.navigation.NavigationTrigger
import com.sohohouse.seven.network.chat.model.channel.OneToOneChatChannel
import com.sohohouse.seven.network.core.models.notification.Notification
import com.sohohouse.seven.network.core.models.notification.NotificationGroup
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

data class NotificationItem constructor(
    val id: String? = null,
    val title: String? = null,
    val body: String? = null,
    val imageUrl: String? = null,
    val navigationTrigger: NavigationTrigger? = null,
    val navigationScreen: NavigationScreen? = null,
    val navigationResourceId: String? = null,
    val createdAt: ZonedDateTime? = null,
    val seen: Boolean = false,
    val persistent: Boolean? = null,
    val dismissed: Boolean = false,
    val complete: Boolean = false,
    val new: Boolean = false,
    val notificationGroup: NotificationGroup? = null
) : DiffItem {

    constructor(notification: Notification) : this(
        notification.id,
        notification.title,
        notification.body,
        notification.imageUrl,
        NavigationTrigger.from(notification.trigger),
        NavigationScreen.from(notification.navigation?.screenName),
        notification.navigation?.resourceId,
        notification.createdAt,
        notification.seen,
        notification.persistent,
        notification.dismissed,
        notification.complete,
        notification.new,
        notification.notificationGroup
    )

    constructor(otoChannel: OneToOneChatChannel) : this(
        otoChannel.channelUrl,
        "",
        otoChannel.lastMessage,
        "",
        NavigationTrigger.NEW_MESSAGE_INVITE,
        NavigationScreen.MESSAGES,
        otoChannel.channelUrl,
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(otoChannel.invitedAt), ZoneId.systemDefault()),
        !otoChannel.isUnread
    )

    override val key: Any?
        get() = id

    //TODO what is the reason for this?
    override fun equals(other: Any?): Boolean {
        if (other !is NotificationItem) return false
        return this.id == other.id
    }
}