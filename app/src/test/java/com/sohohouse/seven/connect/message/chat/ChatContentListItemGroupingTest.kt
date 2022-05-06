package com.sohohouse.seven.connect.message.chat

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.connect.message.chat.content.ChatContentListItem
import com.sohohouse.seven.network.chat.model.message.Message
import com.sohohouse.seven.network.chat.model.message.MessageTime
import org.joda.time.DateTime
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

@Suppress("TestFunctionName")
class ChatContentListItemGroupingTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private data class TestInput(
        val input: List<Message>,
        val expected: List<ChatContentListItem>,
        val message: String
    )

    private val myProfileId = "0"
    private val memberId = "1"

    @Test
    fun testEmptyState() {
        val input = TestInput(
            input = emptyList(),
            expected = emptyList(),
            message = "Message list is empty, should return empty"
        )

        assertTestInput(input)
    }

    @Test
    fun testTwoDividedItem() {
        val input = TestInput(
            input = listOf(
                SentMessageByMember(DateTime(0)),
                SentMessageByMember(DateTime(5)),
                SentMessageByMember(DateTime(10)),
                SentMessageByMe(DateTime(15)),
                SentMessageByMe(DateTime(20)),
            ),
            expected = listOf(
                ChatContentSentByMember(
                    listOf(DateTime(0), DateTime(5), DateTime(10)),
                    "test",
                    "test",
                    "test"
                ),
                ChatContentSentByMe(listOf(DateTime(15), DateTime(20)), "test", "test"),
            ),
            message = """
                             Member message -> Member message -> Member message
                             My message -> My message
                             should return two message item
                        """.trimIndent()
        )

        assertTestInput(input)
    }

    @Test
    fun testFiveMessagesGroupedInTwo() {
        val input = TestInput(
            input = listOf(
                SentMessageByMember(DateTime(0)),
                SentMessageByMember(DateTime(5)),
                SentMessageByMember(DateTime(10)),
                SentMessageByMe(DateTime(15)),
                SentMessageByMe(DateTime(20)),
            ),
            expected = listOf(
                ChatContentSentByMember(
                    listOf(DateTime(0), DateTime(5), DateTime(10)),
                    "test",
                    "test",
                    "test"
                ),
                ChatContentSentByMe(listOf(DateTime(15), DateTime(20)), "test", "test"),
            ),
            message = """
                             Member message -> Member message -> Member message
                             My message -> My message
                             should return two message item
                        """.trimIndent()
        )

        assertTestInput(input)
    }

    @Test
    fun testFiveMessagesGroupedInThreeBecauseOfTimingDifference() {
        val input = TestInput(
            input = listOf(
                SentMessageByMember(DateTime(0)),
                SentMessageByMember(DateTime(FIFTEEN_SECONDS)),
                SentMessageByMember(DateTime(MINUTE)),
                SentMessageByMe(DateTime(MINUTE + FIFTEEN_SECONDS)),
                SentMessageByMe(DateTime(MINUTE + THIRTY_SECONDS)),
            ),
            expected = listOf(
                ChatContentSentByMember(
                    listOf(DateTime(0), DateTime(FIFTEEN_SECONDS)),
                    "test",
                    "test"
                ),
                ChatContentSentByMember(listOf(DateTime(MINUTE))),
                ChatContentSentByMe(
                    listOf(
                        DateTime(MINUTE + FIFTEEN_SECONDS),
                        DateTime(MINUTE + THIRTY_SECONDS)
                    ), "test", "test"
                ),
            ),
            message = """
                             Member message -> Member message after 15 secs
                             Member message after 31 secs
                             My message -> My message after a minute
                             should return three message item
                        """.trimIndent()
        )

        assertTestInput(input)
    }

    @Test
    fun testTwoMessagesDividedByDurationOfDayBetweenThem() {
        val input = TestInput(
            input = listOf(
                SentMessageByMember(DateTime(0)),
                SentMessageByMember(DateTime(TimeUnit.DAYS.toMillis(1L)))
            ),
            expected = listOf(
                ChatContentSentByMember(listOf(DateTime(0))),
                ChatContentNewDateIndicator(DateTime(TimeUnit.DAYS.toMillis(1L))),
                ChatContentSentByMember(listOf(DateTime(TimeUnit.DAYS.toMillis(1L)))),
            ),
            message = """
                    Member message -> after 0 secs
                    Date indicator -> indicates that one day has passed between messages
                    Member message -> after 1 day
                """.trimIndent()
        )

        assertTestInput(input)
    }

    @Test
    fun `video message are single in message group`() {
        val inputMessages = listOf(
            SentVideoMessageByMe(DateTime(0)),
            SentVideoMessageByMe(DateTime(1)),
            SentMessageByMember(DateTime(5)),
            SentVideoMessageByMe(DateTime(10)),
            SentMessageByMember(DateTime(15)),
        )

        val actual = ChatContentListItem(inputMessages, myProfileId, {}, {})
        actual.forEach { content ->
            if (content is ChatContentListItem.MessageGroup) {
                val containsVideo = content.messageContents.any { msg ->
                    msg is Message.Video
                }
                if (containsVideo)
                    Assert.assertTrue(
                        "video message inside group of ${content.messageContents.size} messages",
                        content.messageContents.size == 1
                    )
            }
        }
    }

    private fun ChatContentNewDateIndicator(dateTime: DateTime): ChatContentListItem.DateIndicator {
        return ChatContentListItem.DateIndicator(MessageTime(dateTime).toString())
    }

    private fun assertTestInput(it: TestInput) {
        val actual = ChatContentListItem(it.input, myProfileId, {}, {})

        Assert.assertEquals(
            "List sizes are not same expected:${it.expected.size}, got:${actual.size}",
            it.expected.size,
            actual.size
        )

        Assert.assertEquals(
            it.message,
            it.expected,
            actual
        )
    }

    private fun SentMessageByMember(sentDate: DateTime) = Message.Text(
        id = "test_$memberId",
        message = "test",
        senderId = memberId,
        sentDate = MessageTime(sentDate),
        senderImageUrl = ""
    )

    private fun SentMessageByMe(sentDate: DateTime) = Message.Text(
        id = "test_$myProfileId",
        message = "test",
        senderId = myProfileId,
        sentDate = MessageTime(sentDate),
        senderImageUrl = ""
    )

    private fun SentVideoMessageByMe(sentDate: DateTime) = Message.Video(
        id = "test_$myProfileId",
        senderId = myProfileId,
        sentDate = MessageTime(sentDate),
        senderImageUrl = "",
        messageVideoUrl = "",
        thumbnail = null
    )

    private fun ChatContentSentByMember(
        sentDate: List<DateTime>,
        vararg messages: String = arrayOf("test")
    ) = ChatContentListItem.MessageGroup(
        messageContents = messages.mapIndexed { i: Int, s: String ->
            Message.Text(
                senderId = memberId,
                message = s,
                sentDate = MessageTime(sentDate[i]),
                id = "${s}_$memberId",
                senderImageUrl = ""
            )
        },
        isSentByMe = false,
        sentDate = MessageTime(sentDate.last()).toString(),
        imageUrl = "",
        onMediaClick = {},
        onProfileIconClick = {}
    )

    private fun ChatContentSentByMe(
        sentDate: List<DateTime>,
        vararg messages: String = arrayOf("test")
    ) = ChatContentListItem.MessageGroup(
        messageContents = messages.mapIndexed { i: Int, s: String ->
            Message.Text(
                senderId = myProfileId,
                message = s,
                sentDate = MessageTime(sentDate[i]),
                id = "${s}_$myProfileId",
                senderImageUrl = ""
            )
        },
        isSentByMe = true,
        sentDate = MessageTime(sentDate.last()).toString(),
        imageUrl = "",
        onMediaClick = {},
        onProfileIconClick = {}
    )

    companion object {
        private const val SECOND = 1000L
        private const val FIFTEEN_SECONDS = 15L * SECOND
        private const val THIRTY_SECONDS = 2L * FIFTEEN_SECONDS
        private const val MINUTE = THIRTY_SECONDS * 2L
    }

}