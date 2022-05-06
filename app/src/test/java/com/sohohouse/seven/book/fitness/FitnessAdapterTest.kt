package com.sohohouse.seven.book.fitness

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
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = TestApp::class)
class FitnessAdapterTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var houseManager: HouseManager

    @Mock
    lateinit var userManager: UserManager
    lateinit var exploreFactory: ExploreListFactory

    val featuredList: MutableList<Event> = ArrayList()
    val allFitnessList: MutableList<Event> = ArrayList()

    @Suppress("DEPRECATION")
    val event = Event(
        _name = "Event Name",
        featured = true,
        startsAt = Date(2020, 9, 12),
        endsAt = Date(2020, 9, 13),
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
        allFitnessList.clear()
    }

    @Test
    fun `adapter contains zero state for empty lists`() {
        //Given all lists are empty
        //When adapter is created
        val adapter = createAdapter(featuredList, allFitnessList)

        //Then adapter contains unfiltered state header and zero state
        assertThat(adapter.itemCount).isEqualTo(1)
        assertThat(adapter.getItemViewType(0) == ZeroStateAdapterItem::class.hashCode())
    }

    @Test
    fun `adapter contains error state`() {
        //Given all lists are empty
        //When adapter is created
        val adapter = createAdapter(featuredList, allFitnessList, isError = true)

        //Then adapter contains zero state
        assertThat(adapter.itemCount).isEqualTo(1)
        assertThat(adapter.getItemViewType(0) == ErrorStateAdapterItem::class.hashCode())
    }

    @Test
    fun `adapter has feature items`() {
        //Given feature list has at most 3 feature items and 1 screening item
        setupData(3, 1)

        //When adapter is created
        val adapter = createAdapter(featuredList, allFitnessList)

        //Then adapter has 2 items: 1 feature events and 1 list event item
        assertThat(adapter.itemCount).isEqualTo(2)
        assertThat(adapter.getItemViewType(0) == FeatureEvent::class.java.hashCode())
        assertThat(adapter.getItemViewType(1) == ListEvent::class.java.hashCode())
    }

    @Test
    fun `adapter only has all fitness events list as non-empty`() {
        //Given only all fitness events list is non-empty
        setupData(0, 3)

        //When adapter is created
        val adapter = createAdapter(featuredList, allFitnessList)

        //one feature event type (soonest open event) and remaininf of list type
        Assert.assertEquals(3, adapter.itemCount)
        assertThat(adapter.getItemViewType(0) == FeatureEvent::class.java.hashCode())
        assertThat(adapter.getItemViewType(1) == ListEvent::class.java.hashCode())
        assertThat(adapter.getItemViewType(2) == ListEvent::class.java.hashCode())
    }


    //getIntent bind helpers
    private fun createAdapter(
        featuredList: List<Event>,
        fitnessList: List<Event>,
        isFiltered: Boolean = false,
        isError: Boolean = false
    ): RendererDiffAdapter<DiffItem> {
        val data = if (isError) exploreFactory.createErrorItems()
        else exploreFactory.createExploreFitnessItems(
            featuredList = featuredList,
            allFitnessList = fitnessList,
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

    //modifies the featuredList and allFitnessList with the number of events specified
    private fun setupData(numOfFeatureEvents: Int, numOfAllFitnessEvents: Int) {
        for (i in 0 until numOfFeatureEvents) {
            featuredList.add(event.copy("${event.name} $i"))
        }
        for (i in 0 until numOfAllFitnessEvents) {
            allFitnessList.add(event.copy("${event.name} $i"))
        }
    }
    //endregion
}
