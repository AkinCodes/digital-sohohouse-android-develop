package com.sohohouse.sendbird

import com.sendbird.android.BaseMessage
import com.sendbird.android.FileMessage
import com.sohohouse.seven.network.chat.model.message.Message
import com.sohohouse.seven.network.chat.model.message.MessageTime
import com.sohohouse.seven.network.chat.model.message.MessageType
import com.sohohouse.seven.network.chat.model.message.StringSet
import org.joda.time.DateTime


fun BaseMessage.toMessage(): Message {
    return when (getMessageType()) {
        MessageType.USER_MESSAGE -> if (ogMetaData != null && ogMetaData.url.contains(message.trim())) {
            Message.Link(
                id = messageId.toString(),
                senderId = sender.userId,
                sentDate = MessageTime(DateTime(createdAt)),
                senderImageUrl = sender.profileUrl,
                title = ogMetaData.title ?: "",
                url = ogMetaData.url ?: "",
                description = ogMetaData.description ?: "",
                linkImageUrl = ogMetaData.ogImage?.url ?: ""
            )
        } else {
            Message.Text(
                id = messageId.toString(),
                message = message,
                senderId = sender.userId,
                sentDate = MessageTime(DateTime(createdAt)),
                senderImageUrl = sender.profileUrl,
            )
        }
        MessageType.IMAGE_MESSAGE -> with(this as FileMessage) {
            Message.Image(
                id = messageId.toString(),
                senderId = sender.userId,
                sentDate = MessageTime(DateTime(createdAt)),
                senderImageUrl = sender.profileUrl,
                messageImageUrl = url,
                thumbnail = if (thumbnails.isEmpty()) null else thumbnails.last().url
            )
        }
        MessageType.VIDEO_MESSAGE -> with(this as FileMessage) {
            Message.Video(
                id = messageId.toString(),
                senderId = sender.userId,
                sentDate = MessageTime(DateTime(createdAt)),
                senderImageUrl = sender.profileUrl,
                messageVideoUrl = url,
                thumbnail = if (thumbnails.isEmpty()) null else thumbnails.last().url
            )
        }
        MessageType.FILE_MESSAGE -> Message.Text(
            id = messageId.toString(),
            message = message,
            senderId = sender.userId,
            sentDate = MessageTime(DateTime(createdAt)),
            senderImageUrl = sender.profileUrl,
        )
    }
}

fun BaseMessage.getMessageType(): MessageType {
    return when (this) {
        is FileMessage -> {
            val mimeType = type.lowercase()
            if (mimeType.startsWith(StringSet.IMAGE)) {
                if (mimeType.contains(StringSet.SVG))
                    MessageType.FILE_MESSAGE
                else
                    MessageType.IMAGE_MESSAGE
            } else if (mimeType.startsWith(StringSet.VIDEO)) {
                MessageType.VIDEO_MESSAGE
            } else
                MessageType.FILE_MESSAGE
        }
        else -> MessageType.USER_MESSAGE
    }
}