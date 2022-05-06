package com.sohohouse.seven.guests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
import com.sohohouse.seven.BuildConfigManager
import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.captor
import com.sohohouse.seven.common.extensions.getDayAndMonthFormattedDate
import com.sohohouse.seven.common.mock
import com.sohohouse.seven.common.prefs.LocalVenueProvider
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(application = TestApp::class)
class NewGuestListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @MockK
    lateinit var analyticsManager: AnalyticsManager

    @MockK
    lateinit var guestListRepository: GuestListRepository

    @MockK
    lateinit var userManager: UserManager

    @MockK
    lateinit var featureFlags: FeatureFlags

    @MockK
    lateinit var localVenueProvider: LocalVenueProvider
    lateinit var guestListHelper: GuestListHelper

    @MockK
    lateinit var venueRepo: VenueRepo

    lateinit var viewModel: NewGuestListViewModel

    val testSelectedDateString = "1 January"
    val testDateSelection = Triple(2021, 1, 1)

    private val testOpenVenue = createTestVenue(isOpenForBusiness = true)
    private val testClosedVenue = createTestVenue(isOpenForBusiness = false)
    private val stringProvider = object : StringProvider {
        override fun getString(resId: Int, vararg params: String): String {
            return "Share message ${params[0]}"
        }

        override fun getString(resId: Int?): String {
            return ""
        }

        override fun getStringArray(resId: Int): Array<String> {
            return emptyArray()
        }
    }

    @Mock
    lateinit var buildConfigManager: BuildConfigManager

    val localVenue = Venue().apply { id = "SD" }

    fun createTestVenue(isOpenForBusiness: Boolean): Venue {
        val operatingHours = if (isOpenForBusiness)
            OperatingHours(
                periods = listOf(
                    Period(
                        venueOpen = VenueTime(0, "0000"),
                        venueClose = VenueTime(0, "2359")
                    )
                )
            )
        else
            OperatingHours()
        return Venue(
            _name = "Soho House Amsterdam",
            venueAddress = VenueAddress(locality = "Amsterdam", country = "Netherlands"),
            venueIcons = VenueIcons("", "", "darkIcon.png", ""),
            operatingHours = operatingHours
        ).apply { id = "AMS" }
    }

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        MockKAnnotations.init(this)
        every { userManager.subscriptionType } returns SubscriptionType.EVERY
        mockkStatic("com.sohohouse.seven.common.extensions.DateKt")
        every { any<Date>().getDayAndMonthFormattedDate() } returns "1 January"
        every { featureFlags.guestRegistration } returns true
        val defaultValue =
            Venue(_name = "GRS", _region = "a", _venueType = "HOUSE", _isTopLevel = true)
        defaultValue.id = "GRS"
        every { venueRepo.venues() } returns VenueList(listOf(defaultValue))

        every { userManager.subscriptionType } returns SubscriptionType.LOCAL

        every { localVenueProvider.localVenue } returns MutableLiveData(localVenue)

        guestListHelper = GuestListHelper(featureFlags, stringProvider, buildConfigManager)
        viewModel = NewGuestListViewModel(
            analyticsManager,
            guestListRepository,
            userManager,
            localVenueProvider,
            venueRepo,
            guestListHelper,
            Dispatchers.Unconfined
        )
    }


    @Test
    fun `viewmodel emits preselected location item on init for local member which cannot be changed`() =
        runBlockingTest {
            val houseItemObserver = mock<Observer<GuestListFormHouseItem?>>().also {
                viewModel.houseItem.observeForever(it)
            }

            val captor = captor<GuestListFormHouseItem>()
            verify(houseItemObserver).onChanged(captor.capture())
            val item = captor.value
            assertEquals(localVenue.id, item.id)
            assertFalse(item.enabled)
        }

    @Test
    fun `on date selected, viewmodel emits correct date item`() = runBlockingTest {
        val dateItemObserver = mock<Observer<GuestListFormDateItem?>>().also {
            viewModel.dateItem.observeForever(it)
        }

        val (year, month, day) = testDateSelection

        viewModel.onDateSelected(year, month, day)

        val captor = captor<GuestListFormDateItem>()

        verify(dateItemObserver, times(2)).onChanged(captor.capture())
        assertEquals(testSelectedDateString, captor.secondValue.dateString)
    }

    @Test
    fun `on house selected, viewmodel emits correct house item`() = runBlockingTest {
        val houseItemObserver = mock<Observer<GuestListFormHouseItem?>>().also {
            viewModel.houseItem.observeForever(it)
        }

        viewModel.onHouseSelected(testOpenVenue)

        val captor = captor<GuestListFormHouseItem?>()

        verify(houseItemObserver, times(2)).onChanged(captor.capture())
        assertEquals("AMS", captor.secondValue?.id)
        assertEquals("Amsterdam, Netherlands", captor.secondValue?.location)
        assertEquals("Soho House Amsterdam", captor.secondValue?.name)
        assertEquals("darkIcon.png", captor.secondValue?.iconUrl)
    }

    @Test
    fun `confirm button is only enabled when date and location have values`() = runBlockingTest {
        val observer = mock<Observer<Boolean>>().also {
            viewModel.submitEnabled.observeForever(it)
        }
        val captor = captor<Boolean>()

        viewModel.onHouseSelected(Venue().apply { id = "test" })
        val (year, month, day) = testDateSelection
        viewModel.onDateSelected(year, month, day)

        verify(observer, times(4)).onChanged(captor.capture())

        assertEquals(true, captor.firstValue)
        assertEquals(true, captor.secondValue)
        assertEquals(true, captor.thirdValue)
        assertEquals(true, captor.allValues[3])
    }

    @Test
    fun `viewmodel calls on repository to create new guestlist when submit clicked and emits navigation event on success`() =
        runBlockingTest {
            val testGuestListId = "testGuestListId"
            every {
                (guestListRepository.createGuestList(
                    any(),
                    any(),
                    any(),
                    any()
                ))
            } returns (value(GuestList().apply { id = testGuestListId }))
            every { userManager.membershipType } returns ""
            every { analyticsManager.logEventAction(any(), any()) } answers { Unit }

            val (year, month, day) = testDateSelection
            viewModel.onDateSelected(year, month, day)
            viewModel.onHouseSelected(testOpenVenue)

            val navigationObserver = mockk<Observer<String>>(relaxed = true).also {
                viewModel.navigateToGustListDetailsEvent.observeForever(it)
            }

            viewModel.onConfirmClick()

            verify { guestListRepository.createGuestList(any(), any(), any(), any()) }
            verify(exactly = 1) { navigationObserver.onChanged(testGuestListId) }
        }

    @Test
    fun `if house is closed for business viewmodel emits appropriate event on submit click`() =
        runBlockingTest {
            val testGuestListId = "testGuestListId"
            every {
                (guestListRepository.createGuestList(
                    any(),
                    any(),
                    any(),
                    any()
                ))
            } returns (value(GuestList().apply { id = testGuestListId }))

            val (year, month, day) = testDateSelection
            viewModel.onDateSelected(year, month, day)
            viewModel.onHouseSelected(testClosedVenue)

            val closedEventObserver = mockk<Observer<Any>>(relaxed = true).also {
                viewModel.houseClosedErrorEvent.observeForever(it)
            }

            viewModel.onConfirmClick()

            verify { guestListRepository wasNot called }
            verify { closedEventObserver.onChanged(any()) }
        }

}