package com.sohohouse.seven.more.bookings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.more.bookings.recycler.*
import com.sohohouse.seven.network.core.models.Booking
import com.sohohouse.seven.network.core.models.EventBooking
import com.sohohouse.seven.network.core.models.RoomBooking
import com.sohohouse.seven.network.core.models.TableReservation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class PastBookingsViewModel @Inject constructor(
    val bookingsRepo: BookingsRepo,
    val bookingItemsFactory: BookingItemsFactory,
    val stringProvider: StringProvider,
    private val userManager: UserManager,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    ErrorDialogViewModel by ErrorDialogViewModelImpl() {

    val adapterItems = MutableLiveData<MutableList<BookingAdapterItem>>()
    private val bookingsMap = HashMap<Any, List<BookingAdapterItem>>()
    val itemChangeEvent = LiveEvent<ItemChangeEvent>()

    fun fetchItems() {
        val monthHeaders = bookingItemsFactory.getLast12MonthsHeaders()
        val thisMonthHeader = monthHeaders.first()

        adapterItems.value = ArrayList<BookingAdapterItem>(monthHeaders).apply {
            val copy =
                if (userManager.subscriptionType == SubscriptionType.FRIENDS) R.string.past_bookings_supporting_text_friends else R.string.past_bookings_supporting_text
            add(0, BookingsHeaderTextItem(stringProvider.getString(copy)))
        }
        getBookingsRequestForMonth(thisMonthHeader)
    }

    private fun getBookingsRequestForMonth(monthItem: PastBookingsCollapsableMonthItem) {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            val monthStart = monthItem.interval.start.toDate()
            bookingsRepo.getBookings(
                eventsStartsDateFrom = monthStart,
                roomsStartDateFrom = monthStart,
                startsAtTo = monthItem.interval.end.toDate(),
                sortOrder = BookingsRepo.SortOrder.DESC,
                tablesStartDateFrom = monthStart
            )
                .fold(
                    {
                        Timber.d(it.toString())
                        toggleMonthCollapsedState(monthItem, collapsed = true)
                    },
                    {
                        val (bookings, houses) = it
                        val newItems = createPopulatedAdapterList(bookings, houses)
                        insertItemsAtMonth(monthItem, newItems)
                    },
                    {}
                )
            setLoadingState(LoadingState.Idle)
        }
    }

    private fun toggleMonthCollapsedState(
        monthItem: PastBookingsCollapsableMonthItem,
        collapsed: Boolean
    ) {
        monthItem.collapsed = collapsed
        (adapterItems.value ?: return).indexOf(monthItem).takeIf { index -> index != -1 }
            ?.let { safeIndex ->
                itemChangeEvent.postValue(ItemChangeEvent(safeIndex))
            }
    }

    private fun insertItemsAtMonth(
        monthITem: PastBookingsCollapsableMonthItem,
        bookings: MutableList<BookingAdapterItem>
    ) {
        val items = ArrayList(adapterItems.value ?: return)

        items.addAll(items.indexOf(monthITem) + 1, bookings)
        adapterItems.postValue(items)

        bookingsMap[monthITem.key] = bookings
    }

    private fun createPopulatedAdapterList(bookingsList: List<Booking>, venueList: VenueList):
            MutableList<BookingAdapterItem> {
        val data: MutableList<BookingAdapterItem> = ArrayList()

        if (bookingsList.isEmpty()) {
            data.add(PastBookingsEmptyMonthAdapterItem)
            return data
        }

        bookingsList.iterator().forEach { booking ->
            data.add(
                when (booking) {
                    is EventBooking -> {
                        bookingItemsFactory.buildEventBookingItem(
                            booking,
                            venueList,
                            includeStatus = false
                        )
                    }
                    is TableReservation -> {
                        bookingItemsFactory.buildTableBookingItem(booking)
                    }
                    else -> {
                        bookingItemsFactory.buildRoomBookingItem(booking as RoomBooking, venueList)
                    }
                }
            )
        }

        return data
    }

    fun onMonthHeaderClick(item: PastBookingsCollapsableMonthItem) {
        if (item.collapsed) {   //show items
            showBookingsForMonth(item)
        } else {    //hide items
            hideBookingsForMonth(item)
        }
        toggleMonthCollapsedState(item, !item.collapsed)
    }

    private fun hideBookingsForMonth(item: PastBookingsCollapsableMonthItem) {
        val items = ArrayList(adapterItems.value ?: return)

        val existingItems = bookingsMap[item.key].orEmpty()
        val bookings = ArrayList(existingItems.filter {
            it.itemType in arrayOf(
                BookingAdapterItemType.EVENT_BOOKING,
                BookingAdapterItemType.ROOM_BOOKING
            )
        })
        if (bookings.isNotEmpty()) {
            bookings.forEach { booking -> items.removeAll { booking.key == it.key } }
        } else {
            val itemIndex =
                items.indexOfFirst { it is PastBookingsCollapsableMonthItem && it.key == item.key } + 1
            if (itemIndex < items.size && items[itemIndex] !is PastBookingsCollapsableMonthItem) {
                items.removeAt(itemIndex)
            }
        }

        adapterItems.postValue(items)
    }

    private fun showBookingsForMonth(item: PastBookingsCollapsableMonthItem) {
        val existingData = bookingsMap[item.key]
        if (existingData == null) {
            getBookingsRequestForMonth(item)
        } else {
            insertItemsAtMonth(item, ArrayList(existingData))
        }
    }

    fun reload() {
        fetchItems()
    }

    fun logEventBookingClickEvent(eventBooking: EventBooking) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.BookingHistoryTapEvent,
            AnalyticsManager.Bookings.getEventBookingParams(eventBooking)
        )
    }

    fun logRoomBookingClickEvent(roomBooking: RoomBooking) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.BookingHistoryTapRoom,
            AnalyticsManager.Bookings.getRoomBookingParams(roomBooking)
        )
    }

    override fun onScreenViewed() {
        super.onScreenViewed()
        setScreenNameInternal(AnalyticsManager.Screens.PastBookings.name)
    }
}
