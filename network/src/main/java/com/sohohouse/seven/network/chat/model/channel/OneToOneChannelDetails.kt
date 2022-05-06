package com.sohohouse.seven.network.chat.model.channel

import com.sohohouse.seven.network.chat.model.message.Message
import kotlinx.coroutines.flow.Flow
import java.io.File

data class OneToOneChannelDetails(
    val oneToOneChannel: OneToOneChatChannel,
    val messages: Flow<Iterable<Message>>,
    val sendMessage: suspend (String) -> Unit,
    val sendImageMessage: suspend (File) -> Unit,
    private val _fetchMoreMessages: suspend () -> Unit,
    val markAsRead: suspend () -> Unit,
) {

    private var isFetching = false

    suspend fun fetchMoreMessages() {
        if (isFetching) return
        try {
            isFetching = true
            _fetchMoreMessages()
        } finally {
            isFetching = false
        }
    }
}