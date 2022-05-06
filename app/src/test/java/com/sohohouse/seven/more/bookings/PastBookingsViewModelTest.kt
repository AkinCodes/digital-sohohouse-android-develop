package com.sohohouse.seven.more.bookings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.EmptyStringProvider
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.more.bookings.recycler.BookingAdapterItem
import com.sohohouse.seven.more.bookings.recycler.PastBookingsCollapsableMonthItem
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.joda.time.Interval
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class PastBookingsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @MockK
    lateinit var bookingsRepo: BookingsRepo

    @MockK
    lateinit var factory: BookingItemsFactory

    @MockK
    lateinit var tracking: AnalyticsManager

    @MockK
    lateinit var houseManager: HouseManager

    @MockK
    lateinit var userManager: UserManager

    lateinit var viewModel: PastBookingsViewModel

    private val stringProvider: StringProvider = EmptyStringProvider()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        // mock Log
//        mockkStatic(Log::class)
//        every { Log.d(any(), any()) } returns 0

        every {
            bookingsRepo.getBookings(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns value(Pair(listOf(), VenueList.empty()))
        every { userManager.subscriptionType } returns SubscriptionType.EVERY
    }

    @Test
    fun `on init viewmodel emits inital month items and then this month items`() {
        viewModel = PastBookingsViewModel(
            bookingsRepo,
            factory,
            stringProvider,
            userManager,
            tracking,
            Dispatchers.Unconfined
        )

        every { factory.getLast12MonthsHeaders() } returns dummyMonthHeaders

        val dataObserver = mockk<Observer<List<BookingAdapterItem>>>()
        val slot = slot<List<BookingAdapterItem>>()
        val items = mutableListOf<BookingAdapterItem>()
        every { dataObserver.onChanged(capture(slot)) } answers { items.addAll(slot.captured) }

        viewModel.adapterItems.observeForever(dataObserver)
        viewModel.fetchItems()

        verify(exactly = 2) { dataObserver.onChanged(any()) }
        assert(items.isNotEmpty())
    }

    private val dummyMonthHeaders: List<PastBookingsCollapsableMonthItem> = listOf(
        PastBookingsCollapsableMonthItem(Interval(0L, 0L), "This month"),
        PastBookingsCollapsableMonthItem(Interval(0L, 0L), "December"),
        PastBookingsCollapsableMonthItem(Interval(0L, 0L), "November"),
        PastBookingsCollapsableMonthItem(Interval(0L, 0L), "October"),
        PastBookingsCollapsableMonthItem(Interval(0L, 0L), "September"),
        PastBookingsCollapsableMonthItem(Interval(0L, 0L), "August"),
        PastBookingsCollapsableMonthItem(Interval(0L, 0L), "July"),
        PastBookingsCollapsableMonthItem(Interval(0L, 0L), "June"),
        PastBookingsCollapsableMonthItem(Interval(0L, 0L), "May"),
        PastBookingsCollapsableMonthItem(Interval(0L, 0L), "April"),
        PastBookingsCollapsableMonthItem(Interval(0L, 0L), "March"),
        PastBookingsCollapsableMonthItem(Interval(0L, 0L), "February")
    )


    @Test
    fun `on init viewmodel retrieves profile and if unsuccessful emits expected values`() {
        every {
            bookingsRepo.getBookings(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Either.Error(ServerError.BAD_REQUEST)
        every { factory.getLast12MonthsHeaders() } returns dummyMonthHeaders

        val dataObserver = mockk<Observer<List<BookingAdapterItem>>>()
        val slot = slot<List<BookingAdapterItem>>()
        val items = mutableListOf<BookingAdapterItem>()
        every { dataObserver.onChanged(capture(slot)) } answers { items.addAll(slot.captured) }

        viewModel = PastBookingsViewModel(
            bookingsRepo,
            factory,
            stringProvider,
            userManager,
            tracking,
            Dispatchers.Unconfined
        )
        viewModel.adapterItems.observeForever(dataObserver)

        val errorObserver = mockk<Observer<Any>>()
        viewModel.showGenericErrorDialogEvent.observeForever(errorObserver)

        viewModel.fetchItems()

        verify(exactly = 1) {
            bookingsRepo.getBookings(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        }
        verify(exactly = 1) { dataObserver.onChanged(any()) }
        verify(exactly = 0) { errorObserver.onChanged(any()) }

        assert(items.isNotEmpty())
    }
}