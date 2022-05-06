package com.sohohouse.seven.network.chat.model

class MiniProfile(
    val profileId: String,
    val profileImageUrl: String = "",
    val nickName: String,
    val isStaff: Boolean = false,
)