package com.sohohouse.seven.connect.noticeboard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.relaxedMockk
import com.sohohouse.seven.common.user.MembershipType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.EmptyStringProvider
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.filter.base.FilterType
import com.sohohouse.seven.connect.filter.topic.Topic
import com.sohohouse.seven.connect.noticeboard.create_post.NoticeboardCreatePostViewModel
import com.sohohouse.seven.network.base.model.value
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
class NoticeboardCreatePostViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @MockK
    lateinit var repo: NoticeboardRepository

    @MockK(relaxed = true)
    lateinit var analyticsManager: AnalyticsManager

    @MockK(relaxed = true)
    lateinit var userManager: UserManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `on post successfully created, finish event is emitted`() {
        every { userManager.membershipType } returns MembershipType.REGULAR.name
        coEvery { repo.createPost(any(), any(), any(), any(), any()) } returns value(
            NoticeboardTestHelper.getMockPost(replyCount = 0)
        )

        val viewmodel = NoticeboardCreatePostViewModel(
            analyticsManager,
            repo,
            userManager,
            EmptyStringProvider(),
            testCoroutineRule.testCoroutineDispatcher
        )

        val observer = mockk<Observer<Any>>(relaxed = true)
        viewmodel.postCreatedEvent.observeForever(observer)

        viewmodel.onPostSubmit("test post")

        verify { repo.createPost(any(), any(), any(), any(), any()) }
        verify { observer.onChanged(any()) }
    }

    @Test
    fun `U27 tag is applied by default for U27 member`() {
        every { userManager.membershipType } returns MembershipType.U27.name

        val viewmodel = NoticeboardCreatePostViewModel(
            analyticsManager,
            repo,
            userManager,
            EmptyStringProvider(),
            testCoroutineRule.testCoroutineDispatcher
        )

        val observer = relaxedMockk<Observer<EnumMap<FilterType, Filter?>>>()
        viewmodel.tags.observeForever(observer)

        val capturingSlot = CapturingSlot<EnumMap<FilterType, Filter?>>()
        verify { observer.onChanged(capture(capturingSlot)) }
        val tags = capturingSlot.captured
        Assert.assertEquals(Topic.U27.id, tags[FilterType.TOPIC_FILTER]?.id)
    }

    @Test
    fun `U27 tag is not applied by default for non-U27 member`() {
        every { userManager.membershipType } returns MembershipType.REGULAR.name

        val viewmodel = NoticeboardCreatePostViewModel(
            analyticsManager,
            repo,
            userManager,
            EmptyStringProvider(),
            testCoroutineRule.testCoroutineDispatcher
        )

        val observer = relaxedMockk<Observer<EnumMap<FilterType, Filter?>>>()
        viewmodel.tags.observeForever(observer)

        val capturingSlot = CapturingSlot<EnumMap<FilterType, Filter?>>()
        verify(exactly = 0) { observer.onChanged(capture(capturingSlot)) }
    }
}