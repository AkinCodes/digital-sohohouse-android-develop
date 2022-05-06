package com.sohohouse.seven.network.chat.model

class ChatMember(
    val id: String,
    val name: String,
    val profileUrl: String,
    val isActive: Boolean,
    val isInvited: Boolean,
    val isStaff: Boolean,
)