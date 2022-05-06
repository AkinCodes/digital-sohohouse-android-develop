package com.sohohouse.sendbird

import com.sendbird.android.FileMessage
import com.sendbird.android.OGMetaData
import com.sendbird.android.UserMessage
import com.sohohouse.seven.network.chat.model.message.Message
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

class MessageTypeTest {

    @MockK(relaxed = true)
    lateinit var sbFileMessage: FileMessage

    @MockK(relaxed = true)
    lateinit var sbUserMessage: UserMessage

    @MockK(relaxed = true)
    lateinit var ogMetaData: OGMetaData

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `sendbird msg type cast to our msg type`() {
        every { sbFileMessage.type }.returns("otherType")
        assert(sbFileMessage.toMessage() is Message.Text)

        every { sbFileMessage.type }.returns("imageType")
        assert(sbFileMessage.toMessage() is Message.Image)

        every { sbFileMessage.type }.returns("videoType")
        assert(sbFileMessage.toMessage() is Message.Video)

        every { ogMetaData.url }.returns("my_url")
        every { sbUserMessage.message }.returns("my_url")
        every { sbUserMessage.ogMetaData }.returns(ogMetaData)
        assert(sbUserMessage.toMessage() is Message.Link)
    }

}