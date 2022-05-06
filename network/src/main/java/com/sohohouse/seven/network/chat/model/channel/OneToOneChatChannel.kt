package com.sohohouse.seven.network.chat.model.channel

import com.sohohouse.seven.network.chat.model.ChatMember
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

data class OneToOneChatChannel(
    val id: String,
    val isUnread: Boolean,
    val lastMessage: String,
    val lastMessageTime: LastMessageTime,
    val isMuted: Boolean,
    val channelUrl: String,
    val unreadCount: Int,
    val members: List<ChatMember>,
    val invitedAt: Long,
    val inviteUserWithId: suspend (String) -> Unit,
) {

    fun memberIsOnline(myProfileId: String) =
        members.find { it.id != myProfileId }?.isActive ?: false

    fun memberProfileUrl(myProfileId: String) =
        members.find { it.id != myProfileId }?.profileUrl ?: ""

    fun memberName(myProfileId: String) = members.find { it.id != myProfileId }?.name ?: ""
    fun memberId(myProfileId: String) = members.find { it.id != myProfileId }?.id ?: ""
    fun isMemberStaff(myProfileId: String) = members.find { it.id != myProfileId }?.isStaff ?: false

    @JvmInline
    value class LastMessageTime(
        private val value: DateTime?,
    ) {

        override fun toString(): String {
            if (value == null) return ""
            val pattern = if (value.toLocalDate() == LocalDate()) {
                DateTimeFormat.forPattern("HH:mm")
            } else {
                DateTimeFormat.forPattern("dd/MM/yyyy")
            }

            return pattern.print(value)
        }
    }

}