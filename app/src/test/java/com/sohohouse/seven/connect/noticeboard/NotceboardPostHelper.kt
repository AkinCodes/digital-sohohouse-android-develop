package com.sohohouse.seven.connect.noticeboard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.VenueTestHelper.mockVenue
import com.sohohouse.seven.network.core.models.Checkin
import com.sohohouse.seven.network.core.models.CheckinMeta
import com.sohohouse.seven.network.core.models.PostTags
import com.sohohouse.seven.network.core.models.Reaction
import com.sohohouse.seven.profile.ProfileTestHelper
import io.mockk.every
import io.mockk.mockk
import moe.banana.jsonapi2.HasMany
import org.junit.Rule
import java.util.*
import kotlin.collections.ArrayList

object NoticeboardTestHelper {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    const val TEST_POST_ID = "TEST_POST_ID"

    fun getMockPost(id: String = TEST_POST_ID, replyCount: Int = 0): Checkin {
        return mockk<Checkin>(relaxed = true).also {
            val replies = ArrayList<Checkin>()
            for (i in 0 until replyCount) {
                replies.add(getMockReply(i.toString()))
            }
            every { it.repliesResource } returns HasMany(*replies.toTypedArray())
            every { it.replies } returns replies
            every { it.id } returns id
            every { it.createdAt } returns Date()
            every { it.venueResource } returns HasMany(mockVenue())
            every { it.profile } returns ProfileTestHelper.fullMockProfile()
            every { it.status } returns "Test post"
            every { it.tags } returns PostTags()
            every { it.venues } returns listOf(mockVenue())
            every { it.reactions } returns CheckinMeta(
                mapOf(Reaction.THUMBS_UP to 3, Reaction.HEART to 12, Reaction.SPICY to 11)
            )
        }
    }

    fun getNoticeboardPost(id: String = TEST_POST_ID, replyCount: Int = 0): NoticeboardPost {
        return mockk<NoticeboardPost>(relaxed = true).also {
            every { it.replies } returns List(replyCount) { getNoticeboardPost() }
            every { it.postId } returns id
        }
    }

    fun getMockReply(id: String, message: String = "foo"): Checkin {
        return mockk<Checkin>(relaxed = true).also {
            every { it.repliesResource } returns HasMany()
            every { it.replies } returns emptyList()
            every { it.id } returns id
            every { it.createdAt } returns Date()
            every { it.venueResource } returns HasMany(mockVenue())
            every { it.profile } returns ProfileTestHelper.fullMockProfile()
            every { it.status } returns "Test reply"
            every { it.tags } returns PostTags()
            every { it.venues } returns listOf(mockVenue())
        }
    }

}