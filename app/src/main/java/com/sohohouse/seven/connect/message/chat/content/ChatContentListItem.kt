package com.sohohouse.seven.connect.message.chat.content

import com.sohohouse.seven.network.chat.model.message.Message


sealed interface ChatContentListItem {

    val id: String

    data class MessageGroup(
        val messageContents: List<Message>,
        val isSentByMe: Boolean,
        val sentDate: String,
        val imageUrl: String,
        var onMediaClick: (urlPair: Pair<String?, String?>) -> Unit,
        val onProfileIconClick: () -> Unit
    ) : ChatContentListItem {

        override val id: String = messageContents.joinToString("_") { it.id }
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MessageGroup

            if (messageContents != other.messageContents) return false
            if (isSentByMe != other.isSentByMe) return false
            if (sentDate != other.sentDate) return false
            if (imageUrl != other.imageUrl) return false
            if (id != other.id) return false

            return true
        }

        override fun hashCode(): Int {
            var result = messageContents.hashCode()
            result = 31 * result + isSentByMe.hashCode()
            result = 31 * result + sentDate.hashCode()
            result = 31 * result + imageUrl.hashCode()
            result = 31 * result + id.hashCode()
            return result
        }

    }

    @JvmInline
    value class DateIndicator(
        val sentDate: String
    ) : ChatContentListItem {
        override val id: String get() = sentDate
    }

    companion object {

        operator fun invoke(
            messageContents: List<Message>,
            myProfileId: String,
            profileIconCallback: () -> Unit,
            onMediaClick: (urlPair: Pair<String?, String?>) -> Unit
        ): List<ChatContentListItem> {
            if (messageContents.isEmpty()) return emptyList()
            val result = mutableListOf<ChatContentListItem>()

            val temp = mutableListOf<Message>()
            messageContents.forEachIndexed { i, message ->
                val shouldGroup = { temp.last().sentDate.shouldGroup(message.sentDate) }
                val hasSameSender = { temp.last().senderId == message.senderId }
                val isVideoGroup = {
                    temp.last() is Message.Video || message is Message.Video
                }
                if (temp.isEmpty() || (isVideoGroup().not() && (hasSameSender() && shouldGroup()))) {
                    temp.add(message)
                } else {
                    result.add(
                        MessageGroup(
                            messageContents = temp.toList(),
                            isSentByMe = temp.last().senderId == myProfileId,
                            sentDate = messageContents[i - 1].sentDate.toString(),
                            imageUrl = messageContents[i - 1].senderImageUrl,
                            onProfileIconClick = profileIconCallback,
                            onMediaClick = onMediaClick
                        )
                    )

                    if (temp.last().sentDate.isSameDay(message.sentDate).not()) {
                        temp.clear()
                        result.add(DateIndicator(message.sentDate.toString()))
                    } else {
                        temp.clear()
                    }

                    temp.add(message)
                }
            }

            if (temp.isNotEmpty())
                result.add(
                    MessageGroup(
                        messageContents = temp,
                        isSentByMe = temp.last().senderId == myProfileId,
                        sentDate = messageContents.last().sentDate.toString(),
                        imageUrl = messageContents.last().senderImageUrl,
                        onProfileIconClick = profileIconCallback,
                        onMediaClick = onMediaClick
                    )
                )

            return result
        }

    }


}