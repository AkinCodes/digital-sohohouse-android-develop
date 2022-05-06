package com.sohohouse.seven.more.bookings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.more.bookings.recycler.BookingAdapterItem
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.models.Booking
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UpcomingBookingsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @MockK
    lateinit var apiService: BookingsRepo

    @MockK
    lateinit var factory: BookingItemsFactory

    @MockK
    lateinit var stringProvider: StringProvider

    @MockK
    lateinit var tracking: AnalyticsManager

    @MockK
    lateinit var userManager: UserManager

    lateinit var viewModel: UpcomingBookingsViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        // mock Log
//        mockkStatic(Log::class)
//        every { Log.d(any(), any()) } returns 0

        viewModel = UpcomingBookingsViewModel(
            apiService,
            factory,
            stringProvider,
            tracking,
            userManager,
            Dispatchers.Unconfined
        )

        every { userManager.subscriptionType } returns SubscriptionType.EVERY
    }

    @Test
    fun `when fetchBookings is called, viewmodel fetches and emits upcoming bookings`() =
        runBlocking {
            every { apiService.getUpcomingBookings() } returns value(
                Pair<List<Booking>, VenueList>(
                    mutableListOf(),
                    VenueList.empty()
                )
            )

            val dataObserver = mockk<Observer<List<BookingAdapterItem>>>()
            val slot = slot<List<BookingAdapterItem>>()
            val items = mutableListOf<BookingAdapterItem>()
            every { dataObserver.onChanged(capture(slot)) } answers { items.addAll(slot.captured) }
            viewModel.adapterItems.observeForever(dataObserver)

            val errorObserver = mockk<Observer<Any>>()
            viewModel.errorViewState.observeForever(errorObserver)

            val loadingObserver = mockk<Observer<LoadingState>>()
            viewModel.loadingState.observeForever(loadingObserver)
            every { loadingObserver.onChanged(any()) } just Runs

            viewModel.fetchBookings()

            verify(exactly = 1) { apiService.getUpcomingBookings() }
            verify(exactly = 1) { dataObserver.onChanged(any()) }
            verify(exactly = 0) { errorObserver.onChanged(any()) }
            verify(exactly = 1) { loadingObserver.onChanged(LoadingState.Loading) }
            verify(exactly = 1) { loadingObserver.onChanged(LoadingState.Idle) }

            assert(items.isNotEmpty())
        }

    @Test
    fun `on init viewmodel retrieves profile and if unsuccessful emits expected values`() =
        runBlocking {
            every { apiService.getUpcomingBookings() } returns Either.Error(ServerError.BAD_REQUEST)

            val dataObserver = mockk<Observer<List<BookingAdapterItem>>>()
            viewModel.adapterItems.observeForever(dataObserver)

            val errorObserver = mockk<Observer<Any>>()
            every { errorObserver.onChanged(any()) } just Runs

            viewModel.errorViewState.observeForever(errorObserver)

            viewModel.fetchBookings()

            verify(exactly = 1) { apiService.getUpcomingBookings() }
            verify(exactly = 0) { dataObserver.onChanged(any()) }
            verify(exactly = 1) { errorObserver.onChanged(any()) }
        }
}