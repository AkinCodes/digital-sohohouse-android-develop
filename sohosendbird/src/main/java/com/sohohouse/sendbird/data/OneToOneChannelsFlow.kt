package com.sohohouse.sendbird.data

import com.sohohouse.seven.network.chat.ChannelId
import com.sohohouse.seven.network.chat.model.channel.OneToOneChatChannel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalCoroutinesApi::class)
class OneToOneChannelsFlow :
    MutableStateFlow<List<OneToOneChatChannel>> by MutableStateFlow(mutableListOf()) {

    fun markAsRead(url: ChannelId) {
        mapAndEmit {
            if (it.channelUrl == url)
                it.copy(isUnread = false)
            else
                it
        }
    }

    fun toggleMuteState(url: String) {
        mapAndEmit {
            if (it.channelUrl == url) {
                it.copy(isMuted = !it.isMuted)
            } else {
                it
            }
        }
    }

    private fun mapAndEmit(mapper: (OneToOneChatChannel) -> OneToOneChatChannel) {
        value = value.map(mapper)
    }

}