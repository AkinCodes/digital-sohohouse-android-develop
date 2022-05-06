package com.sohohouse.seven.book.adapter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.book.adapter.model.*
import com.sohohouse.seven.book.adapter.renderer.*
import com.sohohouse.seven.common.design.adapter.RendererDiffAdapter
import com.sohohouse.seven.common.design.carousel.CarouselHeaderRenderer
import com.sohohouse.seven.common.design.carousel.CarouselRenderer
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.views.EventStatusHelperImpl
import com.sohohouse.seven.common.views.ExploreListFactory
import com.sohohouse.seven.network.core.models.Event
import moe.banana.jsonapi2.HasOne
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.junit.*
import org.junit.rules.Timeout
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApp::class)
class EventsAdapterTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var houseManager: HouseManager

    @Mock
    lateinit var userManager: UserManager

    lateinit var exploreFactory: ExploreListFactory

    @get:Rule
    var timeout = Timeout(10, TimeUnit.SECONDS)

    val featuredList: MutableList<Event> = ArrayList()
    val allEventsList: MutableList<Event> = ArrayList()

    @Suppress("DEPRECATION")
    val event = Event(
        _name = "Event Name",
        featured = true,
        startsAt = Date(2020, 9, 12),
        endsAt = Date(2020, 9, 13),
        venue = HasOne("venue", "SD"),
        state = "open_for_booking"
    )
    val venues = VenueList.empty()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val eventStatusHelper = EventStatusHelperImpl(userManager, houseManager)
        exploreFactory = ExploreListFactory(eventStatusHelper)
    }

    @After
    fun teardown() {
        featuredList.clear()
        allEventsList.clear()
    }

    @Test
    fun `for empty lists in unfiltered state, adapter contains zero state`() {
        //Given all lists are empty
        //When adapter is created
        val adapter = createAdapter(featuredList, allEventsList)

        //Then adapter contains zero state
        assertThat(adapter.itemCount).isEqualTo(1)
        assertThat(adapter.getItemViewType(0) == ZeroStateAdapterItem::class.hashCode())
    }

    @Test
    fun `adapter contains error state`() {
        //Given all lists are empty
        //When adapter is created
        val adapter = createAdapter(featuredList, allEventsList, isError = true)

        //Then adapter contains zero state
        assertThat(adapter.itemCount).isEqualTo(1)
        assertThat(adapter.getItemViewType(0) == ErrorStateAdapterItem::class.hashCode())
    }

    @Test
    fun `adapter has only all events non-empty`() {
        //Given only allEvents is non-empty
        setupData(0, 3)

        //When adapter is created
        val adapter = createAdapter(featuredList, allEventsList)

        //one feature event type (soonest open event) and remaininf of list type
        Assert.assertEquals(3, adapter.itemCount)
        AssertionsForInterfaceTypes.assertThat(adapter.getItemViewType(0) == FeatureEvent::class.hashCode())
        AssertionsForInterfaceTypes.assertThat(adapter.getItemViewType(1) == ListEvent::class.hashCode())
        AssertionsForInterfaceTypes.assertThat(adapter.getItemViewType(2) == ListEvent::class.hashCode())
    }

    @Test
    fun `adapter contains items in the order of featured, all events`() {
        //Given all lists contain one event
        setupData(1, 1)

        //When adapter is created
        val adapter = createAdapter(featuredList, allEventsList)

        //Then adapter should contain 2 items in the following order:
        // 1 feature event,  1 all events item (list)
        assertThat(adapter.getItemViewType(0) == FeatureEvent::class.hashCode())
        assertThat(adapter.getItemViewType(1) == ListEvent::class.hashCode())
    }

    //getIntent bind helpers
    private fun createAdapter(
        featuredList: List<Event>,
        allEventList: List<Event>,
        isFiltered: Boolean = false,
        isError: Boolean = false
    ): RendererDiffAdapter<DiffItem> {
        val data = if (isError) exploreFactory.createErrorItems()
        else exploreFactory.createExploreEventsItems(
            featuredList = featuredList,
            allList = allEventList,
            isFiltered = isFiltered, venues = venues
        )
        return RendererDiffAdapter<DiffItem>().apply {
            registerRenderers(
                FeatureEventRenderer(),
                ListEventRenderer(),
                DividerRenderer(),
                LoadingStateRenderer(),
                ZeroStateItemRenderer(),
                CarouselHeaderRenderer(EventCarouselHeader::class.java),
                CarouselRenderer(EventCarousel::class.java) { item, imageView, position -> },
                ErrorStateRenderer()
            )
            submitItems(data)
        }
    }

    //modifies the featuredList, historyList, and allEventsList with the number of events specified
    private fun setupData(numOfFeatureEvents: Int, numOfAllEvents: Int) {
        for (i in 0 until numOfFeatureEvents) {
            featuredList.add(event.copy("${event.name} $i"))
        }
        for (i in 0 until numOfAllEvents) {
            allEventsList.add(event.copy("${event.name} $i"))
        }
    }
    //endregion
}