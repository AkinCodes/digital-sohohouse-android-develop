package com.sohohouse.seven.more.bookings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.book.table.model.BookedTable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.isNextWeek
import com.sohohouse.seven.common.extensions.isThisWeek
import com.sohohouse.seven.common.extensions.isToday
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.more.bookings.recycler.*
import com.sohohouse.seven.network.core.models.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class UpcomingBookingsViewModel @Inject constructor(
    private val bookingsRepo: BookingsRepo,
    private val bookingItemsFactory: BookingItemsFactory,
    private val stringProvider: StringProvider,
    analyticsManager: AnalyticsManager,
    private val userManager: UserManager,
    dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    ErrorDialogViewModel by ErrorDialogViewModelImpl(),
    ErrorViewStateViewModel by ErrorViewStateViewModelImpl() {

    val adapterItems = MutableLiveData<MutableList<BookingAdapterItem>>()
    val bookedTableDetails = LiveEvent<BookedTable>()

    private var venues: VenueList = VenueList.empty()

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.UpcomingBookings.name)
    }

    fun fetchBookings() {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)

            bookingsRepo.getUpcomingBookings().fold(
                ifError = {
                    Timber.d(it.toString())
                    showErrorView()
                },
                ifValue = {
                    val (bookings, houses) = it
                    this@UpcomingBookingsViewModel.venues = houses
                    setEventDetailInfo(bookings.filterIsInstance(EventBooking::class.java))
                    val items = createPopulatedAdapterList(bookings, houses)
                    adapterItems.postValue(items)
                },
                ifEmpty = {}
            )

            setLoadingState(LoadingState.Idle)
        }
    }

    fun tableBookingSelected(item: TableBookingAdapterItem) {
        bookedTableDetails.postValue(
            BookedTable.from(
                item.tableBooking,
                venues.findById(item.tableBooking.venue?.parentId),
                stringProvider
            )
        )
    }

    private fun createPopulatedAdapterList(bookingList: List<Booking>, venueList: VenueList):
            MutableList<BookingAdapterItem> {
        val data: MutableList<BookingAdapterItem> = ArrayList()

        if (bookingList.isEmpty()) {
            val noBookingsMsg =
                if (userManager.subscriptionType == SubscriptionType.FRIENDS)
                    R.string.bookings_empty_body_friends
                else R.string.bookings_empty_body
            data.add(BookingEmptyAdapterItem(noBookingsMsg))
            return data
        }

        data.add(BookingsHeaderTextItem(stringProvider.getString(R.string.upcoming_bookings_supporting_text)))

        val items = HashMap<String?, Booking>()
        bookingList.iterator().forEach { booking ->
            booking.startsAt?.let {
                items[booking.bookingId] = booking
            }
        }
        val sortedDates = items.values.sortedBy { it.startsAt }
        sortedDates.forEachIndexed { index, booking ->

            val item = when (val event = items[booking.bookingId]) {
                is TableReservation -> {
                    bookingItemsFactory.buildTableBookingItem(event)
                }
                is EventBooking -> {
                    bookingItemsFactory.buildEventBookingItem(event, venueList)
                }
                else -> {
                    bookingItemsFactory.buildRoomBookingItem(event as RoomBooking, venueList)
                }
            }

            val previousItemDate = sortedDates.getOrNull(index - 1)?.startsAt

            getDateHeaderForItem(previousItemDate, booking.startsAt)?.let {
                data.add(it)
            }

            data.add(item)
        }

        return data
    }

    override fun reloadDataAfterError() {
        fetchBookings()
    }

    private fun setEventDetailInfo(bookings: List<EventBooking>) {
        for (booking in bookings) {
            val event = booking.event ?: Event()
            event.document.addInclude(booking)
        }
    }

    private fun getDateHeaderForItem(
        previousItemDate: Date?,
        thisItemDate: Date?
    ): UpcomingBookingsDateAdapterItem? {
        val thisDateLabel = getDateLabel(thisItemDate)
        val previousDateLabel = getDateLabel(previousItemDate)

        return if (thisDateLabel != null && thisDateLabel != previousDateLabel) {
            UpcomingBookingsDateAdapterItem(thisDateLabel)
        } else {
            null
        }
    }

    private fun getDateLabel(date: Date?): String? {
        return when {
            date == null -> null
            date.isToday() -> stringProvider.getString(R.string.today)
            date.isThisWeek() -> stringProvider.getString(R.string.this_week)
            date.isNextWeek() -> stringProvider.getString(R.string.next_week)
            else -> stringProvider.getString(R.string.in_the_distant_future)
        }
    }

    fun logRoomClickEvent(roomBooking: RoomBooking) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.BookingUpcomingTapRoom,
            AnalyticsManager.Bookings.getRoomBookingParams(roomBooking)
        )
    }

    fun logEventBookingClickEvent(eventBooking: EventBooking) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.BookingUpcomingTapEvent,
            AnalyticsManager.Bookings.getEventBookingParams(eventBooking)
        )
    }

    fun logTabSelectedAction() {
        analyticsManager.logEventAction(AnalyticsManager.Action.BookingTabUpcoming)
    }
}