package com.sohohouse.seven.network.chat

import com.sohohouse.seven.network.chat.model.MiniProfile


interface ChatConnectionRepo {
    suspend fun connect(userData: MiniProfile)

    fun registerPushTokenForCurrentUser(token: String)
}