package com.sohohouse.seven.network.chat.model

import com.sohohouse.seven.network.chat.ChannelId

data class InviteUser(
    val id: String,
    val profileUrl: String,
    val friendName: String,
    val channelId: ChannelId,
)