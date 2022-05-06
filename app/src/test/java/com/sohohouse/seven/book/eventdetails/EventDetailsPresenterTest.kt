package com.sohohouse.seven.book.eventdetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.sohohouse.seven.R
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.book.eventdetails.payment.BookEventHelper
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.events.ExploreCategoryManager
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.GymMembership
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.*
import com.sohohouse.seven.common.views.eventdetaillist.*
import com.sohohouse.seven.network.base.model.empty
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.*
import com.sohohouse.seven.network.core.request.DeleteBookingRequest
import com.sohohouse.seven.network.core.request.GetEventDetailsRequest
import com.sohohouse.seven.network.core.request.PatchEventBookingRequest
import com.sohohouse.seven.network.core.request.PostEventBookingRequest
import com.sohohouse.seven.network.vimeo.VimeoRequestFactory
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.reactivex.Single
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.ObjectDocument
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = TestApp::class)
class EventDetailsPresenterTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var zipRequestsUtil: ZipRequestsUtil

    @Mock
    lateinit var view: EventDetailsActivity

    @Mock
    lateinit var loadingView: LoadingView

    @Mock
    lateinit var categoryManager: ExploreCategoryManager

    @Mock
    lateinit var analyticsManager: AnalyticsManager

    @Mock
    lateinit var houseManager: HouseManager

    @Mock
    lateinit var userManager: UserManager

    @Mock
    lateinit var vimeoRequestFactory: VimeoRequestFactory

    @MockK
    lateinit var venueRepo: VenueRepo

    lateinit var eventStatusHelper: EventStatusHelper

    lateinit var eventDetailsPresenter: EventDetailsPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MockKAnnotations.init(this)
        val bookEventHelper = BookEventHelper(zipRequestsUtil)
        val stepperPresenter: StepperPresenter = StepperPresenterImpl(houseManager, userManager)
        eventStatusHelper = EventStatusHelperImpl(userManager, houseManager)
        eventDetailsPresenter = EventDetailsPresenter(
            zipRequestsUtil,
            categoryManager,
            bookEventHelper,
            houseManager,
            userManager,
            venueRepo,
            stepperPresenter,
            vimeoRequestFactory,
            eventStatusHelper,
            analyticsManager
        )
    }

    @Test
    fun `for event with price greater than zero, price item is after overview item`() {
        //Given event details of a event whose price is greater than zero
        eventDetailsPresenter.eventId = "id"

        val resultVenue = Venue(
            _name = "VENUE NAME",
            _venueColors = VenueColors(house = "#000000"),
            _timeZone = "Europe/London"
        )
        resultVenue.id = "SD"
        val resource = EventResource(resourceType = "MEMBER_EVENTS")
        resource.id = "MEMBER_EVENTS"
        val booking = EventBooking(
            state = BookingState.UNCONFIRMED.name,
            bookingType = BookingType.GUEST_LIST.name
        ).apply { id = "1" }
        val result = Event(
            priceCents = 1, priceCurrency = "GBP", venue = HasOne("venue", "SD"),
            resource = HasOne(resource),
            booking = HasOne(booking)
        )
        result.id = "id"
        val document = ObjectDocument(result)
        document.addInclude(resource)
        document.addInclude(booking)
        result.document = document
        val venueMap = mapOf(Pair("SD", resultVenue))

        every { venueRepo.venues() } returns VenueList(listOf(resultVenue))
        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetEventDetailsRequest>()))
            .thenReturn(Single.just(value(result)))
        Mockito.`when`(categoryManager.getCategories())
            .thenReturn(Single.just(value(listOf())))
        Mockito.`when`(view.loadingView)
            .thenReturn(loadingView)
        mockUserAccount()

        // WHEN the view is attached
        eventDetailsPresenter.attach(view)
        eventDetailsPresenter.fetchData(true)

        // THEN the presenter fetches event details and the price item is located after overview item
        val argument = argumentCaptor<MutableList<BaseEventDetailsAdapterItem>>()
        verify(zipRequestsUtil).issueApiCall(any<GetEventDetailsRequest>())
        verify(view).showEventDetails(argument.capture())
        Assertions.assertThat(argument.firstValue[0] is EventOverviewAdapterItem).isTrue()

        Assertions.assertThat(argument.firstValue[1] is EventTicketsAdapterItem).isTrue()
        assertAttributeItems(
            argument.firstValue[1], iconDrawableRes = R.drawable.icon_events_tickets,
            labelStringRes = R.string.explore_events_event_tickets_label,
            description = null
        )
    }

    @Test
    fun `for house visit events`() {
        eventDetailsPresenter.eventId = "id"

        val resource = EventResource(resourceType = "HOUSE_VISIT")
        resource.id = "HOUSE_VISIT"

        val booking = EventBooking(
            state = BookingState.UNCONFIRMED.name,
            bookingType = BookingType.GUEST_LIST.name
        ).apply { id = "1" }
        val result = Event(
            _name = "House Kitchen",
            _address = "Soho House Amsterdam",
            _description = "Some description here",
            venue = HasOne("venue", "AMST"),
            resource = HasOne(resource),
            booking = HasOne(booking)
        )
        result.id = "id"

        val resultVenue = Venue(
            _name = "VENUE NAME",
            _venueColors = VenueColors(house = "#000000"),
            _timeZone = "Europe/London"
        )
        resultVenue.id = "AMST"

        val document = ObjectDocument(result)
        document.addInclude(resultVenue)
        document.addInclude(resource)
        document.addInclude(booking)
        result.document = document

        val venueMap = mapOf(Pair("AMST", resultVenue))

        coEvery { venueRepo.venues() } returns VenueList(listOf(resultVenue))
        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetEventDetailsRequest>()))
            .thenReturn(Single.just(value(result)))
        Mockito.`when`(categoryManager.getCategories())
            .thenReturn(Single.just(value(listOf())))
        Mockito.`when`(view.loadingView)
            .thenReturn(loadingView)

        mockUserAccount()

        // WHEN the view is attached
        eventDetailsPresenter.attach(view)
        eventDetailsPresenter.fetchData(true)

        // THEN the presenter fetches event details and the list provided to view contains:
        val argument = argumentCaptor<MutableList<BaseEventDetailsAdapterItem>>()
        verify(zipRequestsUtil).issueApiCall(any<GetEventDetailsRequest>())
        verify(view).showEventDetails(argument.capture())
        Assertions.assertThat(argument.firstValue.size).isEqualTo(5)

        //first item is Overview item
        Assertions.assertThat(argument.firstValue[0] is EventOverviewAdapterItem).isTrue()
        val item = argument.firstValue[0] as EventOverviewAdapterItem
        Assertions.assertThat(item.eventName).isEqualTo(result.name)
        Assertions.assertThat(item.houseName).isEqualTo(resultVenue.name)
        Assertions.assertThat(item.houseColor).isEqualTo(resultVenue.venueColors.house)
        Assertions.assertThat(item.eventStatus)
            .isEqualTo(eventStatusHelper.getRestrictedEventStatus(result, resultVenue))

        //second item is Ticket item
        Assertions.assertThat(argument.firstValue[1] is EventTicketsAdapterItem).isTrue()

        //third item is Date item
        Assertions.assertThat(argument.firstValue[2] is EventDateAdapterItem).isTrue()
        assertAttributeItems(
            argument.firstValue[2],
            iconDrawableRes = R.drawable.icon_events_date,
            labelStringRes = R.string.explore_events_event_date_label,
            description = ""
        )

        //fourth item is Event Description item
        Assertions.assertThat(argument.firstValue[3] is EventDescriptionAdapterItem).isTrue()
        assertAttributeItems(
            argument.firstValue[3],
            iconDrawableRes = R.drawable.icon_events_detail,
            labelStringRes = R.string.explore_events_event_details_label,
            description = result.description
        )

        //last item is House details item
        Assertions.assertThat(argument.firstValue[4] is HouseDetailsAdapterItem).isTrue()
        assertAttributeItems(
            argument.firstValue[4],
            iconDrawableRes = R.drawable.icon_events_location,
            labelStringRes = R.string.explore_events_house_event_details_label,
            description = null,
            CTAStringRes = R.string.explore_events_event_map_cta
        )
    }

    @Test
    fun `for cinema events`() {
        //Given event details is of a cinema event
        eventDetailsPresenter.eventId = "id"

        val resultFilm = EventsFilm(
            director = "Dan Inkpen",
            cast = "Dan, Erica",
            distributor = "Black Bear Pictures",
            year = 12,
            runningTime = 123,
            country = "USA",
            certificate = "R",
            subtitles = "French",
            language = "English"
        )

        resultFilm.id = "1"

        val resultVenue = Venue(
            _name = "VENUE NAME",
            _venueColors = VenueColors(house = "#000000"),
            _timeZone = "Europe/London",
            venueAddress = VenueAddress(
                listOf("Television Centre 101 Wood Lane"),
                postalCode = "W12 7FR",
                country = "United Kingdom"
            ),
            _city = "London"
        )

        val resultParentVenue = Venue(
            _name = "VENUE NAME",
            _venueColors = VenueColors(house = "#000000"),
            _timeZone = "Europe/London",
            venueAddress = VenueAddress(
                listOf("Television Centre 101 Wood Lane"),
                postalCode = "W12 7FR",
                country = "United Kingdom"
            ),
            _city = "London"
        )

        resultVenue.id = "SD"
        resultParentVenue.id = "SD"
        val resource = EventResource(resourceType = "SCREENING")
        resource.id = "SCREENING"
        val booking = EventBooking(
            state = BookingState.UNCONFIRMED.name,
            bookingType = BookingType.GUEST_LIST.name
        ).apply { id = "1" }
        val result = Event(
            film = HasOne(resultFilm), venue = HasOne("venue", "SD"), resource = HasOne(resource),
            booking = HasOne(booking)
        )
        result.id = "id"
        val document = ObjectDocument(resultFilm)
        document.addInclude(resultFilm)
        document.addInclude(resultVenue)
        document.addInclude(resource)
        document.addInclude(booking)
        result.document = document

        val venueMap = mapOf(Pair("SD", resultVenue))
        every { venueRepo.venues() } returns VenueList(listOf(resultVenue))

        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetEventDetailsRequest>()))
            .thenReturn(Single.just(value(result)))
        Mockito.`when`(categoryManager.getCategories())
            .thenReturn(Single.just(value(listOf())))
        Mockito.`when`(view.loadingView)
            .thenReturn(loadingView)

        mockUserAccount()

        // WHEN the view is attached
        eventDetailsPresenter.attach(view)
        eventDetailsPresenter.fetchData(true)

        // THEN the presenter fetches event details and the list provided to view contains:
        val argument = argumentCaptor<MutableList<BaseEventDetailsAdapterItem>>()
        verify(zipRequestsUtil).issueApiCall(any<GetEventDetailsRequest>())
        verify(view).showEventDetails(argument.capture())
        Assertions.assertThat(argument.firstValue.size).isEqualTo(14)

        //first item is Overview item
        Assertions.assertThat(argument.firstValue[0] is EventOverviewAdapterItem).isTrue()
        val item = argument.firstValue[0] as EventOverviewAdapterItem
        Assertions.assertThat(item.eventName).isEqualTo(result.name)
        Assertions.assertThat(item.houseName).isEqualTo(resultVenue.name)
        Assertions.assertThat(item.houseColor).isEqualTo(resultVenue.venueColors.house)
        Assertions.assertThat(item.eventStatus)
            .isEqualTo(eventStatusHelper.getRestrictedEventStatus(result, resultVenue))

        //second item is Ticket item
        Assertions.assertThat(argument.firstValue[1] is EventTicketsAdapterItem).isTrue()

        //third item is Date item
        Assertions.assertThat(argument.firstValue[2] is EventDateAdapterItem).isTrue()
        assertAttributeItems(
            argument.firstValue[2],
            iconDrawableRes = R.drawable.icon_events_date,
            labelStringRes = R.string.explore_events_event_date_label,
            description = ""
        )

        //fourth item is Event Description item
        Assertions.assertThat(argument.firstValue[3] is EventDescriptionAdapterItem).isTrue()
        assertAttributeItems(
            argument.firstValue[3],
            iconDrawableRes = R.drawable.icon_events_detail,
            labelStringRes = R.string.explore_cinema_event_details_label,
            description = result.description
        )

        //next 6 items are Cinema Sub Description Items in the following order
        assertCinemaSubDescription(
            argument.firstValue[4],
            R.string.explore_cinema_event_director_label,
            resultFilm.director
                ?: ""
        )
        assertCinemaSubDescription(
            argument.firstValue[5], R.string.explore_cinema_event_cast_label, resultFilm.cast
                ?: ""
        )
        assertCinemaSubDescription(
            argument.firstValue[6],
            R.string.explore_cinema_event_distributor_label,
            resultFilm.distributor
                ?: ""
        )
        assertCinemaSubDescription(
            argument.firstValue[7],
            R.string.explore_cinema_event_year_label,
            resultFilm.year.toString()
        )
        assertCinemaSubDescription(
            argument.firstValue[8],
            R.string.explore_cinema_event_runtime_label,
            resultFilm.runningTime.toString()
        )
        assertCinemaSubDescription(
            argument.firstValue[9], R.string.explore_cinema_event_country_label, resultFilm.country
                ?: ""
        )
        assertCinemaSubDescription(
            argument.firstValue[10],
            R.string.explore_cinema_event_certificate_label,
            resultFilm.certificate
                ?: ""
        )
        assertCinemaSubDescription(
            argument.firstValue[11],
            R.string.explore_cinema_event_subtitles_label,
            resultFilm.subtitles
                ?: ""
        )
        assertCinemaSubDescription(
            argument.firstValue[12],
            R.string.explore_cinema_event_language_label,
            resultFilm.language ?: ""
        )


        //last item is House details item
        Assertions.assertThat(argument.firstValue[13] is HouseDetailsAdapterItem).isTrue()
        assertAttributeItems(
            argument.firstValue[13],
            iconDrawableRes = R.drawable.icon_events_location,
            labelStringRes = R.string.explore_events_house_event_details_label,
            description = HouseDetailsAdapterItem.getVenueAddress(resultVenue, resultParentVenue),
            CTAStringRes = R.string.explore_events_event_map_cta
        )
    }


    @Test
    fun `for non-cinema events`() {
        //Given event details of a non-cinema event
        eventDetailsPresenter.eventId = "id"

        val resultVenue = Venue(
            _name = "VENUE NAME",
            _venueColors = VenueColors(house = "#000000"),
            _timeZone = "Europe/London",
            venueAddress = VenueAddress(
                listOf("Television Centre 101 Wood Lane"),
                postalCode = "W12 7FR",
                country = "United Kingdom"
            ),
            _city = "London"
        )
        resultVenue.id = "1"
        val resource = EventResource(resourceType = "MEMBER_EVENTS")
        resource.id = "MEMBER_EVENTS"
        val booking = EventBooking(
            state = BookingState.UNCONFIRMED.name,
            bookingType = BookingType.GUEST_LIST.name
        ).apply { id = "1" }
        val result = Event(
            venue = HasOne("venue", "1"), resource = HasOne(resource),
            booking = HasOne(booking)
        )
        result.id = "id"
        val document = ObjectDocument(result)
        document.addInclude(resultVenue)
        document.addInclude(resource)
        document.addInclude(booking)
        result.document = document
        val venueMap = mapOf(Pair("1", resultVenue))

        every { venueRepo.venues() } returns VenueList(listOf(resultVenue))
        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetEventDetailsRequest>()))
            .thenReturn(Single.just(value(result)))
        Mockito.`when`(categoryManager.getCategories())
            .thenReturn(Single.just(value(listOf())))
        Mockito.`when`(view.loadingView)
            .thenReturn(loadingView)
        mockUserAccount()

        // WHEN the view is attached
        eventDetailsPresenter.attach(view)
        eventDetailsPresenter.fetchData(true)

        // THEN the presenter fetches event details and the list provided to view contains:
        val argument = argumentCaptor<MutableList<BaseEventDetailsAdapterItem>>()
        verify(zipRequestsUtil).issueApiCall(any<GetEventDetailsRequest>())
        verify(view).showEventDetails(argument.capture())
        Assertions.assertThat(argument.firstValue.size).isEqualTo(5)

        //first item is Overview item
        Assertions.assertThat(argument.firstValue[0] is EventOverviewAdapterItem).isTrue()
        val item = argument.firstValue[0] as EventOverviewAdapterItem
        Assertions.assertThat(item.eventName).isEqualTo(result.name)
        Assertions.assertThat(item.houseName).isEqualTo(resultVenue.name)
        Assertions.assertThat(item.houseColor).isEqualTo(resultVenue.venueColors.house)
        Assertions.assertThat(item.eventStatus)
            .isEqualTo(eventStatusHelper.getRestrictedEventStatus(result, resultVenue))

        //second item is Ticket item
        Assertions.assertThat(argument.firstValue[1] is EventTicketsAdapterItem).isTrue()

        //third item is Date item
        Assertions.assertThat(argument.firstValue[2] is EventDateAdapterItem).isTrue()
        assertAttributeItems(
            argument.firstValue[2],
            iconDrawableRes = R.drawable.icon_events_date,
            labelStringRes = R.string.explore_events_event_date_label,
            description = ""
        )

        //fourth item is Event Description item
        Assertions.assertThat(argument.firstValue[3] is EventDescriptionAdapterItem).isTrue()
        assertAttributeItems(
            argument.firstValue[3],
            iconDrawableRes = R.drawable.icon_events_detail,
            labelStringRes = R.string.explore_events_event_details_label,
            description = result.description
        )

        //last item is House details item
        Assertions.assertThat(argument.firstValue[4] is HouseDetailsAdapterItem).isTrue()
        assertAttributeItems(
            argument.firstValue[4],
            iconDrawableRes = R.drawable.icon_events_location,
            labelStringRes = R.string.explore_events_house_event_details_label,
            description = "",
            CTAStringRes = R.string.explore_events_event_map_cta
        )
    }

    @Test
    fun `upon failure to find the event's category in the category manager, the overview item's categoryUrl is empty`() {
        //Given event details of a non-cinema event
        eventDetailsPresenter.eventId = "id"

        val resultVenue = Venue(
            _name = "VENUE NAME",
            _venueColors = VenueColors(house = "#000000"),
            _timeZone = "Europe/London"
        )
        resultVenue.id = "SD"
        val resource = EventResource(resourceType = "MEMBER_EVENTS")
        resource.id = "MEMBER_EVENTS"
        val booking = EventBooking(
            state = BookingState.UNCONFIRMED.name,
            bookingType = BookingType.GUEST_LIST.name
        ).apply { id = "1" }
        val result = Event(
            venue = HasOne("venue", "SD"), resource = HasOne(resource),
            booking = HasOne(booking)
        )
        result.id = "id"
        val document = ObjectDocument(result)
        document.addInclude(resultVenue)
        document.addInclude(resource)
        document.addInclude(booking)
        result.document = document

        val venueMap = mapOf(Pair("SD", resultVenue))


        every { venueRepo.venues() } returns VenueList(listOf(resultVenue))
        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetEventDetailsRequest>()))
            .thenReturn(Single.just(value(result)))
        Mockito.`when`(view.loadingView)
            .thenReturn(loadingView)

        //AND getContentCategories returns an empty list
        Mockito.`when`(categoryManager.getCategories())
            .thenReturn(Single.just(value(listOf())))

        mockUserAccount()

        // WHEN the view is attached
        eventDetailsPresenter.attach(view)
        eventDetailsPresenter.fetchData(true)

        val argument = argumentCaptor<MutableList<BaseEventDetailsAdapterItem>>()
        verify(zipRequestsUtil).issueApiCall(any<GetEventDetailsRequest>())
        verify(view).showEventDetails(argument.capture())

        //first item is Overview item and its categoryUrl is an empty string
        Assertions.assertThat(argument.firstValue[0] is EventOverviewAdapterItem).isTrue()
        val item = argument.firstValue[0] as EventOverviewAdapterItem
        Assertions.assertThat(item.eventName).isEqualTo(result.name)
        Assertions.assertThat(item.categoryUrl).isEqualTo(null)
        Assertions.assertThat(item.eventStatus)
            .isEqualTo(eventStatusHelper.getRestrictedEventStatus(result, resultVenue))
    }

    @Test
    fun `upon success in finding the event's category in the categoryManager, the overview item's categoryUrl is non-empty`() {
        //Given event details of a non-cinema event
        eventDetailsPresenter.eventId = "id"

        val resultVenue = Venue(
            _name = "VENUE NAME",
            _venueColors = VenueColors(house = "#000000"),
            _timeZone = "Europe/London"
        )
        resultVenue.id = "1"
        val resource = EventResource(resourceType = "MEMBER_EVENTS")
        resource.id = "MEMBER_EVENTS"
        val booking = EventBooking(
            state = BookingState.UNCONFIRMED.name,
            bookingType = BookingType.GUEST_LIST.name
        ).apply { id = "1" }
        val result = Event(
            venue = HasOne(resultVenue), category = "family", resource = HasOne(resource),
            booking = HasOne(booking)
        )
        result.id = "id"
        val document = ObjectDocument(result)
        document.addInclude(resultVenue)
        document.addInclude(resource)
        document.addInclude(booking)
        result.document = document

        val category = mockCategory(result)

        val venueMap = mapOf(Pair("1", resultVenue))


        every { venueRepo.venues() } returns VenueList(listOf(resultVenue))
        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetEventDetailsRequest>()))
            .thenReturn(Single.just(value(result)))
        Mockito.`when`(view.loadingView)
            .thenReturn(loadingView)

        //AND getContentCategories's return value contains the event's category
        Mockito.`when`(categoryManager.getCategories())
            .thenReturn(Single.just(value(listOf(category))))

        mockUserAccount()

        // WHEN the view is attached
        eventDetailsPresenter.attach(view)
        eventDetailsPresenter.fetchData(true)

        val argument = argumentCaptor<MutableList<BaseEventDetailsAdapterItem>>()
        verify(zipRequestsUtil).issueApiCall(any<GetEventDetailsRequest>())
        verify(view).showEventDetails(argument.capture())

        //first item is Overview item and its categoryUrl is a non-empty string (the category's icon value)
        Assertions.assertThat(argument.firstValue[0] is EventOverviewAdapterItem).isTrue()
        val item = argument.firstValue[0] as EventOverviewAdapterItem
        Assertions.assertThat(item.eventName).isEqualTo(result.name)
        Assertions.assertThat(item.categoryUrl).isEqualTo(category.icon?.png)
        Assertions.assertThat(item.eventStatus)
            .isEqualTo(eventStatusHelper.getRestrictedEventStatus(result, resultVenue))
    }

    @Test
    fun `when viewing event details of an open event with booking that does not allow more guests, hide stepper`() {
        //GIVEN event details of an event
        eventDetailsPresenter.eventId = "id"

        val resultVenue = Venue(
            _name = "VENUE NAME",
            _venueColors = VenueColors(house = "#000000"),
            _timeZone = "Europe/London"
        )
        resultVenue.id = "1"
        val eventBooking = EventBooking(
            state = BookingState.CONFIRMED.name,
            bookingType = BookingType.GUEST_LIST.name,
            numberOfGuests = 3
        )
        eventBooking.id = "1"
        val resource = EventResource(resourceType = "MEMBER_EVENTS")
        resource.id = "MEMBER_EVENTS"
        val result = Event(
            venue = HasOne(resultVenue),
            category = "family",
            maxGuestsPerBooking = 3,
            booking = HasOne(eventBooking),
            resource = HasOne(resource),
            cancellableUntil = Date()
        )
        result.id = "id"
        val document = ObjectDocument(result)
        document.addInclude(resultVenue)
        document.addInclude(eventBooking)
        document.addInclude(resource)
        result.document = document

        val category = mockCategory(result)

        val venueMap = mapOf(Pair("1", resultVenue))


        every { venueRepo.venues() } returns VenueList(listOf(resultVenue))
        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetEventDetailsRequest>()))
            .thenReturn(Single.just(value(result)))
        Mockito.`when`(view.loadingView)
            .thenReturn(loadingView)

        //AND getContentCategories's return value contains the event's category
        Mockito.`when`(categoryManager.getCategories())
            .thenReturn(Single.just(value(listOf(category))))

        mockUserAccount()

        //WHEN the view is attached
        eventDetailsPresenter.attach(view)
        eventDetailsPresenter.fetchData(true)

        //THEN the stepper is hidden
        verify(zipRequestsUtil).issueApiCall(any<GetEventDetailsRequest>())
        verify(view).hideBookingStepper()
    }

    @Test
    fun `when delete booking succeeds, update view`() {
        //GIVEN event details of an event
        eventDetailsPresenter.eventId = "id"

        val resultVenue = Venue(
            _name = "VENUE NAME",
            _venueColors = VenueColors(house = "#000000"),
            _timeZone = "Europe/London"
        )
        resultVenue.id = "1"
        val eventBooking = EventBooking(
            state = BookingState.CONFIRMED.name,
            bookingType = BookingType.GUEST_LIST.name,
            numberOfGuests = 3
        )
        eventBooking.id = "1"
        val resource = EventResource(resourceType = "MEMBER_EVENTS")
        resource.id = "MEMBER_EVENTS"
        val result = Event(
            venue = HasOne(resultVenue),
            category = "family",
            maxGuestsPerBooking = 3,
            booking = HasOne(eventBooking),
            resource = HasOne(resource),
            priceCents = 0,
            priceCurrency = "CAD",
            cancellableUntil = Date()
        )
        result.id = "id"
        val document = ObjectDocument(result)
        document.addInclude(resultVenue)
        document.addInclude(eventBooking)
        document.addInclude(resource)
        result.document = document

        val category = mockCategory(result)

        val venueMap = mapOf(Pair("1", resultVenue))


        every { venueRepo.venues() } returns VenueList(listOf(resultVenue))
        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetEventDetailsRequest>()))
            .thenReturn(Single.just(value(result)))
        Mockito.`when`(view.loadingView)
            .thenReturn(loadingView)
        Mockito.`when`(categoryManager.getCategories())
            .thenReturn(Single.just(value(listOf(category))))
        Mockito.`when`(zipRequestsUtil.issueApiCall(any<DeleteBookingRequest>()))
            .thenReturn(Single.just(empty()))
        mockUserAccount()

        eventDetailsPresenter.attach(view)
        eventDetailsPresenter.fetchData(true)
        verify(zipRequestsUtil).issueApiCall(any<GetEventDetailsRequest>())

        //WHEN deleting the booking
        eventDetailsPresenter.deleteBooking()

        //THEN delete call is issued and view updated
        verify(zipRequestsUtil).issueApiCall(any<DeleteBookingRequest>())
        verify(view, times(2)).showEventDetails(any())
    }

    @Test
    fun `when delete guest succeeds, update view`() {
        //GIVEN event details of an event
        eventDetailsPresenter.eventId = "id"

        val resultVenue = Venue(
            _name = "VENUE NAME",
            _venueColors = VenueColors(house = "#000000"),
            _timeZone = "Europe/London"
        )
        resultVenue.id = "1"
        val eventBooking = EventBooking(
            state = BookingState.CONFIRMED.name,
            bookingType = BookingType.GUEST_LIST.name,
            numberOfGuests = 3
        )
        eventBooking.id = "1"
        val resource = EventResource(resourceType = "MEMBER_EVENTS")
        resource.id = "MEMBER_EVENTS"
        val result = Event(
            venue = HasOne(resultVenue),
            category = "family",
            maxGuestsPerBooking = 3,
            booking = HasOne(eventBooking),
            resource = HasOne(resource),
            cancellableUntil = Date()
        )
        result.id = "id"
        val document = ObjectDocument(result)
        document.addInclude(resultVenue)
        document.addInclude(eventBooking)
        document.addInclude(resource)
        result.document = document

        val category = mockCategory(result)

        val venueMap = mapOf(Pair("1", resultVenue))


        every { venueRepo.venues() } returns VenueList(listOf(resultVenue))
        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetEventDetailsRequest>()))
            .thenReturn(Single.just(value(result)))
        Mockito.`when`(view.loadingView)
            .thenReturn(loadingView)
        Mockito.`when`(categoryManager.getCategories())
            .thenReturn(Single.just(value(listOf(category))))
        Mockito.`when`(zipRequestsUtil.issueApiCall(any<PatchEventBookingRequest>()))
            .thenReturn(Single.just(value(eventBooking)))
        mockUserAccount()

        eventDetailsPresenter.attach(view)
        eventDetailsPresenter.fetchData(true)
        verify(zipRequestsUtil).issueApiCall(any<GetEventDetailsRequest>())

        //WHEN deleting a guest
        eventDetailsPresenter.bookEvent(1)

        //THEN delete call is issued and view updated
        verify(zipRequestsUtil).issueApiCall(any<PatchEventBookingRequest>())
        verify(view, times(2)).showEventDetails(any())
    }

    @Test
    fun `when join event succeeds, update view`() {
        //GIVEN event details of an event
        eventDetailsPresenter.eventId = "id"

        val eventBooking = EventBooking(
            state = BookingState.CONFIRMED.name,
            bookingType = BookingType.GUEST_LIST.name,
            numberOfGuests = 3
        )
        eventBooking.id = null

        val resultVenue = Venue(
            _name = "VENUE NAME",
            _venueColors = VenueColors(house = "#000000"),
            _timeZone = "Europe/London"
        )
        resultVenue.id = "1"
        val resource = EventResource(resourceType = "MEMBER_EVENTS")
        resource.id = "MEMBER_EVENTS"
        val result = Event(
            venue = HasOne(resultVenue),
            category = "family",
            maxGuestsPerBooking = 3,
            resource = HasOne(resource),
            cancellableUntil = Date(),
            booking = HasOne(eventBooking)
        )
        result.id = "id"
        val document = ObjectDocument(result)
        document.addInclude(resultVenue)
        document.addInclude(eventBooking)
        document.addInclude(resource)
        result.document = document


        val category = mockCategory(result)

        val venueMap = mapOf(Pair("1", resultVenue))

        every { venueRepo.venues() } returns VenueList(listOf(resultVenue))
        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetEventDetailsRequest>()))
            .thenReturn(Single.just(value(result)))
        Mockito.`when`(view.loadingView)
            .thenReturn(loadingView)
        Mockito.`when`(categoryManager.getCategories())
            .thenReturn(Single.just(value(listOf(category))))
        Mockito.`when`(zipRequestsUtil.issueApiCall(any<PostEventBookingRequest>()))
            .thenReturn(Single.just(value(eventBooking)))
        mockUserAccount()

        eventDetailsPresenter.attach(view)
        eventDetailsPresenter.fetchData(true)
        verify(zipRequestsUtil).issueApiCall(any<GetEventDetailsRequest>())

        //WHEN join an event
        eventDetailsPresenter.bookEvent(1)

        //THEN join call issued and view updated
        verify(zipRequestsUtil).issueApiCall(any<PostEventBookingRequest>())
        verify(view, times(2)).showEventDetails(any())
    }

    private fun assertAttributeItems(
        item: BaseEventDetailsAdapterItem, iconDrawableRes: Int,
        labelStringRes: Int? = null,
        description: String? = null,
        CTAStringRes: Int? = null
    ) {
        Assertions.assertThat(item is EventAttributeAdapterItem).isEqualTo(true)
        (item as EventAttributeAdapterItem).let {
            if (labelStringRes != null) Assertions.assertThat(item.labelStringRes)
                .isEqualTo(labelStringRes)
            if (description != null) Assertions.assertThat(item.description).isEqualTo(description)
            if (CTAStringRes != null) Assertions.assertThat(item.CTAStringRes)
                .isEqualTo(CTAStringRes)
        }
    }

    private fun assertCinemaSubDescription(
        item: BaseEventDetailsAdapterItem,
        attr: Int,
        value: String
    ) {
        Assertions.assertThat(item is EventDetailsSubDescriptionAdapterItem).isTrue()
        (item as EventDetailsSubDescriptionAdapterItem).let {
            Assertions.assertThat(it.value).isEqualTo(value)
            Assertions.assertThat(it.attrStringRes).isEqualTo(attr)
        }
    }

    private fun mockCategory(result: Event): EventCategory {
        val category = EventCategory(icon = Icon(png = "png"), eventTypes = listOf("MEMBER_EVENT"))
        category.id = result.category
        return category
    }

    private fun mockUserAccount(
        subscriptionType: SubscriptionType = SubscriptionType.LOCAL,
        gymMembership: GymMembership = GymMembership.NONE
    ) {
        Mockito.`when`(userManager.subscriptionType)
            .thenReturn(subscriptionType)
        Mockito.`when`(userManager.gymMembership)
            .thenReturn(gymMembership)
    }
}