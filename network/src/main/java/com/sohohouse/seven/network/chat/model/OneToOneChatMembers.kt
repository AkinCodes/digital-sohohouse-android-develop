package com.sohohouse.seven.network.chat.model

class OneToOneChatMembers(
    val myProfileId: String,
    val memberProfileId: String,
) {
    val channelId: String
        get() {
            val (smaller, bigger) = if (myProfileId > memberProfileId)
                memberProfileId to myProfileId
            else
                myProfileId to memberProfileId
            return "${smaller}_${bigger}"
        }
}