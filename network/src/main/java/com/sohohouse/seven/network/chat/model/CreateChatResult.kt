package com.sohohouse.seven.network.chat.model

import com.sohohouse.seven.network.chat.ChannelId

data class CreateChatResult(
    val channelId: ChannelId = "",
    val channelUrl: String = "",
    val isNew: Boolean,
)