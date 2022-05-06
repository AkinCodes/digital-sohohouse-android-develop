package com.sohohouse.seven.network.chat.model.message

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

sealed interface Message {

    val id: String
    val senderId: String
    val sentDate: MessageTime
    val senderImageUrl: String

    data class Text(
        override val id: String,
        override val senderId: String,
        override val sentDate: MessageTime,
        override val senderImageUrl: String,
        val message: String,
    ) : Message

    data class Image(
        override val id: String,
        override val senderId: String,
        override val sentDate: MessageTime,
        override val senderImageUrl: String,
        val messageImageUrl: String,
        val thumbnail: String?,
    ) : Message

    data class Video(
        override val id: String,
        override val senderId: String,
        override val sentDate: MessageTime,
        override val senderImageUrl: String,
        val messageVideoUrl: String,
        val thumbnail: String?,
    ) : Message

    data class Link(
        override val id: String,
        override val senderId: String,
        override val sentDate: MessageTime,
        override val senderImageUrl: String,
        val title: String,
        val url: String,
        val description: String,
        val linkImageUrl: String,
    ) : Message {
        private val sohoUrlPart = "sohohouse.com"
        private val removablePrefixes = listOf(
            "https://www.",
            "http://www.",
            "https://",
            "http://"
        )

        fun getVisibleLink(): String {
            removablePrefixes.forEach {
                if (url.indexOf(it, 0, true) == 0)
                    return url.substring(it.length)
            }
            return url
        }

        val isSohoHouseLink get() = url.contains(sohoUrlPart, true)
    }

}


@JvmInline
value class MessageTime(
    private val value: DateTime,
) {
    fun shouldGroup(messageTime: MessageTime): Boolean {
        val intervalInSecs = 30
        return value.plusSeconds(intervalInSecs).isAfter(messageTime.value)
    }

    fun isSameDay(dateTime: MessageTime): Boolean {
        val m = value.monthOfYear
        val y = value.year
        val d = value.dayOfMonth
        return m == dateTime.value.monthOfYear
                && y == dateTime.value.year
                && d == dateTime.value.dayOfMonth
    }

    override fun toString(): String {
        val pattern = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
        return pattern.print(value)
    }
}

enum class MessageType {
    USER_MESSAGE,
    IMAGE_MESSAGE,
    VIDEO_MESSAGE,
    FILE_MESSAGE
}

object StringSet {
    const val IMAGE = "image"
    const val VIDEO = "video"
    const val JPG = "jpg"
    const val JPEG = "jpeg"
    const val SVG = "svg"
    const val MP4 = "mp4"
}