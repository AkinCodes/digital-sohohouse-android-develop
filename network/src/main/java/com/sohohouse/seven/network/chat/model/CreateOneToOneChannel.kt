package com.sohohouse.seven.network.chat.model

data class CreateOneToOneChannel(
    val oneToOneChatMembers: OneToOneChatMembers,
    val imageUrl: String,
    val name: String,
)