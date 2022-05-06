package com.sohohouse.seven.guests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.verify
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.captor
import com.sohohouse.seven.common.extensions.toDate
import com.sohohouse.seven.common.mock
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.guests.GuestsTestHelper.mockGuestList
import com.sohohouse.seven.guests.list.GuestListIndexViewModel
import com.sohohouse.seven.guests.list.GuestListItem
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.GuestList
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.threeten.bp.LocalDateTime
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(application = TestApp::class)
class GuestListIndexViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var analyticsManager: AnalyticsManager

    @Mock
    lateinit var guestListRepository: GuestListRepository

    @MockK
    lateinit var venueRepo: VenueRepo

    @Mock
    lateinit var userManager: UserManager

    val viewModel by lazy {
        GuestListIndexViewModel(
            analyticsManager,
            guestListRepository,
            venueRepo,
            userManager,
            Dispatchers.Unconfined
        )
    }

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MockKAnnotations.init(this)
    }

    @Test
    fun `viewmodel emits no guest invitation item`() = runBlockingTest {
        `when`(guestListRepository.getGuestLists()).thenReturn(value(emptyList()))

        every { venueRepo.venues() } returns VenueList.empty()
        val itemsObserver = mock<Observer<List<GuestListItem>>>().also {
            viewModel.items.observeForever(it)
        }

        viewModel.getGuestLists()

        val captor = captor<List<GuestListItem>>()
        verify(itemsObserver).onChanged(captor.capture())
        assertEquals(captor.value[0], GuestListItem.DescriptionItem)
        assertEquals(captor.value.none { it is GuestListItem.ListHeaderItem }, true)
    }

    @Test
    fun `viewmodel emits a guest list item`() = runBlockingTest {
        val guestlist = mockGuestList(date = Date())
        every { venueRepo.venues() } returns VenueList.empty()
        `when`(guestListRepository.getGuestLists()).thenReturn(value(listOf(guestlist)))

        val itemsObserver = mock<Observer<List<GuestListItem>>>().also {
            viewModel.items.observeForever(it)
        }

        viewModel.getGuestLists()

        val captor = captor<List<GuestListItem>>()
        verify(itemsObserver).onChanged(captor.capture())
        assertEquals(captor.value[0], GuestListItem.DescriptionItem)
        assertEquals(captor.value[1], GuestListItem.ListHeaderItem)
        assertEquals(captor.value.count { it is GuestListItem.GuestInvitationItem }, 1)

        val item =
            captor.value.firstOrNull { it is GuestListItem.GuestInvitationItem } as? GuestListItem.GuestInvitationItem
        assertEquals(item?.id, guestlist.id)
        assertEquals(item?.date, guestlist.date)
    }

    @Test
    fun `viewmodel sorts guest list items by the date and index`() {
        val list = mockGuestLists()
        every { venueRepo.venues() } returns VenueList.empty()
        `when`(guestListRepository.getGuestLists()).thenReturn(value(list))

        val itemsObserver = mock<Observer<List<GuestListItem>>>().also {
            viewModel.items.observeForever(it)
        }

        viewModel.getGuestLists()

        val captor = captor<List<GuestListItem>>()
        verify(itemsObserver).onChanged(captor.capture())
        val items = captor.value.filterIsInstance<GuestListItem.GuestInvitationItem>()
        assertEquals(items.size, list.size)
        assertEquals(items[0].id, "10")
        assertEquals(items[1].id, "1")
        assertEquals(items[2].id, "7")
        assertEquals(items[3].id, "3")
        assertEquals(items[4].id, "5")
        assertEquals(items[5].id, "9")
        assertEquals(items[6].id, "13")
        assertEquals(items[7].id, "11")
        assertEquals(items[8].id, "12")
        assertEquals(items[9].id, "8")
        assertEquals(items[10].id, "4")
        assertEquals(items[11].id, "6")
        assertEquals(items[12].id, "2")
    }


    private fun mockGuestLists(): List<GuestList> {
        return mutableListOf<GuestList>().apply {
            val dateTime = LocalDateTime.now()
            this.add(mockGuestList("1", date = dateTime.toDate()))
            this.add(mockGuestList("2", date = dateTime.plusMonths(1).toDate()))
            this.add(mockGuestList("3", date = dateTime.plusDays(1).toDate()))
            this.add(mockGuestList("4", date = dateTime.plusWeeks(1).plusDays(1).toDate()))
            this.add(mockGuestList("5", date = dateTime.plusDays(1).toDate()))
            this.add(mockGuestList("6", date = dateTime.plusWeeks(2).toDate()))
            this.add(mockGuestList("7", date = dateTime.plusSeconds(1).toDate()))
            this.add(mockGuestList("8", date = dateTime.plusWeeks(1).toDate()))
            this.add(mockGuestList("9", date = dateTime.plusDays(1).toDate()))
            this.add(mockGuestList("10", date = dateTime.minusSeconds(1).toDate()))
            this.add(mockGuestList("11", date = dateTime.plusDays(2).toDate()))
            this.add(mockGuestList("12", date = dateTime.plusDays(6).toDate()))
            this.add(mockGuestList("13", date = dateTime.plusDays(1).toDate()))
        }
    }
}