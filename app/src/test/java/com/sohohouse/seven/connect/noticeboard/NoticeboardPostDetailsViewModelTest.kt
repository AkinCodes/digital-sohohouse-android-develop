package com.sohohouse.seven.connect.noticeboard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.EmptyStringProvider
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.connect.filter.FilterManager
import com.sohohouse.seven.connect.noticeboard.NoticeboardTestHelper.getMockPost
import com.sohohouse.seven.connect.noticeboard.NoticeboardTestHelper.getNoticeboardPost
import com.sohohouse.seven.connect.noticeboard.post_details.NoticeboardPostDetailsViewModel
import com.sohohouse.seven.connect.noticeboard.post_details.adapter.NoticeboardPostReply
import com.sohohouse.seven.connect.noticeboard.reactions.NoticeboardReactionIconsProvider
import com.sohohouse.seven.network.base.model.empty
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.SohoApiService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout

@ExperimentalCoroutinesApi
class NoticeboardPostDetailsViewModelTest {

    companion object {
        private const val TEST_REPLY_ID = "12345"
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val globalTimeout: Timeout = Timeout.seconds(20)

    @MockK(relaxed = true)
    lateinit var analyticsManager: AnalyticsManager

    @MockK(relaxed = true)
    lateinit var repository: NoticeboardRepository

    @MockK
    lateinit var userManager: UserManager

    @MockK(relaxed = true)
    lateinit var venueRepo: VenueRepo

    @MockK
    lateinit var houseManager: HouseManager

    @MockK
    lateinit var filterManager: FilterManager

    @MockK(relaxed = true)
    lateinit var iconProvider: NoticeboardReactionIconsProvider

    @MockK(relaxed = true)
    lateinit var sohoApiService: SohoApiService

    private val noticeboardItemFactory: NoticeboardItemFactory by lazy {
        NoticeboardItemFactory(EmptyStringProvider(), userManager, venueRepo, iconProvider)
    }

    private lateinit var viewModel: NoticeboardPostDetailsViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { userManager.profileID } returns "testProfileID"
        viewModel = NoticeboardPostDetailsViewModel(
            analyticsManager = analyticsManager,
            repo = repository,
            ioDispatcher = Dispatchers.Unconfined,
            filterManager = filterManager,
            sohoApiService = sohoApiService,
            userManager = userManager,
        )
    }

    @Test
    fun `viewmodel emits original post then fetches post replies then emits them`() {
        val post = getNoticeboardPost(replyCount = 1)

        every { repository.getCachedPost(any()) } returns post
        coEvery { repository.getPost(any()) } returns ApiResponse.Success(post)

        val observer = mockk<Observer<List<DiffItem>>>(relaxed = true)

        viewModel.items.observeForever(observer)

        val itemsSlot = CapturingSlot<List<DiffItem>>()
        val items2Slot = CapturingSlot<List<DiffItem>>()
        val items3Slot = CapturingSlot<List<DiffItem>>()

        viewModel.init(post.postId)
        Thread.sleep(500)
        coVerifySequence {
            observer.onChanged(capture(itemsSlot))
            repository.getCachedPost(any())
            observer.onChanged(capture(items2Slot))
            repository.getPost(any())
            observer.onChanged(capture(items3Slot))
        }

        val items = itemsSlot.captured
        val items2 = items2Slot.captured
        val items3 = items3Slot.captured

        assertEquals(1, items.size)
        assertTrue(items[0] is LoadingItem)

        assertEquals(2, items2.size)
        assertTrue(items2[0] is NoticeboardPost)
        assertTrue(items2[1] is LoadingItem)

        assertEquals(2, items3.size)
        assertTrue(items3[0] is NoticeboardPost)
        assertTrue(items3[1] is NoticeboardPostReply)
    }

    @Test
    fun `viewmodel emits reply item when user posts reply message`() {
        var post = getNoticeboardPost(replyCount = 1)
        val replyMessage = "Test reply"
        every { repository.getCachedPost(any()) } returns post
        coEvery { repository.getPost(any()) } returns ApiResponse.Success(post)
        every {
            repository.createPost(any(), any(), any())
        } returns value(NoticeboardTestHelper.getMockReply(TEST_REPLY_ID, replyMessage))

        val observer = mockk<Observer<List<DiffItem>>>(relaxed = true)
        viewModel.items.observeForever(observer)

        val replyLoadingObserver = mockk<Observer<LoadingState>>(relaxed = true)
        viewModel.replyLoadingState.observeForever(replyLoadingObserver)

        viewModel.init(post.postId)
        Thread.sleep(500)

        post = getNoticeboardPost(replyCount = 2)
        coEvery { repository.getPost(any()) } returns ApiResponse.Success(post)
        viewModel.onReplySubmitted(replyMessage)

        val itemsSlot = CapturingSlot<List<DiffItem>>()

        coVerifySequence {
            observer.onChanged(capture(itemsSlot))
            replyLoadingObserver.onChanged(LoadingState.Idle)
            repository.getCachedPost(any())
            observer.onChanged(capture(itemsSlot))
            repository.getPost(any())
            observer.onChanged(capture(itemsSlot))
            replyLoadingObserver.onChanged(LoadingState.Loading)
            repository.createPost(any(), any(), any())
            repository.getPost(any())
            observer.onChanged(capture(itemsSlot))
            replyLoadingObserver.onChanged(LoadingState.Idle)
        }

        val items = itemsSlot.captured

        assertEquals(3, items.size)
        assertTrue(items[2] is NoticeboardPostReply)
    }

    @Test
    fun `viewmodel deletes OP when user deletes OP`() {
        val opId = "OP_ID"

        val checkin = getNoticeboardPost(id = opId)
        val post = mockk<NoticeboardPost>(relaxed = true).also {
            every { it.postId } returns opId
        }

        every { repository.getCachedPost(any()) } returns checkin
        coEvery { repository.getPost(any()) } returns ApiResponse.Success(checkin)
        every { repository.deletePost(any()) } returns empty()

        val opDeletedObserver = mockk<Observer<Any>>(relaxed = true)
        viewModel.originalPostDeletedEvent.observeForever(opDeletedObserver)

        val deleteIdSlot = CapturingSlot<String>()

        viewModel.init(opId)
        viewModel.deleteReply(post.postId)

        verify { opDeletedObserver.onChanged(any()) }
        verify { repository.deletePost(capture(deleteIdSlot)) }
        assertEquals(opId, deleteIdSlot.captured)
    }

    @Test
    fun `viewmodel deletes reply when user deletes reply`() {
        val opId = "OP_ID"

        val checkin = getNoticeboardPost(id = opId, replyCount = 1)

        val reply = mockk<NoticeboardPost>(relaxed = true).also {
            every { it.postId } returns checkin.replies.first().postId
        }

        every { repository.getCachedPost(any()) } returns checkin
        coEvery { repository.getPost(any()) } returns ApiResponse.Success(checkin)
        every { repository.deletePost(any()) } returns empty()

        val opDeletedObserver = mockk<Observer<Any>>(relaxed = true)
        viewModel.originalPostDeletedEvent.observeForever(opDeletedObserver)

        val itemsObserver = mockk<Observer<List<DiffItem>>>(relaxed = true)
        viewModel.items.observeForever(itemsObserver)

        viewModel.init(opId)
        viewModel.deleteReply(reply.postId)

        val itemsSlot = ArrayList<List<DiffItem>>()
        val deleteIdSlot = CapturingSlot<String>()

        verify(exactly = 0) { opDeletedObserver.onChanged(any()) }
        verify { itemsObserver.onChanged(capture(itemsSlot)) }
        verify { repository.deletePost(capture(deleteIdSlot)) }
        assertEquals(reply.postId, deleteIdSlot.captured)
        assertEquals(1, itemsSlot.last().size) //list size should be 1 after reply is deleted
    }

}