package com.sohohouse.sendbird.repo

import com.sendbird.android.BaseMessage
import com.sendbird.android.PreviousMessageListQuery
import com.sendbird.android.SendBirdException
import com.sohohouse.sendbird.toMessage
import com.sohohouse.seven.network.chat.model.ChannelOperationErrorType
import com.sohohouse.seven.network.chat.model.ChannelOperationException
import com.sohohouse.seven.network.chat.model.message.Message

class PagingMessageListQueryResult(
    var onMessagesGet: (List<Message>) -> Unit = {}
) : PreviousMessageListQuery.MessageListQueryResult {
    override fun onResult(messages: MutableList<BaseMessage>?, ex: SendBirdException?) {
        if (messages == null) return
        if (ex != null)
            throw ChannelOperationException(ChannelOperationErrorType.GetMessages, ex, ex.code)
        else {
            onMessagesGet(messages.filter { it.sender != null && it.message != null }.map {
                it.toMessage()
            })
        }
    }
}