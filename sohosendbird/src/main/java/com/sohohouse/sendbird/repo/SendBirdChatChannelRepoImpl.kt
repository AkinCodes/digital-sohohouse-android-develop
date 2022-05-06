package com.sohohouse.sendbird.repo

import com.sendbird.android.*
import com.sohohouse.sendbird.data.OneToOneChannelsFlow
import com.sohohouse.sendbird.mapper.SendBirdChannelToOnoToOneChannel
import com.sohohouse.sendbird.toMessage
import com.sohohouse.seven.network.chat.ChannelId
import com.sohohouse.seven.network.chat.ChatChannelsRepo
import com.sohohouse.seven.network.chat.RequestChannel
import com.sohohouse.seven.network.chat.create.DMChannelRequest
import com.sohohouse.seven.network.chat.model.ChannelOperationErrorType
import com.sohohouse.seven.network.chat.model.ChannelOperationException
import com.sohohouse.seven.network.chat.model.CreateChatResult
import com.sohohouse.seven.network.chat.model.CreateOneToOneChannel
import com.sohohouse.seven.network.chat.model.channel.OneToOneChannelDetails
import com.sohohouse.seven.network.chat.model.channel.OneToOneChatChannel
import com.sohohouse.seven.network.chat.model.message.Message
import com.sohohouse.seven.network.chat.model.message.StringSet
import com.sohohouse.seven.network.core.BaseApiService
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.isSuccessful
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


@ExperimentalCoroutinesApi
internal class SendBirdChatChannelRepoImpl(
    private val networkErrorReporter: BaseApiService.NetworkErrorReporter,
    private val sendBirdChannelToOnoToOneChannel: SendBirdChannelToOnoToOneChannel,
    private val sohoApiService: SohoApiService,
) : ChatChannelsRepo {

    private val _state = OneToOneChannelsFlow()

    override fun channels(): Flow<Iterable<OneToOneChatChannel>> {
        val listQuery = GroupChannel.createMyGroupChannelListQuery()
        listQuery.memberStateFilter = GroupChannelListQuery.MemberStateFilter.JOINED
        listQuery.order = GroupChannelListQuery.Order.LATEST_LAST_MESSAGE

        listQuery.next { list, ex ->
            if (ex == null) {
                _state.value = list.filter {
                    it.memberCount <= 2
                }.map(sendBirdChannelToOnoToOneChannel::map)
            } else {
                networkErrorReporter.logException(ex)
            }
        }
        return _state
    }

    override suspend fun channel(requestChannel: RequestChannel): OneToOneChannelDetails {
        val channel = when (requestChannel) {
            is RequestChannel.WithChannelId -> getChannelFromId(requestChannel.channelId)
            is RequestChannel.WithChannelUrl -> getChannelFromUrl(requestChannel.channelUrl)
        }
        val oneToOneChannel = sendBirdChannelToOnoToOneChannel.map(channel)
        val onMessageReceived = OnMessagesReceived(channel)

        val sendMessage: suspend (String) -> Unit = {
            sendUserMessage(channel, it, onMessageReceived)
        }
        val sendImageMessage: suspend (File) -> Unit = {
            sendImageMessage(channel, it, onMessageReceived)
        }

        val query = channel.createPreviousMessageListQuery()
        val pagingMessageListQueryResult = PagingMessageListQueryResult()
        return OneToOneChannelDetails(
            oneToOneChannel,
            getChannelMessages(onMessageReceived, pagingMessageListQueryResult, query),
            sendMessage = sendMessage,
            sendImageMessage = sendImageMessage,
            _fetchMoreMessages = {
                if (query.hasMore()) query.load(pagingMessageListQueryResult)
            },
            markAsRead = {
                if (channel.unreadMessageCount > 0)
                    suspendCoroutine<Unit> { cont ->
                        markAsRead(channel, cont)
                    }
            }
        )
    }

    private suspend fun sendUserMessage(
        channel: GroupChannel,
        it: String,
        onMessageReceived: OnMessagesReceived
    ) {
        return suspendCoroutine { cont ->
            channel.sendUserMessage(it) { msg, ex ->
                val receiverIsBlocked = 900080
                if (ex != null && ex.code == receiverIsBlocked) {
                    cont.resumeWithException(
                        ChannelOperationException(
                            ChannelOperationErrorType.SendMessageToBlockedReceiver,
                            ex,
                            ex.code
                        )
                    )
                } else if (ex != null) {
                    cont.resumeWithException(
                        ChannelOperationException(
                            ChannelOperationErrorType.SendMessage,
                            ex,
                            ex.code
                        )
                    )
                } else {

                    onMessageReceived.onMessageReceived(channel, msg)
                    cont.resume(Unit)
                }

            }
        }
    }

    private suspend fun sendImageMessage(
        channel: GroupChannel,
        file: File,
        onMessageReceived: OnMessagesReceived
    ) {
        return suspendCoroutine { continuation ->
            val size = file.length().toInt()
            val mimeType = when (file.extension) {
                StringSet.JPEG,
                StringSet.JPG -> "image/jpg"
                StringSet.MP4 -> "video/mp4"
                else -> ""
            }
            val thumbnailSizes = listOf(
                FileMessage.ThumbnailSize(480, 800)
            )
            val params = FileMessageParams(file)
                .setFileName(file.name)
                .setFileSize(size)
                .setMimeType(mimeType)
                .setThumbnailSizes(thumbnailSizes)

            channel.sendFileMessage(params) { message, ex ->
                if (ex != null) {
                    continuation.resumeWithException(
                        ChannelOperationException(
                            ChannelOperationErrorType.SendImageMessage, ex, ex.code
                        )
                    )
                }
                onMessageReceived.onMessageReceived(channel, message)
                continuation.resume(Unit)
            }
        }
    }

    private suspend fun getChannelFromUrl(channelUrl: String): GroupChannel {
        return suspendCoroutine {
            GroupChannel.getChannel(channelUrl) { channel, ex ->
                if (ex != null) {
                    it.resumeWithException(
                        ChannelOperationException(
                            ChannelOperationErrorType.GetChannel,
                            ex,
                            ex.code
                        )
                    )
                } else {
                    it.resume(channel)
                }
            }
        }
    }

    private suspend fun getChannelFromId(channelId: ChannelId): GroupChannel {
        return suspendCoroutine {
            val query = GroupChannel.createMyGroupChannelListQuery()
            query.channelNameContainsFilter = channelId
            query.isIncludeEmpty = true
            query.next { list, ex ->
                when {
                    ex != null -> it.resumeWithException(ex)
                    list.isEmpty() -> it.resumeWithException(NoSuchElementException("List is empty. ChannelId: $channelId"))
                    else -> it.resume(list.first())
                }
            }
        }
    }

    private fun markAsRead(channel: GroupChannel, cont: Continuation<Unit>) {
        channel.markAsRead {
            if (it != null)
                cont.resumeWithException(
                    ChannelOperationException(
                        ChannelOperationErrorType.MarkAsRead,
                        it,
                        it.code
                    )
                )
            else {
                _state.markAsRead(channel.url)
                cont.resume(Unit)
            }
        }
    }

    override suspend fun mute(oneToOneChatChannel: OneToOneChatChannel) {
        return setMuteState(oneToOneChatChannel, GroupChannel.PushTriggerOption.OFF)
    }

    override suspend fun unMute(oneToOneChatChannel: OneToOneChatChannel) {
        return setMuteState(oneToOneChatChannel, GroupChannel.PushTriggerOption.DEFAULT)
    }

    private suspend fun setMuteState(
        oneToOneChatChannel: OneToOneChatChannel,
        option: GroupChannel.PushTriggerOption
    ) {
        return suspendCoroutine { cont ->
            GroupChannel.getChannel(oneToOneChatChannel.channelUrl) { channel, ex ->
                if (ex != null)
                    cont.resumeWithException(
                        ChannelOperationException(
                            ChannelOperationErrorType.Mute,
                            ex,
                            ex.code
                        )
                    )

                channel.setMyPushTriggerOption(option) {
                    if (it != null)
                        cont.resumeWithException(
                            ChannelOperationException(
                                ChannelOperationErrorType.Mute,
                                it,
                                it.code
                            )
                        )
                    else {
                        _state.toggleMuteState(channel.url)
                        cont.resume(Unit)
                    }
                }

            }
        }
    }

    override suspend fun delete(oneToOneChatChannel: OneToOneChatChannel) {
        return deleteByUrl(oneToOneChatChannel.channelUrl)
    }

    private suspend fun deleteByUrl(channelUrl: String) {
        return suspendCoroutine { cont ->
            GroupChannel.getChannel(channelUrl) { channel, ex ->
                if (ex != null)
                    cont.resumeWithException(
                        ChannelOperationException(
                            ChannelOperationErrorType.Delete,
                            ex,
                            ex.code
                        )
                    )

                channel.resetMyHistory {
                    if (it != null)
                        cont.resumeWithException(
                            ChannelOperationException(
                                ChannelOperationErrorType.Delete,
                                it,
                                it.code
                            )
                        )
                    else {
                        cont.resume(Unit)
                        _state.value = _state.value.filter { item ->
                            item.channelUrl != channelUrl
                        }
                    }
                }
            }
        }
    }

    override suspend fun create(createOneToOneChannel: CreateOneToOneChannel): CreateChatResult {
        return suspendCoroutine { cont ->
            val query = GroupChannel.createMyGroupChannelListQuery().apply {
                channelNameContainsFilter = createOneToOneChannel.oneToOneChatMembers.channelId
                isIncludeEmpty = true
                memberStateFilter = GroupChannelListQuery.MemberStateFilter.ALL
            }

            query.next { list, ex ->
                when {
                    ex != null -> {
                        cont.resumeWithException(
                            ChannelOperationException(
                                ChannelOperationErrorType.GetChannel,
                                ex,
                                ex.code
                            )
                        )
                    }
                    list.isEmpty() -> continueCreation(cont, createOneToOneChannel, true)
                    else -> continueCreation(cont, createOneToOneChannel, false)
                }
            }
        }
    }

    private fun continueCreation(
        cont: Continuation<CreateChatResult>,
        createOneToOneChannel: CreateOneToOneChannel,
        isNew: Boolean
    ) {
        cont.resume(
            CreateChatResult(
                createOneToOneChannel.oneToOneChatMembers.channelId,
                isNew = isNew
            )
        )
    }

    override suspend fun createNewDMChannel(createOneToOneChannel: CreateOneToOneChannel): CreateChatResult {
        val result = sohoApiService.createDMChannel(
            createOneToOneChannel.oneToOneChatMembers.let {
                DMChannelRequest(listOf(it.myProfileId, it.memberProfileId))
            }
        )
        if (result.isSuccessful()) {
            return CreateChatResult(
                channelUrl = result.response.channelUrl ?: "",
                isNew = true
            )
        } else {
            result.apply {
                throw ChannelOperationException(
                    ChannelOperationErrorType.Create,
                    Exception(message + response),
                    code ?: 0
                )
            }
        }
    }

    private fun getChannelMessages(
        onMessageReceived: OnMessagesReceived,
        pagingMessageListQueryResult: PagingMessageListQueryResult,
        query: PreviousMessageListQuery
    ): Flow<Iterable<Message>> {
        val channelHandlerId = UUID.randomUUID().toString()
        return callbackFlow {

            query.load(pagingMessageListQueryResult)

            val messageList = mutableListOf<Message>()
            pagingMessageListQueryResult.onMessagesGet = {
                messageList.addAll(0, it)
                offer(messageList)
            }

            onMessageReceived.onMessageReceived = { msg ->
                messageList.add(msg.toMessage())
                offer(messageList)
            }
            onMessageReceived.onMessageUpdated = { msg ->
                messageList.indexOfFirst {
                    it.id == msg.messageId.toString()
                }.also { index ->
                    if (index >= 0) {
                        messageList.removeAt(index)
                        messageList.add(index, msg.toMessage())
                        offer(messageList)
                    }
                }
            }
            SendBird.addChannelHandler(channelHandlerId, onMessageReceived)

            awaitClose { SendBird.removeChannelHandler(channelHandlerId) }
        }
    }


    override fun hasUnreadMessages(): Flow<Boolean> {
        val channelHandlerId = UUID.randomUUID().toString()
        return callbackFlow {
            val callBack = object : SendBird.ChannelHandler() {
                override fun onMessageReceived(p0: BaseChannel?, p1: BaseMessage?) {
                    if (p1 != null && p0 != null) {
                        var unReadCount = 0
                        val listQuery = GroupChannel.createMyGroupChannelListQuery()
                        listQuery.memberStateFilter = GroupChannelListQuery.MemberStateFilter.JOINED
                        listQuery.order = GroupChannelListQuery.Order.LATEST_LAST_MESSAGE

                        listQuery.next { list, ex ->
                            if (ex == null) {
                                unReadCount = list
                                    .filter { it.memberCount <= 2 }
                                    .map(sendBirdChannelToOnoToOneChannel::map)
                                    .count { it.isUnread }
                                offer(unReadCount > 0)
                            } else {
                                networkErrorReporter.logException(ex)
                            }
                        }
                    }
                }
            }

            SendBird.addChannelHandler(channelHandlerId, callBack)

            awaitClose { SendBird.removeChannelHandler(channelHandlerId) }
        }
    }

    override fun clear() {
        _state.value = emptyList()
    }

}