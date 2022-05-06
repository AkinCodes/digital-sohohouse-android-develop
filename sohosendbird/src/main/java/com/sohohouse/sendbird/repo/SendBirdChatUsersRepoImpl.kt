package com.sohohouse.sendbird.repo

import com.sendbird.android.GroupChannel
import com.sendbird.android.SendBird
import com.sohohouse.seven.network.chat.ChatUsersRepo
import com.sohohouse.seven.network.chat.model.ChannelOperationErrorType
import com.sohohouse.seven.network.chat.model.ChannelOperationException
import com.sohohouse.seven.network.chat.model.ChatMember
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SendBirdChatUsersRepoImpl : ChatUsersRepo {

    override suspend fun users(): Iterable<ChatMember> = suspendCoroutine { cont ->
        val query = SendBird.createApplicationUserListQuery()

        query.next { list, ex ->
            if (ex != null) {
                cont.resumeWithException(ex)
            } else {
                cont.resume(list.map {
                    ChatMember(
                        id = it.userId,
                        name = it.nickname,
                        profileUrl = it.profileUrl,
                        isActive = it.isActive,
                        isInvited = false,
                        isStaff = it.metaData["staff"].toBoolean()
                    )
                })
            }
        }
    }

    override suspend fun accept(channelUrl: String) {
        return suspendCoroutine { cont ->
            GroupChannel.getChannel(channelUrl) { channel, ex ->
                if (ex == null)
                    channel.acceptInvitation {
                        if (it == null) cont.resume(Unit)
                        else cont.resumeWithException(
                            ChannelOperationException(
                                ChannelOperationErrorType.AcceptInvite,
                                it,
                                it.code
                            )
                        )
                    }
                else
                    cont.resumeWithException(
                        ChannelOperationException(
                            ChannelOperationErrorType.AcceptInvite,
                            ex,
                            ex.code
                        )
                    )
            }
        }
    }

    override suspend fun decline(channelUrl: String) {
        return suspendCoroutine { cont ->
            GroupChannel.getChannel(channelUrl) { channel, ex ->
                if (ex == null)
                    channel.declineInvitation {
                        if (it == null) cont.resume(Unit)
                        else cont.resumeWithException(
                            ChannelOperationException(
                                ChannelOperationErrorType.DeclineInvite,
                                it,
                                it.code
                            )
                        )
                    }
                else
                    cont.resumeWithException(
                        ChannelOperationException(
                            ChannelOperationErrorType.DeclineInvite,
                            ex,
                            ex.code
                        )
                    )
            }
        }
    }
}