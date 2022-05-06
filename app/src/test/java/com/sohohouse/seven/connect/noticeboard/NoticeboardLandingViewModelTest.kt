package com.sohohouse.seven.connect.noticeboard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.google.firebase.crashlytics.internal.network.HttpResponse
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.EmptyStringProvider
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.connect.filter.FilterManager
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.filter.base.FilterType
import com.sohohouse.seven.connect.filter.topic.Topic
import com.sohohouse.seven.connect.mynetwork.blockedprofiles.getOrAwaitValue
import com.sohohouse.seven.connect.noticeboard.NoticeboardTestHelper.getNoticeboardPost
import com.sohohouse.seven.connect.noticeboard.reactions.NoticeboardReactionIconsProvider
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.empty
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.models.Checkin
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import java.net.HttpURLConnection

@ExperimentalCoroutinesApi
class NoticeboardLandingViewModelTest {

    @MockK(relaxed = true)
    lateinit var analyticsManager: AnalyticsManager

    @MockK
    lateinit var userManager: UserManager

    @MockK(relaxed = true)
    lateinit var venueRepo: VenueRepo

    @MockK(relaxed = true)
    lateinit var filterManager: FilterManager

    @MockK(relaxed = true)
    lateinit var repository: NoticeboardRepository

    @MockK(relaxed = true)
    lateinit var dataSourceFactory: NoticeboardDataSourceFactory

    @MockK(relaxed = true)
    lateinit var iconProvider: NoticeboardReactionIconsProvider

    @MockK(relaxed = true)
    lateinit var stringProvider: StringProvider

    @MockK
    lateinit var sohoRepo: SohoApiService

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    private val noticeboardItemFactory: NoticeboardItemFactory by lazy {
        NoticeboardItemFactory(EmptyStringProvider(), userManager, venueRepo, iconProvider)
    }

    private val dataSource: TestNoticeboardDataSource by lazy {
        TestNoticeboardDataSource(noticeboardItemFactory)
    }

    private val topicFilter = Filter(Topic.TopicArtAndDesign.id, "Art and Design")

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val globalTimeout: Timeout = Timeout.seconds(10)

    lateinit var viewModel: NoticeboardLandingViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { userManager.profileID } returns "testProfileID"
        every { dataSourceFactory.create() } returns dataSource
        every { filterManager.get(FilterType.TOPIC_FILTER) } returns listOf(topicFilter)
        every { filterManager.get(FilterType.CITY_FILTER) } returns listOf(
            Filter("DUB", "Dublin")
        )
        every { filterManager.get(FilterType.HOUSE_FILTER) } returns listOf(
            Filter(
                "SDH",
                "Shoreditch House"
            )
        )
        viewModel = NoticeboardLandingViewModel(
            profileId = null,
            dataSourceFactory = dataSourceFactory,
            repo = repository,
            filterManager = filterManager,
            sohoApiService = sohoRepo,
            ioDispatcher = testCoroutineDispatcher,
            analyticsManager = analyticsManager,
            stringProvider = stringProvider
        )
    }

    @Test
    fun `viewmodel emits pagedlist and filters, scroll-top-top event on refresh`() = runBlocking {

        val itemsObserver = mockk<Observer<PagedList<DiffItem>>>(relaxed = true)
        val filtersObserver = mockk<Observer<List<Filter>>>(relaxed = true)
        val scrollToTopObserver = mockk<Observer<Any>>(relaxed = true)

        viewModel.items.observeForever(itemsObserver)
        viewModel.filters.observeForever(filtersObserver)
        viewModel.scrollToTopEvent.observeForever(scrollToTopObserver)

        viewModel.refresh()

        val itemsSlot = CapturingSlot<PagedList<DiffItem>>()
        verify { itemsObserver.onChanged(capture(itemsSlot)) }
        assertEquals(11, itemsSlot.captured.size)

        val filtersSlot = ArrayList<List<Filter>>()
        verify { filtersObserver.onChanged(capture(filtersSlot)) }
        assertEquals(3, filtersSlot.last().size)

        verify { scrollToTopObserver.onChanged(any()) }

        assertEquals(1, dataSource.refreshCalls)
    }

    @Test
    fun `viewmodel updates filter manager appropriately on tag click`() = runBlocking {

        val itemsObserver = mockk<Observer<PagedList<DiffItem>>>(relaxed = true)
        viewModel.items.observeForever(itemsObserver)

        viewModel.onTopicClick(topicFilter)

        val filterTypeSlot = CapturingSlot<FilterType>()
        val filterSlot = CapturingSlot<List<Filter>>()
        verify { filterManager.clear() }
        verify { filterManager.set(capture(filterTypeSlot), capture(filterSlot)) }
        Assert.assertTrue(filterTypeSlot.captured == FilterType.TOPIC_FILTER)
        Assert.assertTrue(filterSlot.captured.first() == topicFilter)
        assertEquals(1, dataSource.refreshCalls)
    }

    @Test
    fun `on delete post viewmodel updates repo and datasource`() = runBlocking {
        coEvery { repository.deletePost(any()) } returns empty()

        val itemsObserver = mockk<Observer<PagedList<DiffItem>>>(relaxed = true)
        viewModel.items.observeForever(itemsObserver)

        val postId = "1"
        viewModel.deletePost(postId)
        coVerify { repository.deletePost(any()) }
        assertEquals(1, dataSource.deleteCalls.size)
        assertEquals(postId, dataSource.deleteCalls.first())
    }

    @Test
    fun `on refresh post viewmodel updates repo and datasource`() = runBlocking {
        val post = getNoticeboardPost()
        coEvery { repository.getPost(any()) } returns ApiResponse.Success(post)

        val itemsObserver = mockk<Observer<PagedList<DiffItem>>>(relaxed = true)
        viewModel.items.observeForever(itemsObserver)

        viewModel.onPostUpdated(post.postId)

        coVerify { repository.getPost(any()) }
        assertEquals(1, dataSource.updateCalls.size)
        assertEquals(post, dataSource.updateCalls.first())
    }

    @Test
    fun `on refresh post which is not found on API viewmodel updates repo and datasource`() =
        runBlockingTest {
            val post = NoticeboardTestHelper.getMockPost()
            coEvery { repository.getPost(any()) } returns
                    ApiResponse.Error(code = HttpURLConnection.HTTP_NOT_FOUND)

            viewModel.items.getOrAwaitValue()

            viewModel.onPostUpdated(post.id)

            coVerify { repository.getPost(any()) }
            assertEquals(1, dataSource.deleteCalls.size)
            assertEquals(post.id, dataSource.deleteCalls.first())
        }

    @Test
    fun `post reactions are not empty and are sorted correctly`() {
        val post = NoticeboardTestHelper.getMockPost()

        val noticeboardPost = noticeboardItemFactory.mapToItem(post, false)

        assert(noticeboardPost.reactions.isNotEmpty())

        val reactions = noticeboardPost.reactions.keys
        assert(noticeboardPost.reactions[reactions.elementAt(0)]!!
                > noticeboardPost.reactions[reactions.elementAt(1)]!!)
        assert(noticeboardPost.reactions[reactions.elementAt(1)]!!
                > noticeboardPost.reactions[reactions.elementAt(2)]!!)
    }
}

class TestNoticeboardDataSource(private val factory: NoticeboardItemFactory) :
    NoticeboardDataSource() {

    var refreshCalls: Int = 0
    val updateCalls: MutableList<NoticeboardPost> = mutableListOf()
    val deleteCalls: MutableList<String> = mutableListOf()

    override fun remove(postId: String) {
        deleteCalls.add(postId)
    }

    override fun update(post: NoticeboardPost) {
        updateCalls.add(post)

    }

    override fun refresh() {
        refreshCalls++
    }

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, DiffItem>,
    ) {
        val posts = mutableListOf<DiffItem>().also {
            it.add(CreatePostItem)
            for (i in 0 until 10) {
                it.add(
                    factory.mapToItem(
                        checkin = NoticeboardTestHelper.getMockPost(i.toString()),
                        showReplyCount = false
                    )
                )
            }
        }

        callback.onResult(posts, null, null)
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, DiffItem>) {
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, DiffItem>) {
    }

}