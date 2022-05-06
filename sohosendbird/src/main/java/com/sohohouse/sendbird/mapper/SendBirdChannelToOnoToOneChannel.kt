package com.sohohouse.sendbird.mapper

import com.sendbird.android.GroupChannel
import com.sendbird.android.Member
import com.sohohouse.seven.network.chat.model.ChannelOperationErrorType
import com.sohohouse.seven.network.chat.model.ChannelOperationException
import com.sohohouse.seven.network.chat.model.ChatMember
import com.sohohouse.seven.network.chat.model.channel.OneToOneChatChannel
import org.joda.time.DateTime
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal interface SendBirdChannelToOnoToOneChannel {

    fun map(channel: GroupChannel): OneToOneChatChannel

    class Impl : SendBirdChannelToOnoToOneChannel {
        override fun map(channel: GroupChannel): OneToOneChatChannel {
            val hasMessage = channel.lastMessage != null
            return OneToOneChatChannel(
                id = channel.name,
                isUnread = channel.unreadMessageCount > 0,
                lastMessage = channel.lastMessage?.message ?: "",
                lastMessageTime = OneToOneChatChannel.LastMessageTime(
                    if (hasMessage) DateTime(channel.lastMessage.createdAt) else null
                ),
                channelUrl = channel.url,
                isMuted = isMuted(channel),
                unreadCount = channel.unreadMessageCount,
                inviteUserWithId = {
                    suspendCoroutine<Unit> { cont ->
                        channel.inviteWithUserId(it) { ex ->
                            if (ex != null) {
                                cont.resumeWithException(
                                    ChannelOperationException(
                                        ChannelOperationErrorType.Invite,
                                        ex,
                                        ex.code
                                    )
                                )
                            } else {
                                cont.resume(Unit)
                            }
                        }
                    }
                },
                members = channel.members.map {
                    val fullName = StringBuilder()
                    fullName.append(it.nickname)
                    val lastName = it.metaData["last_name"]
                    if (lastName?.isNotEmpty() == true) {
                        fullName.append(" ")
                        fullName.append(lastName)
                    }
                    ChatMember(
                        id = it.userId,
                        name = fullName.toString(),
                        profileUrl = it.profileUrl,
                        isActive = it.isActive,
                        isInvited = it.memberState == Member.MemberState.INVITED,
                        isStaff = it.metaData["staff"].toBoolean()
                    )
                },
                invitedAt = channel.invitedAt
            )
        }

        private fun isMuted(channel: GroupChannel): Boolean {
            return channel.myPushTriggerOption == GroupChannel.PushTriggerOption.OFF
        }

    }


}