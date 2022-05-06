package com.sohohouse.seven.network.chat

import com.sohohouse.seven.network.chat.model.ChannelOperationException
import com.sohohouse.seven.network.chat.model.CreateChatResult
import com.sohohouse.seven.network.chat.model.CreateOneToOneChannel
import com.sohohouse.seven.network.chat.model.channel.OneToOneChannelDetails
import com.sohohouse.seven.network.chat.model.channel.OneToOneChatChannel
import kotlinx.coroutines.flow.Flow

/**
 * Without calling [ChatConnectionRepo.connect] first
 * Methods defined inside of this interface will crash.
 */
interface ChatChannelsRepo {

    fun channels(): Flow<Iterable<OneToOneChatChannel>>
    suspend fun channel(requestChannel: RequestChannel): OneToOneChannelDetails

    fun hasUnreadMessages(): Flow<Boolean>

    fun clear()

    /** if operation did not complete
     * @throws ChannelOperationException */
    suspend fun mute(oneToOneChatChannel: OneToOneChatChannel)

    /** if operation did not complete
     * @throws ChannelOperationException */
    suspend fun unMute(oneToOneChatChannel: OneToOneChatChannel)

    /** if operation did not complete
     * @throws ChannelOperationException */
    suspend fun delete(oneToOneChatChannel: OneToOneChatChannel)

    /** if operation did not complete
     * @throws ChannelOperationException */
    suspend fun create(createOneToOneChannel: CreateOneToOneChannel): CreateChatResult

    suspend fun createNewDMChannel(createOneToOneChannel: CreateOneToOneChannel): CreateChatResult
}

/** ChannelName equals to MyProfileId_MemberProfileId */
typealias ChannelId = String

sealed interface RequestChannel {

    @JvmInline
    value class WithChannelId(
        val channelId: String,
    ) : RequestChannel

    @JvmInline
    value class WithChannelUrl(
        val channelUrl: String,
    ) : RequestChannel

    companion object {
        operator fun invoke(
            channelId: ChannelId,
            channelUrl: String,
        ): RequestChannel {
            return if (channelUrl.isNotEmpty())
                WithChannelUrl(channelUrl)
            else
                WithChannelId(channelId)
        }
    }

}