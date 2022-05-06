package com.sohohouse.seven.connect.message.list

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.LiveEvent
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.connect.message.model.MessagesListItem
import com.sohohouse.seven.network.chat.ChatChannelsRepo
import com.sohohouse.seven.network.chat.ChatConnectionRepo
import com.sohohouse.seven.network.chat.model.channel.OneToOneChatChannel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MessagesListViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val userManager: UserManager,
    private val connectionRepository: ChatConnectionRepo,
    private val channelRepository: ChatChannelsRepo,
    private val dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager), Loadable.ViewModel by Loadable.ViewModelImpl() {

    var isMessagesListFetched: Boolean = false
        private set
    private val _channelsLiveData = MutableLiveData<List<MessagesListItem>>()
    val channelsLiveData: LiveData<List<MessagesListItem>>
        get() = _channelsLiveData
    val removeMessageDialog = LiveEvent<OneToOneChatChannel>()

    init {
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            connectionRepository.connect(userManager.getMiniProfileForSB())
            channelRepository.hasUnreadMessages().collect {
                updateChannels()
            }
        }
        updateChannels()
    }

    private fun updateChannels() {
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            setLoading()
            connectionRepository.connect(userManager.getMiniProfileForSB())

            channelRepository.channels().map {
                channelToMessagesItem(it)
            }.collect {
                setIdle()
                isMessagesListFetched = true
                withContext(Dispatchers.Main) {
                    _channelsLiveData.value = it
                }
            }
        }
    }

    private fun channelToMessagesItem(channels: Iterable<OneToOneChatChannel>): List<MessagesListItem> {
        val aList = mutableListOf<MessagesListItem>()
        channels.iterator().forEach { channelItem ->
            aList.add(
                MessagesListItem(
                    id = channelItem.id,
                    chatUrl = channelItem.channelUrl,
                    title = channelItem.memberName(userManager.profileID),
                    text = channelItem.lastMessage,
                    strTime = channelItem.lastMessageTime.toString(),
                    hasUnreadMsg = channelItem.isUnread,
                    isOnline = channelItem.memberIsOnline(userManager.profileID),
                    memberId = channelItem.memberId(userManager.profileID),
                    isMuted = channelItem.isMuted,
                    imageUrl = channelItem.memberProfileUrl(userManager.profileID),
                    isStaff = channelItem.isMemberStaff(userManager.profileID),
                    onMute = { muteChannel(channelItem) },
                    onDelete = { showDialogToRemoveChannel(channelItem) },
                    onClick = {},
                )
            )
        }
        return aList.toList()
    }

    private fun showDialogToRemoveChannel(channel: OneToOneChatChannel) {
        removeMessageDialog.postValue(channel)
    }

    private fun muteChannel(channel: OneToOneChatChannel) {
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {

            val bundle = Bundle()
            bundle.putInt(UNREAD_COUNT, channel.unreadCount)
            bundle.putString(RECEIPT_ID, channel.id)

            if (channel.isMuted) {
                channelRepository.unMute(channel)
                analyticsManager.logEventAction(AnalyticsManager.Action.MessagingUnMute, bundle)
            } else {
                channelRepository.mute(channel)
                analyticsManager.logEventAction(AnalyticsManager.Action.MessagingMute, bundle)
            }
        }
    }

    fun removeChannel(channel: OneToOneChatChannel) {
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            channelRepository.delete(channel)
        }
    }

    companion object {
        private const val UNREAD_COUNT = "number unread"
        private const val RECEIPT_ID = "recipient_global_id"
    }
}