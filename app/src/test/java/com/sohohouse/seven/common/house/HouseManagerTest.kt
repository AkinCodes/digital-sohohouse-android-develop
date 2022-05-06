package com.sohohouse.seven.common.house

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockito_kotlin.any
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.prefs.LocalVenueProvider
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.network.core.models.*
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(application = TestApp::class)
class HouseManagerTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var zipRequestsUtil: ZipRequestsUtil

    @Mock
    lateinit var userManager: UserManager

    @Mock
    lateinit var accountInteractor: AccountInteractor

    @MockK
    lateinit var localVenueProvider: LocalVenueProvider

    @Mock
    lateinit var account: Account

    private lateinit var houseManager: HouseManager

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MockKAnnotations.init(this)
        houseManager = HouseManager(
            zipRequestsUtil,
            userManager,
            accountInteractor,
            localVenueProvider
        )
    }

    @Test
    fun `getLocalHouse returns local house from user manager`() {
        // GIVEN user manager has local house
        userManager.localHouseId = "house"

        // WHEN fetching local house from house manager
        val houseId = houseManager.getLocalHouseId()

        // THEN the manager returns local house
        assertThat(userManager.localHouseId == houseId).isTrue()
    }

    @Test
    fun `organizeHousesForLocationRecyclerView organizes venues into recyclerView items`() {
        // GIVEN we have some venues
        val venue1 = Venue(_name = "1", _region = "WORLDWIDE", _venueType = "HOUSE")
        venue1.id = "1"
        val venue2 = Venue(_name = "2", _region = "NORTH_AMERICA", _venueType = "HOUSE")
        venue2.id = "2"
        val venue3 = Venue(_name = "3", _region = "WORLDWIDE", _venueType = "HOUSE")
        venue3.id = "3"
        val venue4 = Venue(_name = "4", _region = "NORTH_AMERICA", _venueType = "HOUSE")
        venue4.id = "4"
        val venue5 = Venue(_name = "5", _region = "NORTH_AMERICA", _venueType = "HOUSE")
        venue5.id = "5"
        val venue6 = Venue(_name = "6", _region = "EUROPE", _venueType = "HOUSE")
        venue6.id = "6"
        val venue7 = Venue(_name = "7", _region = "WORLDWIDE", _venueType = "HOUSE")
        venue7.id = "7"
        val venues = listOf(venue1, venue2, venue3, venue4, venue5, venue6, venue7)

        every { localVenueProvider.localVenue } returns MutableLiveData(Venue().apply {
            id = userManager.localHouseId
        })
        `when`(userManager.favouriteHouses)
            .thenReturn(listOf("1", "4", "6", "7"))

        // WHEN organize the data
        val result =
            houseManager.organizeHousesForLocationRecyclerView(venues, true, includeStudios = false)
        val selectedList = result.first
        val myHouseList = result.second
        val allRegionsList = result.third
        val allHousesList = allRegionsList.flatMap { it.childList }

        // THEN house manager returns organized data for selected, favourite, and house list
        assertThat(selectedList == myHouseList.map { it.id }).isTrue()
        assertThat(selectedList.size == 4).isTrue()
        assertThat(allRegionsList.size == 3).isTrue()
        Assert.assertEquals(7, allHousesList.size)
    }


    @Test
    fun `organizeHousesForLocationRecyclerView organizes venues into recyclerView items for accessible non-CWH, open houses only`() {
        val closedHouseHours = OperatingHours(
            periods = listOf(
                Period(
                    venueOpen = VenueTime(_day = 0, time = "0000"),
                    venueClose = VenueTime(_day = 0, time = "0000")
                )
            )
        )
        val openHouseHours = OperatingHours(
            periods = listOf(
                Period(
                    venueOpen = VenueTime(_day = 0, time = "0000"),
                    venueClose = VenueTime(_day = 0, time = "2359")
                )
            )
        )

        // GIVEN we have some venues
        val venue1 = Venue(
            _name = "1",
            _region = "WORLDWIDE",
            _venueType = "HOUSE",
            operatingHours = openHouseHours
        )
        venue1.id = "1"
        val venue2 = Venue(
            _name = "2",
            _region = "NORTH_AMERICA",
            _venueType = "HOUSE",
            operatingHours = closedHouseHours
        )
        venue2.id = "2"
        val venue3 = Venue(
            _name = "3",
            _region = "WORLDWIDE",
            _venueType = "HOUSE",
            operatingHours = openHouseHours
        )
        venue3.id = "3"
        val venue4 = Venue(
            _name = "4",
            _region = "NORTH_AMERICA",
            _venueType = "HOUSE",
            operatingHours = openHouseHours
        )
        venue4.id = "4"
        val venue5 = Venue(
            _name = "5",
            _region = "NORTH_AMERICA",
            _venueType = "HOUSE",
            operatingHours = openHouseHours
        )
        venue5.id = "5"
        val venue6 = Venue(
            _name = "6",
            _region = "EUROPE",
            _venueType = "HOUSE",
            operatingHours = openHouseHours
        )
        venue6.id = "6"
        val venue7 = Venue(
            _name = "7",
            _region = "WORLDWIDE",
            _venueType = "HOUSE",
            operatingHours = openHouseHours
        )
        venue7.id = "7"
        val venue8 = Venue(
            _name = "8",
            _region = "WORLDWIDE",
            _venueType = "CWH",
            operatingHours = openHouseHours
        )
        venue7.id = "8"
        val venues = listOf(venue1, venue2, venue3, venue4, venue5, venue6, venue7, venue8)

        every { localVenueProvider.localVenue } returns MutableLiveData(Venue().apply {
            id = userManager.localHouseId
        })
        `when`(userManager.favouriteHouses)
            .thenReturn(listOf("1", "4", "6"))

        `when`(accountInteractor.canAccess(any<Venue>())).thenReturn(true)
        `when`(accountInteractor.canAccess(venue7)).thenReturn(false)


        // WHEN organize the data
        val result = houseManager.organizeHousesForLocationRecyclerView(
            venues,
            hasDisabledState = false,
            includeCWH = false,
            includeAccessibleVenuesOnly = true,
            includeOpenHousesOnly = true,
            includeStudios = false
        )
        val allRegionsList = result.third

        val allHousesList = allRegionsList.flatMap { it.childList }

        Assert.assertEquals(5, allHousesList.size)
    }
}