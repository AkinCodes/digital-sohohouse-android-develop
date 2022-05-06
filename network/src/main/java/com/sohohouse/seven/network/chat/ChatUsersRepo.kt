package com.sohohouse.seven.network.chat

import com.sohohouse.seven.network.chat.model.ChatMember


/**
 * Without calling [ChatConnectionRepo.connect] first
 * Methods defined inside of this interface will crash.
 */
interface ChatUsersRepo {
    suspend fun users(): Iterable<ChatMember>

    suspend fun accept(channelUrl: String)
    suspend fun decline(channelUrl: String)
}