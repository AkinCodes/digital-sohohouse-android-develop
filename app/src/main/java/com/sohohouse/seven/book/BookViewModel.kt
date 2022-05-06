package com.sohohouse.seven.book

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.LiveEvent
import com.sohohouse.seven.base.mvvm.postEvent
import com.sohohouse.seven.book.eventdetails.eventstatus.EVENT_STATUS_TYPES
import com.sohohouse.seven.book.eventdetails.eventstatus.EventStatusItem
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.events.ExploreCategoryManager
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.ZipRequestsUtil
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.EventStatusHelper
import com.sohohouse.seven.common.views.EventStatusType
import com.sohohouse.seven.common.views.UserBookingState
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.network.core.request.GetEventDetailsRequest
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val viewInfo: BookViewInfo,
    private val zipRequestsUtil: ZipRequestsUtil,
    private val categoryManager: ExploreCategoryManager,
    private val eventStatusHelper: EventStatusHelper,
    private val venueRepo: VenueRepo,
    private val userManager: UserManager
) : BaseViewModel(analyticsManager) {

    val showEventStatus = LiveEvent<EventStatusItem>()
    val showInvalidDialog = LiveEvent<Any>()
    val showInvalidEventDialog = LiveEvent<Any>()
    val showEventDetail = LiveEvent<Event>()
    val showBookAVisitWebView = LiveEvent<Any>()
    val showFilter = MutableLiveData(isNotFriendsMember())

    val tabs get() = viewInfo.tabs
    var selectedTab = -1
        private set

    private lateinit var event: Event
    private lateinit var venue: Venue

    fun onPageSelected(pagePosition: Int) {
        if (selectedTab == pagePosition) return

        selectedTab = pagePosition

        when (viewInfo.tabs[pagePosition]) {
            BookTab.HOUSE_VISIT -> {
                showFilter.postValue(true)
                screenNameEvent.postValue(AnalyticsManager.Screens.HouseVisit.name)
                analyticsManager.logEventAction(AnalyticsManager.Action.HouseVisit)
            }
            BookTab.EVENTS -> {
                showFilter.postValue(isNotFriendsMember())
                screenNameEvent.postValue(AnalyticsManager.Screens.Events.name)
                analyticsManager.logEventAction(AnalyticsManager.Action.EventsTab)
            }
            BookTab.SCREENING -> {
                showFilter.postValue(isNotFriendsMember())
                screenNameEvent.postValue(AnalyticsManager.Screens.Screenings.name)
                analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsTab)
            }
            BookTab.GYM -> {
                showFilter.postValue(true)
                screenNameEvent.postValue(AnalyticsManager.Screens.Fitness.name)
                analyticsManager.logEventAction(AnalyticsManager.Action.GymTab)
            }
            BookTab.BEDROOMS -> {
                showFilter.postValue(false)
                analyticsManager.logEventAction(AnalyticsManager.Action.BedroomsTab)
            }
            BookTab.ELECTRIC_CINEMA -> {
                showFilter.postValue(false)
            }
            BookTab.BOOK_A_TABLE -> {
                showFilter.postValue(false)
            }
        }
    }

    @SuppressLint("CheckResult")
    fun showNotificationModal(eventId: String) {
        viewModelScope.launch {
            zipRequestsUtil.issueApiCall(
                GetEventDetailsRequest(
                    eventId = eventId,
                    includeBookings = true,
                    includeResource = true
                )
            ).fold(
                ifValue = { event ->
                    this@BookViewModel.event = event
                    onData(venueRepo.venues(), event)
                }, ifEmptyOrError = { showInvalidEventDialog.postEvent() }
            )
        }
    }

    private fun onData(venues: VenueList, event: Event) {
        val eventVenue = venues.findById(event.venue?.get()?.id)
        if (eventVenue != null) {
            this@BookViewModel.venue = eventVenue
            categoryManager.getCategoriesV2().fold(
                ifValue = {
                    val bookingState = UserBookingState.getState(event.booking?.get(event.document))
                    val bookingInfo =
                        if (bookingState != null) event.booking?.get(event.document) else null
                    val eventStatusType = eventStatusHelper.getRestrictedEventStatus(event, venue)
                    when {
                        bookingInfo == null && eventStatusType == EventStatusType.OPEN_FOR_BOOKING -> showEventDetail.postValue(
                            event
                        )
                        eventStatusType in EVENT_STATUS_TYPES || event.isTicketless -> prepareEventStatus(
                            event,
                            eventStatusType
                        )
                        else -> showInvalidEventDialog.postEvent()
                    }
                },
                ifEmptyOrError = { showInvalidEventDialog.postEvent() }
            )
        } else {
            showInvalidEventDialog.postEvent()
        }
    }

    private fun prepareEventStatus(event: Event, eventStatus: EventStatusType) {
        val eventStatusItem = EventStatusItem(
            event,
            eventStatus,
            venue.timeZone,
            venue.name,
            venue.venueColors.house
        )

        showEventStatus.postValue(eventStatusItem)
    }

    private fun isNotFriendsMember(): Boolean =
        userManager.subscriptionType != SubscriptionType.FRIENDS

    fun trackBackToAppFromBookBedroom(position: Int) {
        if (position == selectedTab && viewInfo.tabs[position] == BookTab.BEDROOMS) {
            analyticsManager.logEventAction(AnalyticsManager.Action.BedroomsBackToApp)
        }
    }
}