package com.sohohouse.seven.more.housepreferences

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.verify
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.house.HouseRegion
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerChildItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerParentItem
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.Account
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.network.core.models.VenueColors
import com.sohohouse.seven.network.core.models.VenueIcons
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.reactivex.Single
import moe.banana.jsonapi2.HasMany
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApp::class)
class MoreHousePreferencesPresenterTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var accountInteractor: AccountInteractor

    @Mock
    lateinit var houseManager: HouseManager

    @Mock
    lateinit var userManager: UserManager

    @Mock
    lateinit var view: MoreHousePreferencesActivity

    @Mock
    lateinit var loadingView: LoadingView

    @Mock
    lateinit var analyticsManager: AnalyticsManager

    @MockK
    lateinit var venueRepo: VenueRepo

    lateinit var moreHousePreferencesPresenter: MoreHousePreferencesPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MockKAnnotations.init(this)
        moreHousePreferencesPresenter = MoreHousePreferencesPresenter(
            accountInteractor,
            houseManager,
            userManager,
            venueRepo,
            analyticsManager
        )
    }

    @Test
    fun `when view attached, fetch house preferences from houseManager`() {

        // GIVEN there are some venues
        val venue1 = Venue(_name = "1", _region = "a", _venueType = "HOUSE", _isTopLevel = true)
        venue1.id = "1"
        val venue2 = Venue(_name = "2", _region = "b", _venueType = "HOUSE", _isTopLevel = true)
        venue2.id = "2"
        val venue3 = Venue(_name = "3", _region = "a", _venueType = "HOUSE", _isTopLevel = true)
        venue3.id = "3"
        val venue4 = Venue(_name = "4", _region = "b", _venueType = "HOUSE", _isTopLevel = true)
        venue4.id = "4"
        val venue5 = Venue(_name = "5", _region = "b", _venueType = "HOUSE", _isTopLevel = true)
        venue5.id = "5"
        val venue6 = Venue(_name = "6", _region = "c", _venueType = "HOUSE", _isTopLevel = true)
        venue6.id = "6"
        val venue7 = Venue(_name = "7", _region = "a", _venueType = "HOUSE", _isTopLevel = true)
        venue7.id = "7"
        val venues = listOf(venue1, venue2, venue3, venue4, venue5, venue6, venue7)
        val account = Account()
        `when`(accountInteractor.getAccount())
            .thenReturn(Single.just(value(account)))
        `when`(view.loadingView).thenReturn(loadingView)
        every { venueRepo.venues() } returns VenueList(venues)
        every { venueRepo.updateFavouriteVenuesSingle(any()) } returns Single.just(
            value(
                VenueList(
                    venues
                )
            )
        )

        val selectedList = listOf("1", "4", "6", "7")
        val favouriteList = listOf(
            createRecyclerItemFromVenue(venue1),
            createRecyclerItemFromVenue(venue4),
            createRecyclerItemFromVenue(venue6),
            createRecyclerItemFromVenue(venue7)
        )
        val allList = listOf(
            LocationRecyclerParentItem(HouseRegion.EUROPE, listOf(), false),
            LocationRecyclerParentItem(HouseRegion.NORTH_AMERICA, listOf(), false),
            LocationRecyclerParentItem(HouseRegion.UK, listOf(), false)
        )
        `when`(
            houseManager.organizeHousesForLocationRecyclerView(
                venues,
                true,
                includeStudios = false
            )
        )
            .thenReturn(Triple(selectedList, favouriteList, allList))

        // WHEN the view is attached
        moreHousePreferencesPresenter.attach(view)

        // THEN the presenter gets the data and initialize the view

        io.mockk.verify { venueRepo.venues() }
        verify(view).onDataReady(favouriteList, allList)
        verify(view).enableApplyButton(true)
        assertThat(moreHousePreferencesPresenter.selectedList == selectedList).isTrue()
    }

    @Test
    fun `resetting selection resets the selected list and updates apply button state`() {
        // GIVEN house manager has local house
        setUpView()
        `when`(houseManager.getLocalHouseId())
            .thenReturn("house")
        `when`(userManager.localHouseId).thenReturn("house")

        // WHEN reset to default selection
        moreHousePreferencesPresenter.resetDefaultSelection()

        // THEN the presenter resets the selected list to just local house
        assertThat(moreHousePreferencesPresenter.selectedList.size == 1).isTrue()
        verify(view).enableApplyButton(true)
        verify(view).resetSelection("house")
    }

    @Test
    fun `when selection changes, the selected list and apply button state are updated`() {
        // GIVEN there are some selections
        setUpView()
        val selectedList = listOf("selected", "select")

        // WHEN selected list change
        moreHousePreferencesPresenter.onSelectedLocationsChanged(selectedList)

        // THEN the selected list and apply button state are updated
        assertThat(moreHousePreferencesPresenter.selectedList.size == 2).isTrue()
        verify(view).enableApplyButton(true)
    }

    @Test
    fun `when apply button is clicked, house manager updates venues and show success view if successful`() {
        // GIVEN there are some selected locations
        setUpView()
        val selectedList = listOf("selected", "select")
        moreHousePreferencesPresenter.selectedList = selectedList

        every { venueRepo.updateFavouriteVenuesSingle(selectedList) } returns Single.just(
            value(
                VenueList(Venue())
            )
        )

        // WHEN apply button is clicked
        moreHousePreferencesPresenter.onApplyButtonClicked()

        // THEN house manager's update call is called and view show success since the call succeeded
        io.mockk.verify { venueRepo.updateFavouriteVenuesSingle(selectedList) }
        verify(view).updateSuccess()
    }

    private fun createRecyclerItemFromVenue(venue: Venue): LocationRecyclerChildItem {
        return LocationRecyclerChildItem(venue.id, "", "", false)
    }

    private fun setUpView() {
        val venue = Venue(
            _venueColors = VenueColors("#000000", "#000000", "#000000"),
            venueIcons = VenueIcons("https://placebear.com/24/24", "", "", ""),
            _timeZone = TimeZone.getDefault().id,
            _name = "name"
        )
        venue.id = "venueId"
        val account = Account(favoriteVenuesResource = HasMany(venue))
        account.id = "accountId"
        `when`(accountInteractor.getAccount())
            .thenReturn(Single.just(value(account)))
        val venues = listOf(venue)
        `when`(view.loadingView)
            .thenReturn(loadingView)
        `when`(
            houseManager.organizeHousesForLocationRecyclerView(
                venues,
                true,
                includeStudios = false
            )
        )
            .thenReturn(Triple(listOf(), listOf(), listOf()))

        moreHousePreferencesPresenter.attach(view)
    }
}