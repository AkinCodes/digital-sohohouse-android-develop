package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.chat.invite.SentMessageRequest
import com.sohohouse.seven.network.core.api.CoreApi
import retrofit2.Call

class PostMessageRequest(private val sendMessageRequest: SentMessageRequest) :
    CoreAPIRequest<Void> {
    override fun createCall(api: CoreApi): Call<out Void> {
        return api.sendMessageRequest(sendMessageRequest)
    }
}