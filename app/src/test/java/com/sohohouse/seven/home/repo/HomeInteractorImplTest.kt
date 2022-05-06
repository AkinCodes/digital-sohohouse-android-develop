package com.sohohouse.seven.home.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.extensions.contains
import com.sohohouse.seven.common.extensions.getFormattedDayOfWeekDayMonth
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.prefs.LocalVenueProvider
import com.sohohouse.seven.common.prefs.PrefsManager
import com.sohohouse.seven.common.prefs.VenueAttendanceProvider
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.toolbar.Banner
import com.sohohouse.seven.connect.trafficlights.AvailableStatus
import com.sohohouse.seven.connect.trafficlights.UserAvailableStatus
import com.sohohouse.seven.connect.trafficlights.VenueMembers
import com.sohohouse.seven.connect.trafficlights.controlpanel.TrafficLightsControlPanel
import com.sohohouse.seven.connect.trafficlights.repo.TrafficLightsRepo
import com.sohohouse.seven.discover.benefits.BenefitsRepo
import com.sohohouse.seven.discover.housenotes.HouseNotesRepo
import com.sohohouse.seven.home.happeningnow.HappeningNowListFactory
import com.sohohouse.seven.home.suggested_people.SuggestedAdapterItem
import com.sohohouse.seven.home.suggested_people.SuggestedPeopleAdapterItem
import com.sohohouse.seven.network.base.model.empty
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Account
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.network.core.models.RecommendationDto
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.profile.ProfileRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.core.IsInstanceOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
class HomeInteractorImplTest {

    @MockK
    lateinit var userManager: UserManager

    @MockK
    lateinit var houseManager: HouseManager

    @MockK
    lateinit var venueRepo: VenueRepo

    @MockK
    lateinit var venueAttendanceProvider: VenueAttendanceProvider

    @MockK
    lateinit var localVenueProvider: LocalVenueProvider

    @MockK
    lateinit var accountInteractor: AccountInteractor

    @MockK
    lateinit var eventsRepo: EventsRepo

    @MockK
    lateinit var benefitsRepo: BenefitsRepo

    @MockK
    lateinit var profileRepo: ProfileRepository

    @MockK
    lateinit var houseNotesRepo: HouseNotesRepo

    @MockK
    lateinit var happeningNowFactory: HappeningNowListFactory

    @MockK
    lateinit var prefsManager: PrefsManager

    @MockK
    lateinit var trafficLightsRepo: TrafficLightsRepo

    @MockK
    lateinit var sohoRepo: SohoApiService

    @MockK
    lateinit var api: CoreApi

    @MockK
    lateinit var housePayBannerDelegate: HousePayBannerDelegate

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        coEvery { eventsRepo.getDynamicHouseEvents(any()) } returns value(listOf())
        coEvery { eventsRepo.getMemberEventsOnDemand() } returns value(listOf())
        every { accountInteractor.getAttendingVenue() } returns Venue()
        coEvery { eventsRepo.getHappeningNowEvents(any(), any()) } returns value(listOf())
        coEvery { venueRepo.fetchVenues() } returns value(VenueList.empty())
        every { venueAttendanceProvider.attendingVenue } returns Venue()
        every {
            happeningNowFactory.getUpcomingEvents(
                any(),
                any()
            )
        } returns BaseAdapterItem.HappeningNowItem.Container(mutableListOf())
        every {
            happeningNowFactory.getDynamicHouseEvents(
                any(),
                any()
            )
        } returns BaseAdapterItem.HappeningNowItem.Container(mutableListOf())
        every { benefitsRepo.getPerks(any(), any(), any(), any()) } returns value(listOf())
        every { houseNotesRepo.getAll() } returns value(listOf())
        every { profileRepo.getMyProfile() } returns value(Profile())
        every { houseManager.getLocalHouseId() } returns "test"
        every { accountInteractor.getCompleteAccountV2(any(), any()) } returns value(Account())
        every { prefsManager.notificationsCustomised } returns false
        every { userManager.favouriteHouses } returns listOf()
        every { accountInteractor.canAccess(any<Venue>()) } returns true
        every { userManager.profileImageURL } returns "testURL"
        every {
            runBlocking {
                trafficLightsRepo.getVenueMembers(perPage = 10, isInitialLoad = false, page = 1)
            }
        } returns value(VenueMembers(mutableListOf(), estimatedTotal = 0, nextPage = 1))
        every {
            localVenueProvider.localVenue
        } returns MutableLiveData(Venue())
        every {
            userManager.availableStatusFlow
        } returns MutableStateFlow(UserAvailableStatus("_id", AvailableStatus.UNAVAILABLE))

        every {
            userManager.connectRecommendationOptIn
        } returns ""

        mockkStatic("com.sohohouse.seven.common.extensions.DateKt")
        every { any<Date>().getFormattedDayOfWeekDayMonth(any()) } returns "1 January"
        coEvery { housePayBannerDelegate.housePayBannerFlow() } returns flowOf(
            listOf(
                fakeBanner()
            )
        )
    }

    private fun fakeBanner() = Banner(
        id = "", title = "", subtitle = "", cta = ""
    )

    @Test
    fun `friends member gets correct sections`() {
        runBlockingTest {
            val homeInteractor = createHomeInteractor()

            every { prefsManager.subscriptionType } returns SubscriptionType.FRIENDS

            val flow = homeInteractor.getHomeItems(refresh = true)

            val items = flow.toList().last()
            assertEquals(4, items.size)

            assertFalse(items.contains { it is BaseAdapterItem.HappeningNowItem })
            assertTrue(items.contains { it is BaseAdapterItem.SetUpAppPromptItem })
            assertTrue(items.contains { it is BaseAdapterItem.DiscoverPerks })
            assertTrue(items.contains { it is Banner })
        }
    }

    @Test
    fun `house member gets correct sections`() {
        runBlockingTest {
            val homeInteractor = createHomeInteractor()

            every { venueAttendanceProvider.attendingVenue } returns Venue()
            every { venueRepo.venues() } returns VenueList(Venue())
            every { prefsManager.subscriptionType } returns SubscriptionType.EVERY
            every {
                runBlocking {
                    trafficLightsRepo.getVenueMembers(false, 1, 10)
                }
            } returns value(VenueMembers(mutableListOf(), 2, 2, "WH"))
            every { venueRepo.venues() } returns VenueList(Venue())

            val flow = homeInteractor.getHomeItems(refresh = true)
            val items = flow.toList().last()
            assertEquals(10, items.size)

            assertEquals(2, items.count { it is BaseAdapterItem.HappeningNowItem })
            assertTrue(items.contains { it is BaseAdapterItem.SetUpAppPromptItem })
            assertTrue(items.contains { it is BaseAdapterItem.DiscoverPerks })
            assertTrue(items.contains { it is BaseAdapterItem.OurHousesItem })
            assertTrue(items.contains { it is BaseAdapterItem.HouseNoteItem })
            assertTrue(items.contains { it is TrafficLightsControlPanel })
            assertTrue(items.contains { it is BaseAdapterItem.BannerCarouselItem })
            assertTrue(items.contains { it is SuggestedAdapterItem })
            assertTrue(items.contains { it is Banner })
        }
    }

    @Test
    fun `when user is not opted in should show placeholders in suggestion carousel`() =
        runBlockingTest {
            val homeInteractor = createHomeInteractor()

            every { prefsManager.subscriptionType } returns SubscriptionType.EVERY
            every { venueAttendanceProvider.attendingVenue } returns Venue()
            every { venueRepo.venues() } returns VenueList(Venue())
            every {
                runBlocking {
                    trafficLightsRepo.getVenueMembers(false, 1, 10)
                }
            } returns empty()

            val suggestedCarousel =
                homeInteractor.getHomeItems(refresh = true).toList().last()
                    .find { it is SuggestedAdapterItem } as SuggestedAdapterItem

            assertEquals(false, suggestedCarousel.isOptedInForSuggestions)
            assertThat(
                suggestedCarousel.suggestedMembers,
                CoreMatchers.everyItem(IsInstanceOf(SuggestedPeopleAdapterItem.Placeholder::class.java))
            )
        }

    @Test
    fun `if user is opted in then show suggested members`() = runBlockingTest {
        val homeInteractor = createHomeInteractor()

        every { prefsManager.subscriptionType } returns SubscriptionType.EVERY
        every { venueAttendanceProvider.attendingVenue } returns Venue()
        every { venueRepo.venues() } returns VenueList(Venue())
        every {
            userManager.connectRecommendationOptIn
        } returns "123"
        every {
            runBlocking {
                sohoRepo.getRecommendations()
            }
        } returns ApiResponse.Success(listOf(RecommendationDto()))
        every {
            runBlocking {
                trafficLightsRepo.getVenueMembers(false, 1, 10)
            }
        } returns empty()

        val suggestedCarousel =
            homeInteractor.getHomeItems(refresh = true).toList().last()
                .find { it is SuggestedAdapterItem } as SuggestedAdapterItem

        assertEquals(true, suggestedCarousel.isOptedInForSuggestions)
        assertThat(
            suggestedCarousel.suggestedMembers,
            CoreMatchers.everyItem(IsInstanceOf(SuggestedPeopleAdapterItem.NormalItem::class.java))
        )
    }

    @Test
    fun `loading state is enters loading then idle state when fetching home items`() {
        runBlockingTest {
            val homeInteractor = createHomeInteractor()

            every { prefsManager.subscriptionType } returns SubscriptionType.EVERY

            val loadingStates = mutableListOf<LoadingState>()
            val job = launch {
                homeInteractor.loadingState().toList(loadingStates)
            }

            homeInteractor.getHomeItems(refresh = true)

            assertEquals(3, loadingStates.size)
            assertEquals(LoadingState.Idle, loadingStates.first())
            assertEquals(LoadingState.Loading, loadingStates[1])
            assertEquals(LoadingState.Idle, loadingStates[2])

            job.cancel()
        }

    }

    @Test
    fun `loading state returns to idle when a flow throws an error`() {
        runBlockingTest {
            val homeInteractor = createHomeInteractor()

            every { prefsManager.subscriptionType } returns SubscriptionType.EVERY
            coEvery { eventsRepo.getHappeningNowEvents(any(), any()) } throws Throwable()

            val loadingStates = mutableListOf<LoadingState>()
            val job = launch {
                homeInteractor.loadingState().toList(loadingStates)
            }

            homeInteractor.getHomeItems(refresh = true)

            assertEquals(3, loadingStates.size)
            assertEquals(LoadingState.Idle, loadingStates.first())
            assertEquals(LoadingState.Loading, loadingStates[1])
            assertEquals(LoadingState.Idle, loadingStates[2])

            job.cancel()
        }

    }

    @Test
    fun `do not show suggested people carousel to members with FRIENDS subscription`() =
        runBlockingTest {
            val homeInteractor = createHomeInteractor()

            every { prefsManager.subscriptionType } returns SubscriptionType.FRIENDS
            every { venueAttendanceProvider.attendingVenue } returns Venue()
            every { venueRepo.venues() } returns VenueList(Venue())
            every {
                userManager.connectRecommendationOptIn
            } returns ""
            every {
                runBlocking {
                    sohoRepo.getRecommendations()
                }
            } returns ApiResponse.Success(listOf(RecommendationDto()))

            val suggestedCarousel =
                homeInteractor.getHomeItems(refresh = true).toList().last()
                    .find { it is SuggestedAdapterItem } as? SuggestedAdapterItem

            assertEquals(null, suggestedCarousel)

        }

    private fun createHomeInteractor() = HomeInteractorImpl(
        userManager = userManager,
        eventsRepo = eventsRepo,
        benefitsRepo = benefitsRepo,
        profileRepo = profileRepo,
        houseNotesRepo = houseNotesRepo,
        happeningNowFactory = happeningNowFactory,
        trafficLightsRepo = trafficLightsRepo,
        prefsManager = prefsManager,
        venueRepo = venueRepo,
        venueAttendanceProvider = venueAttendanceProvider,
        housePayBannerDelegate = housePayBannerDelegate,
        ioDispatcher = testCoroutineDispatcher,
        apiService = sohoRepo
    )
}