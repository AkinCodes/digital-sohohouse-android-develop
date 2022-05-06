package com.sohohouse.seven.home.houseboard.viewmodels

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.LiveEvent
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.book.table.model.BookedTable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.deeplink.DeeplinkViewModel
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.prefs.LocalVenueProvider
import com.sohohouse.seven.common.user.SubscriptionType.*
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.DateUtils
import com.sohohouse.seven.common.utils.ErrorInteractor
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.guests.GuestListHelper
import com.sohohouse.seven.home.houseboard.items.*
import com.sohohouse.seven.more.bookings.BookingItemsFactory
import com.sohohouse.seven.more.bookings.BookingsRepo
import com.sohohouse.seven.more.bookings.recycler.TableBookingAdapterItem
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.chat.ChatChannelsRepo
import com.sohohouse.seven.network.chat.ChatConnectionRepo
import com.sohohouse.seven.network.core.models.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class HouseBoardViewModel @Inject constructor(
    private val houseManager: HouseManager,
    private val bookingsRepo: BookingsRepo,
    private val bookingItemsFactory: BookingItemsFactory,
    private val errorInteractor: ErrorInteractor,
    private val stringProvider: StringProvider,
    private val accountInteractor: AccountInteractor,
    private val userManager: UserManager,
    private val guestListHelper: GuestListHelper,
    private val venueRepo: VenueRepo,
    private val localVenueProvider: LocalVenueProvider,
    private val connectionRepository: ChatConnectionRepo,
    private val channelRepository: ChatChannelsRepo,
    analyticsManager: AnalyticsManager,
    deeplinkViewModel: DeeplinkViewModel,
    dispatcher: CoroutineDispatcher,
    notificationViewModel: NotificationViewModel
) : BaseViewModel(analyticsManager, dispatcher),
    DeeplinkViewModel by deeplinkViewModel,
    NotificationViewModel by notificationViewModel,
    Loadable.ViewModel by Loadable.ViewModelImpl() {

    companion object {
        const val UPCOMING_BOOKINGS_MAX_ITEMS = 7
        const val BOOK_BEDROOM_ACTION = "BOOK_BEDROOM_ACTION"
        const val BOOK_CINEMA_TICKETS_ACTION = "BOOK_CINEMA_TICKETS_ACTION"
        const val VIEW_MEMBERSHIP_CARD_ACTION = "VIEW_MEMBERSHIP_CARD_ACTION"
        const val VIEW_ALL_BENEFITS_ACTION = "VIEW_ALL_BENEFITS_ACTION"
    }

    val localVenueSlug: String? get() = _localVenue.value?.slug

    private var upcomingBooking: UpcomingBookingsContainerItem? = null

    private var allVenues: VenueList = VenueList.empty()

    private var userAccount: Account? = null

    private val _items = MutableLiveData<List<Any>>()

    private val _localVenue = MutableLiveData<Venue>()

    private val _intent = MutableLiveData<Intent>()

    private val _openCurrentHouseEvent = LiveEvent<Venue>()

    private val _openTableBooking = LiveEvent<BookedTable>()

    val openTableBooking: LiveData<BookedTable>
        get() = _openTableBooking

    val openCurrentHouseEvent: LiveEvent<Venue>
        get() = _openCurrentHouseEvent

    val items: LiveData<List<Any>>
        get() = _items

    val localVenue: LiveData<Venue>
        get() = _localVenue

    val intent: LiveData<Intent>
        get() = _intent

    private val _invitedChannelsLiveData = MutableLiveData<List<NotificationItem>>()
    val invitedChannelsLiveData: LiveData<List<NotificationItem>>
        get() = _invitedChannelsLiveData

    init {
        localVenueProvider.localVenue.observeForever(::onLocalVenueFetched)
        if (userManager.subscriptionType != FRIENDS) {
            viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
                connectionRepository.connect(userManager.getMiniProfileForSB())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        clearNotificationJobs()
    }

    fun load() {
        viewModelScope.launch(viewModelContext) {
            fetchUpcomingBookings()
            fetchAccountAndVenues()
        }
    }

    private fun onLocalVenueFetched(venue: Venue) {
        _localVenue.postValue(venue)
    }

    private fun onVenuesFetched(triple: Triple<Venue?, VenueList, Account>) {
        val (localVenue, allVenues, userAccount) = triple
        this.allVenues = allVenues
        this.userAccount = userAccount

        if (localVenue != null) {
            _localVenue.postValue(localVenue)
            _items.postValue(
                createDisplayList(
                    localVenue,
                    userAccount,
                    allVenues,
                    notifications.value,
                    invitedChannelsLiveData.value
                )
            )
        }
    }

    private fun fetchUpcomingBookings() {
        bookingsRepo.getUpcomingBookings().fold(
            ifError = { onError(it) },
            ifValue = { onBookingFetched(it) },
            ifEmpty = {}
        )
    }

    private fun fetchAccountAndVenues() {
        val triple = value(
            Triple(
                localVenueProvider.localVenue.value.let { Either.Value(it) },
                value(venueRepo.venues()),
                accountInteractor.getCompleteAccountV2()
            )
        )
        errorInteractor.tripError(triple).fold(
            ifError = { onError(it) },
            ifValue = { onVenuesFetched(it) },
            ifEmpty = {}
        )
    }

    fun onNewNotificationItems() {
        _items.postValue(
            createDisplayList(
                _localVenue.value,
                userAccount,
                allVenues,
                notifications.value,
                invitedChannelsLiveData.value
            )
        )
    }

    private fun onBookingFetched(pair: Pair<List<Booking>, VenueList>) {
        _localVenue.value ?: return
        val (bookings, houses) = pair
        upcomingBooking = buildUpcomingBookingsCarouselItem(bookings, houses)
    }

    private fun onError(error: ServerError) {
        Timber.d(error.toString())
    }

    private fun createDisplayList(
        venue: Venue?,
        userAccount: Account?,
        allVenues: VenueList,
        notifications: List<NotificationItem>?,
        invitations: List<NotificationItem>?
    ): List<Any> {
        if (venue == null || userAccount == null) return emptyList()

        return mutableListOf<Any>().also { items ->
            when (userManager.subscriptionType) {
                FRIENDS -> items.addAll(buildFriendsItems(userAccount, notifications))
                EVERY, EVERY_PLUS, LOCAL, CWH -> items.addAll(
                    buildItems(
                        userAccount,
                        venue,
                        allVenues,
                        notifications,
                        invitations
                    )
                )
                else -> {
                }
            }
        }
    }

    private fun buildItems(
        userAccount: Account,
        venue: Venue?,
        allVenues: VenueList,
        notifications: List<NotificationItem>?,
        invitations: List<NotificationItem>?
    ): List<Any> {
        if (venue == null) return emptyList()

        return mutableListOf<Any?>().apply {
            notifications?.let { addAll(it) }
            invitations?.let { addAll(it) }
            if (upcomingBooking != null) add(upcomingBooking)
            add(
                if (guestListHelper.canInviteGuests(
                        userAccount,
                        userManager.subscriptionType,
                        allVenues
                    )
                ) InviteGuestsItem else null
            )
            add(
                if (!venue.isCwh) {
                    HoursDisplayItem(
                        venue.name,
                        getHouseHoursLabel(stringProvider, venue),
                        getTodayOperatingHours(stringProvider, venue),
                        stringProvider.getString(R.string.membership_card_cta)
                    )
                } else {
                    DarkButtonItem(
                        stringProvider.getString(R.string.membership_card_cta),
                        VIEW_MEMBERSHIP_CARD_ACTION
                    )
                }
            )
            add(
                SecondaryButtonItem(
                    stringProvider.getString(R.string.book_room_cta),
                    BOOK_BEDROOM_ACTION
                )
            )
            add(NavigationRowItem(stringProvider.getString(R.string.house_rules_cta)))

        }.filterNotNull()
    }

    private fun buildFriendsItems(
        userAccount: Account,
        notifications: List<NotificationItem>?
    ): List<Any> {
        return mutableListOf<Any?>().apply {
            add(
                MembershipCardItem(
                    subscriptionType = userAccount.subscriptionType,
                    membershipDisplayName = userAccount.membershipDisplayName,
                    memberName = stringProvider.getString(R.string.more_membership_name_label)
                        .replaceBraces(
                            userAccount.firstName
                                ?: "", userAccount.lastName ?: ""
                        ),
                    membershipId = userAccount.id,
                    profileImageUrl = userAccount.imageUrl,
                    loyaltyId = userAccount.loyaltyId,
                    isStaff = userAccount.isStaff
                )
            )
            notifications?.let { addAll(it) }
            if (upcomingBooking != null) add(upcomingBooking)
        }.filterNotNull()
    }

    private fun getTodayOperatingHours(stringProvider: StringProvider, venue: Venue): String {
        return DateUtils.formatTodayOperatingHours(
            stringProvider,
            venue.operatingHours,
            venue.timeZone
        )
    }

    private fun buildUpcomingBookingsCarouselItem(
        bookings: List<Booking>,
        venues: VenueList
    ): UpcomingBookingsContainerItem? {
        val showSeeAllBtn = bookings.size > UPCOMING_BOOKINGS_MAX_ITEMS
        val bookingsToDisplay = bookings.take(UPCOMING_BOOKINGS_MAX_ITEMS)
        if (bookingsToDisplay.isEmpty()) return null
        return UpcomingBookingsContainerItem(
            bookingItemsFactory.buildBookingItems(
                bookingsToDisplay,
                venues
            ), showSeeAllBtn
        )
    }

    private fun getHouseHoursLabel(stringProvider: StringProvider, venue: Venue): String {
        return if (DateUtils.isVenueOpen(venue.operatingHours, venue.timeZone)) {
            stringProvider.getString(R.string.open_now_label)
        } else {
            stringProvider.getString(R.string.closed_label)
        }
    }

    fun logRoomBookingClick(roomBooking: RoomBooking) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.BookingHistoryTapRoom,
            AnalyticsManager.Bookings.getRoomBookingParams(roomBooking)
        )
    }

    fun logEventBookingClick(eventBooking: EventBooking) {
        analyticsManager.logEventAction(AnalyticsManager.Action.HouseBoardBookingTapEvent)
        analyticsManager.logEventAction(
            AnalyticsManager.Action.UpcomingBookingsCarouselTapEvent,
            AnalyticsManager.Bookings.getEventBookingParams(eventBooking)
        )
    }

    fun logSeeAllUpcomingBookingsClick() {
        analyticsManager.logEventAction(AnalyticsManager.Action.UpcomingBookingsSeeAll)
        analyticsManager.logEventAction(AnalyticsManager.Action.HouseBoardBookingSeeAll)
    }

    fun onNotificationClick(item: NotificationItem) {
        patchNotification(item, seen = true)
        deeplink(item.navigationResourceId, item.navigationScreen, item.navigationTrigger)
    }

    fun trackHouseClick() {
        analyticsManager.logEventAction(AnalyticsManager.Action.HouseBoardTapHouse)
    }

    fun logNotificationTapView() {
        analyticsManager.logEventAction(AnalyticsManager.Action.NotificationsTapview)
    }

    fun logNotificationSwipeView() {
        analyticsManager.logEventAction(AnalyticsManager.Action.NotificationsSwipeview)
    }

    fun logNotificationTapClear() {
        analyticsManager.logEventAction(AnalyticsManager.Action.NotificationsTapclear)
    }

    fun logGoToMembershipCard() {
        analyticsManager.logEventAction(AnalyticsManager.Action.HouseBoardMembershipCard)
    }

    fun logExpandNotifications() {
        analyticsManager.logEventAction(AnalyticsManager.Action.ExpandNotificationsStack)
    }

    fun logCollapseNotifications() {
        analyticsManager.logEventAction(AnalyticsManager.Action.NotificationsShowless)
    }

    fun logClickInviteGuest() {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.HouseBoardGuestsNewListStart,
            AnalyticsManager.HouseGuest.buildParams(membershipType = userManager.membershipType)
        )
    }

    fun onTableBookingClick(tableBooking: TableBookingAdapterItem) {
        _openTableBooking.value = BookedTable.from(
            tableBooking.tableBooking,
            allVenues.findById(tableBooking.tableBooking.venue?.parentId),
            stringProvider
        )
    }
}