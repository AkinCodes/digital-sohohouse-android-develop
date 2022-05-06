package com.sohohouse.seven.connect.message.chat.content

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.fullName
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import com.sohohouse.seven.network.chat.ChannelId
import com.sohohouse.seven.network.chat.ChatChannelsRepo
import com.sohohouse.seven.network.chat.ChatConnectionRepo
import com.sohohouse.seven.network.chat.RequestChannel
import com.sohohouse.seven.network.chat.model.ChannelOperationException
import com.sohohouse.seven.network.chat.model.channel.OneToOneChannelDetails
import com.sohohouse.seven.network.chat.model.channel.OneToOneChatChannel
import com.sohohouse.seven.network.core.models.MutualConnectionRequests
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.Connected
import com.sohohouse.seven.profile.MutualConnectionStatus
import com.sohohouse.seven.profile.ProfileRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import moe.banana.jsonapi2.HasOne
import java.io.File

class ChatContentViewModel @AssistedInject constructor(
    analyticsManager: AnalyticsManager,
    private val userManager: UserManager,
    private val chatChannelsRepo: ChatChannelsRepo,
    private val connectionRepo: ConnectionRepository,
    private val chatConnectionRepo: ChatConnectionRepo,
    @Assisted("channelUrl") private val channelUrl: String,
    @Assisted("channelId") private val channelId: ChannelId,
    @Assisted("memberProfileId") private val _recipientProfileID: String,
    private val profileRepository: ProfileRepository,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager), Errorable.ViewModel by Errorable.ViewModelImpl(),
    Loadable.ViewModel by Loadable.ViewModelImpl() {

    val recipient = MutableLiveData<UiModel>()
    val messagingInvitation = MutableLiveData<Pair<String, String>>()
    val messages = MutableLiveData<List<ChatContentListItem>>()
    val threeDotMenuAction = LiveEvent<Pair<String, Boolean>>()
    val openProfile = LiveEvent<ProfileItem>()
    val showLoader = MutableLiveData<Boolean>()
    val isRecipientBlocked = connectionRepo.blockedMembers.map { it.contains(recipientProfileID) }
        .asLiveData(ioDispatcher)
    private var oneToOneChannelDetails: OneToOneChannelDetails? = null
    private var profile: ProfileItem? = null
    private val recipientProfileID: String
        get() = _recipientProfileID.ifEmpty {
            oneToOneChannelDetails?.oneToOneChannel?.memberId(userManager.profileID) ?: ""
        }

    var onMediaClick: (Pair<String?, String?>) -> Unit = {}

    init {
        showLoader.postValue(true)
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            chatConnectionRepo.connect(userManager.getMiniProfileForSB())

            val oneToOneChannelDetails = chatChannelsRepo.channel(
                RequestChannel(
                    channelId = channelId,
                    channelUrl = channelUrl
                )
            ).also { oneToOneChannel ->
                oneToOneChannel.messages.onEach {
                    showLoader.postValue(false)
                    messages.postValue(
                        ChatContentListItem(
                            messageContents = it.toList(),
                            myProfileId = userManager.profileID,
                            profileIconCallback = ::openRecipientProfile,
                            onMediaClick = onMediaClick
                        )
                    )
                }.launchIn(this)
                oneToOneChannelDetails = oneToOneChannel
                checkInvitation(oneToOneChannel.oneToOneChannel)
            }

            logAnalyticEvents(analyticsManager, oneToOneChannelDetails.oneToOneChannel)

            launch(coroutineContext) {
                connectionRepo.getBlockedMembers()
            }

            launch(coroutineContext) {
                oneToOneChannelDetails.markAsRead()
            }

            launch(coroutineContext) {
                getRecipientProfile(oneToOneChannelDetails.oneToOneChannel)
            }
        }
    }

    private fun checkInvitation(oneToOneChannel: OneToOneChatChannel) {
        if (oneToOneChannel.members.firstOrNull {
                it.id == userManager.profileID && it.isInvited
            } != null) {
            messagingInvitation.postValue(
                Pair(
                    oneToOneChannel.memberName(userManager.profileID),
                    oneToOneChannel.channelUrl
                )
            )
        }
    }

    private fun getRecipientProfile(oneToOneChannel: OneToOneChatChannel) {
        profileRepository.getProfile(recipientProfileID)
            .ifValue {
                profile = ProfileItem(
                    it, MutualConnectionStatus.from(
                        it,
                        userManager.profileID,
                        connectionRepo.blockedMembers.value
                    ), showMessageButton = false
                )

                recipient.postValue(
                    UiModel(
                        name = it.fullName,
                        imageUrl = it.imageUrl,
                        hasMessages = oneToOneChannel.lastMessage.isNotEmpty(),
                        showConnectionBanner = oneToOneChannel.lastMessage.isEmpty() && profile?.status != Connected,
                        isConnected = profile?.status == Connected,
                        sendRequest = { sendRequest() }
                    ))
            }
    }

    fun shouldShowSendRequest(): Boolean {
        return (recipient.value?.isConnected?.not() ?: false) && (messages.value?.isEmpty()
            ?: false)
    }

    fun openRecipientProfile() {
        if (profile != null) {
            openProfile.postValue(profile)
        }
    }

    private fun sendRequest() {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            connectionRepo.postConnectionRequest(
                MutualConnectionRequests(
                    message = "",
                    receiver = HasOne(Profile().also { it.id = recipientProfileID })
                )
            )
        }
    }

    fun fetchMoreMessages() {
        val oneToOneChannelDetails = oneToOneChannelDetails ?: return
        viewModelScope.launch(Dispatchers.IO) {
            oneToOneChannelDetails.fetchMoreMessages()
        }
    }

    fun sendMessage(message: String) {
        val oneToOneChannelDetails = oneToOneChannelDetails ?: return
        viewModelScope.launch(viewModelContext) {
            val isEmpty = (messages.value ?: emptyList()).isEmpty()
            if (isEmpty) {
                launch(coroutineContext) {
                    oneToOneChannelDetails.oneToOneChannel.inviteUserWithId(recipientProfileID)
                }

                launch(coroutineContext) {
                    connectionRepo.postMessageRequest(
                        oneToOneChannelDetails.oneToOneChannel.channelUrl,
                        listOf(recipientProfileID)
                    )
                }
            }

            try {
                oneToOneChannelDetails.sendMessage(message)
            } catch (ex: ChannelOperationException) {
                onError(ex)
            }

            if (isEmpty) {
                chatChannelsRepo.channels()
            }
        }
    }

    fun sendImageMessage(file: File) {
        val oneToOneChannelDetails = oneToOneChannelDetails ?: return
        viewModelScope.launch(viewModelContext) {
            val isEmpty = (messages.value ?: emptyList()).isEmpty()
            if (isEmpty) {
                launch(coroutineContext) {
                    oneToOneChannelDetails.oneToOneChannel.inviteUserWithId(recipientProfileID)
                }

                launch(coroutineContext) {
                    connectionRepo.postMessageRequest(
                        oneToOneChannelDetails.oneToOneChannel.channelUrl,
                        listOf(recipientProfileID)
                    )
                }
            }

            try {
                showLoader.postValue(true)
                oneToOneChannelDetails.sendImageMessage(file)
            } catch (ex: ChannelOperationException) {
                showLoader.postValue(false)
                onError(ex)
            }

            if (isEmpty) {
                chatChannelsRepo.channels()
            }
        }
    }

    fun openThreeDotMenu() {
        val oneToOneChannel = oneToOneChannelDetails?.oneToOneChannel ?: return
        analyticsManager.logEventAction(
            AnalyticsManager.Action.MessagingMessagesMoreTap,
            Bundle().apply {
                putString(
                    AnalyticsManager.Parameters.MessagingRecipientGlobalID.value,
                    oneToOneChannel.members.find { it.id != userManager.profileID }?.id
                        ?: ""
                )
                putBoolean(
                    AnalyticsManager.Parameters.MessagingMessagesAreEmpty.value,
                    !oneToOneChannel.isUnread
                )
            }
        )

        val id = oneToOneChannel.members.find { it.id != userManager.profileID }?.id ?: ""
        threeDotMenuAction.postValue(id to !oneToOneChannel.isUnread)
    }

    private fun logAnalyticEvents(
        analyticsManager: AnalyticsManager,
        oneToOneChannel: OneToOneChatChannel
    ) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.ChatMessagesOpened,
            bundleOf(
                AnalyticsManager.Parameters.GlobalId.value to userManager.profileID,
                "number_participants" to oneToOneChannel.members.size,
                AnalyticsManager.Parameters.RecipientGlobalId.value to recipientProfileID,
                "messages_are_empty" to oneToOneChannel.lastMessage.isEmpty()
            )
        )
    }

    fun unblockMember() {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            connectionRepo.patchUnblockMember(recipientProfileID)
                .ifError { showError() }
            setLoadingState(LoadingState.Idle)
        }
    }


    data class UiModel(
        val name: String,
        val imageUrl: String,
        val showConnectionBanner: Boolean,
        val hasMessages: Boolean,
        val isConnected: Boolean,
        val sendRequest: () -> Unit
    )

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("channelId") channelId: ChannelId,
            @Assisted("channelUrl") channelUrl: String,
            @Assisted("memberProfileId") memberProfileId: String
        ): ChatContentViewModel
    }

}
