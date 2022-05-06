package com.sohohouse.sendbird.repo

import com.sendbird.android.BaseChannel
import com.sendbird.android.BaseMessage
import com.sendbird.android.SendBird

class OnMessagesReceived(
    private val channel: BaseChannel,
    var onMessageReceived: (BaseMessage) -> Unit = {},
    var onMessageUpdated: (BaseMessage) -> Unit = {}
) : SendBird.ChannelHandler() {
    override fun onMessageReceived(p0: BaseChannel?, p1: BaseMessage?) {
        if (p1 != null && channel.name == p0?.name)
            onMessageReceived(p1)
    }

    override fun onMessageUpdated(channel: BaseChannel?, message: BaseMessage?) {
        super.onMessageUpdated(channel, message)
        if (message != null && this.channel.name == channel?.name)
            onMessageUpdated(message)
    }
}