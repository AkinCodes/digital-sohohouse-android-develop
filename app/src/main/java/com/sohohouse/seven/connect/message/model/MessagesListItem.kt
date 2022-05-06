package com.sohohouse.seven.connect.message.model

import com.sohohouse.seven.base.DiffItem

data class MessagesListItem constructor(
    val id: String,
    val chatUrl: String,
    val title: String,
    val text: String,
    val strTime: String,
    val hasUnreadMsg: Boolean,
    val isOnline: Boolean,
    val isMuted: Boolean,
    val imageUrl: String,
    val memberId: String,
    val isStaff: Boolean,
    val onMute: () -> Unit,
    val onDelete: () -> Unit,
    val onClick: () -> Unit
) : DiffItem {
    override val key: Any
        get() = id
}
