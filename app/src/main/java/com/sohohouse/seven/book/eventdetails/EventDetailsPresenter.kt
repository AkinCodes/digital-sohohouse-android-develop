package com.sohohouse.seven.book.eventdetails

import android.annotation.SuppressLint
import android.net.Uri
import android.text.TextUtils
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.base.error.ErrorDialogPresenter
import com.sohohouse.seven.base.error.ErrorViewStatePresenter
import com.sohohouse.seven.base.load.PresenterLoadable
import com.sohohouse.seven.book.eventdetails.bookingsuccess.BookingSuccessItem
import com.sohohouse.seven.book.eventdetails.bookingsuccess.EventGuestListAdapterItem
import com.sohohouse.seven.book.eventdetails.model.EventExternalLinkAdapterItem
import com.sohohouse.seven.book.eventdetails.payment.BookEventHelper
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.error.ErrorReporter
import com.sohohouse.seven.common.events.ExploreCategoryManager
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.*
import com.sohohouse.seven.common.views.eventdetaillist.*
import com.sohohouse.seven.common.views.eventdetaillist.HouseDetailsAdapterItem.Companion.getVenueAddress
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.*
import com.sohohouse.seven.network.core.models.NotificationSubscription.Companion.OPEN_FOR_BOOKING_ACTION
import com.sohohouse.seven.network.core.request.*
import com.sohohouse.seven.network.vimeo.GetVideoConfigRequest
import com.sohohouse.seven.network.vimeo.VimeoRequestFactory
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class EventDetailsPresenter @Inject constructor(
    zipRequestsUtil: ZipRequestsUtil,
    private val categoryManager: ExploreCategoryManager,
    bookEventHelper: BookEventHelper,
    private val houseManager: HouseManager,
    private val userManager: UserManager,
    private val venueRepo: VenueRepo,
    stepperPresenter: StepperPresenter,
    private val vimeoRequestFactory: VimeoRequestFactory,
    private val eventStatusHelper: EventStatusHelper,
    private val analyticsManager: AnalyticsManager
) : BasePresenter<EventDetailsViewController>(),
    PresenterLoadable<EventDetailsViewController>,
    ErrorDialogPresenter<EventDetailsViewController>,
    ErrorViewStatePresenter<EventDetailsViewController>,
    StepperPresenter by stepperPresenter,
    EventDetailTracker by EventDetailTrackerImpl(analyticsManager),
    EventBooker by EventBookerImpl(bookEventHelper, zipRequestsUtil) {

    companion object {
        const val TAG = "EventDetailsPresenter"
    }

    internal lateinit var eventId: String
    private lateinit var event: Event
    private val bookingState: BookingState?
        get() {
            return BookingState.valueOf(event.booking?.get(event.document)?.state ?: return null)
        }

    private lateinit var venue: Venue
    private var parentVenue: Venue? = null
    private var bookingInfo: EventBooking? = null

    private var reminder: NotificationSubscription? = null
    private var bookingId: String? = null
    private var bookingType: BookingType? = null

    private var shouldRefreshOnResult = false

    @SuppressLint("CheckResult")
    fun setUp(event: Event) {
        Single.just(value(venueRepo.venues()))
            .doOnSuccess { this.event = event }
            .compose(retrieveEventVenue())
            .compose(composeAdapterItem())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorViewStateTransformer())
            .subscribe(::onEventDetailsFetched) { Timber.e(it.localizedMessage) }
    }

    @SuppressLint("CheckResult")
    fun fetchData(isFirstFetch: Boolean = false) {
        shouldRefreshOnResult = !isFirstFetch || shouldRefreshOnResult

        zipRequestsUtil.issueApiCall(
            GetEventDetailsRequest(
                eventId = eventId,
                includeBookings = true,
                includeResource = true
            )
        )
            .flatMap { either ->
                either.fold(
                    ifValue = {
                        this.event = it
                        if (bookingState == BookingState.UNCONFIRMED) {
                            executeWhenAvailable { view, _, _ ->
                                view.showConfirmationButton { confirmBooking() }
                            }
                        }
                        if (it.isDigitalEvent) {
                            val url = fetchVimeoVideoUrl(it)
                            executeWhenAvailable { view, _, _ ->
                                view.setVideoUrl(url)
                            }
                        }
                        Single.just(value(venueRepo.venues()))
                    },
                    ifEmpty = { Single.just(Either.Empty()) },
                    ifError = { Single.just(Either.Error(it)) }
                )
            }
            .observeOn(AndroidSchedulers.mainThread())
            .compose(retrieveEventVenue())
            .compose(composeAdapterItem())
            .compose(loadTransformer())
            .compose(errorViewStateTransformer())
            .subscribe(::onEventDetailsFetched) { Timber.e(it.localizedMessage) }
    }

    @SuppressLint("CheckResult")
    private fun confirmBooking() {
        zipRequestsUtil.issueApiCall(
            PatchBookingStateRequest(
                BookingState.CONFIRMED.name,
                eventId,
                bookingId ?: ""
            )
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorDialogTransformer())
            .subscribe(
                {
                    it.ifEmpty {
                        event.booking?.get(event.document)?.state = BookingState.CONFIRMED.name
                    }
                },
                { ErrorReporter.logException(it) }
            )
    }

    private fun onEventDetailsFetched(either: Either<ServerError, List<BaseEventDetailsAdapterItem>>) {
        either.fold(
            ifValue = {
                track(event)
                executeWhenAvailable { view, _, _ ->
                    view.setupScreenName(EventType.get(event.eventType))
                    view.showEventDetails(it)
                }
            },
            ifError = { Timber.d(it.toString()) },
            ifEmpty = {}
        )
    }

    @SuppressLint("CheckResult")
    private fun fetchReminder() {
        executeIfAvailable { view -> view.showRemindMeView(RemindMeButtonStatus.IS_LOADING) }
        zipRequestsUtil.issueApiCall(GetNotificationSubscriptionsRequest(resourceId = eventId))
            .observeOn(AndroidSchedulers.mainThread())
            .compose(errorDialogTransformer())
            .subscribe(::onFetchReminder) { Timber.e(it.localizedMessage) }
    }

    private fun onFetchReminder(either: Either<ServerError, List<NotificationSubscription>>) {
        either.fold(
            ifValue = {
                for (reminder in it)
                    if (reminder.event.get().id == eventId) this.reminder = reminder
                val status =
                    if (this.reminder != null) RemindMeButtonStatus.DELETE_REMINDER
                    else RemindMeButtonStatus.SET_REMINDER
                executeWhenAvailable { view, _, _ -> view.showRemindMeView(status) }
            },
            ifError = {
                executeWhenAvailable { view, _, _ -> view.hideRemindMeView() }
                Timber.d(it.toString())
            },
            ifEmpty = {}
        )
    }

    private fun showHideBookingStepper(bookingInfo: EventBooking?, eventStatus: EventStatusType?) {
        val config = getStringForBooking(event, venue, eventStatus, bookingInfo)
        executeWhenAvailable { view, _, _ ->
            if (shouldHideStepper(
                    config,
                    event,
                    bookingInfo
                ) || bookingState == BookingState.UNCONFIRMED
            ) {
                view.hideBookingStepper()
            } else {
                view.showBookingStepper(config)
            }
        }
    }

    private fun showHideRemindMe(eventStatus: EventStatusType?) {
        if (event.isDigitalEvent) return

        when (eventStatus) {
            EventStatusType.OPEN_SOON -> fetchReminder()
            else -> executeWhenAvailable { view, _, _ -> view.hideRemindMeView() }
        }
    }

    private fun createEventDetailsData(
        categoryName: String,
        categoryUrl: String?
    ): List<BaseEventDetailsAdapterItem> {
        val eventType = EventType.get(event.eventType)
        val eventStatus = eventStatusHelper.getRestrictedEventStatus(event, venue)
        val booking = event.booking?.get(event.document)
        val bookingState = UserBookingState.getState(booking)
        bookingInfo = if (bookingState != null) booking else null

        bookingId = bookingInfo?.id
        bookingInfo?.bookingType?.let {
            bookingType = BookingType.valueOf(it)
        }

        showHideBookingStepper(bookingInfo, eventStatus)
        showHideRemindMe(eventStatus)

        val eventResource = event.resource.get(event.document)
        val resourceInstructor = eventResource?.instructor ?: ""
        val instructor = if (resourceInstructor.isEmpty()) {
            eventResource.resourceMeta.get(event.document)?.instructor ?: ""
        } else {
            resourceInstructor
        }

        val data: MutableList<BaseEventDetailsAdapterItem> = mutableListOf()

        data.add(
            EventOverviewAdapterItem(
                eventName = event.name,
                houseName = venue.name,
                houseColor = venue.venueColors.house,
                eventStatus = eventStatus,
                bookingState = bookingState,
                isCancelled = BookingState.CANCELLED.name == booking?.state,
                categoryUrl = categoryUrl,
                categoryName = categoryName,
                openingCancellationDate =
                if (eventStatus == EventStatusType.OPEN_SOON)
                    event.openForBookingAt
                else
                    event.cancellableUntil,
                isPendingLotteryState = event.isPendingLotteryState(),
                isTicketless = event.isTicketless,
                timeZone = venue.timeZone,
                instructor = instructor,
                numberOfGuests = bookingInfo?.numberOfGuests ?: 0,
                isDigitalEvent = event.isDigitalEvent,
                isNonRefundable = event.isNonRefundable
            )
        )

        val isLotteryOpen = event.hasLottery && event.openForBookingAt?.before(Date()) == true
        if (event.isDigitalEvent.not() && (!eventType.isHouseVisitEvent() || !event.isTicketless)) {
            data.add(
                EventTicketsAdapterItem(
                    event.isTicketless,
                    event.drawLotteryAt,
                    venue.timeZone,
                    event.lotteryDrawn,
                    event.priceCents,
                    event.priceCurrency
                        ?: "",
                    eventType,
                    event.maxGuestsPerBooking,
                    isLotteryOpen,
                    if (eventStatus == EventStatusType.OPEN_SOON) event.openForBookingAt else null
                )
            )
        }

        data.add(EventDateAdapterItem(event.startsAt, event.endsAt, venue.timeZone) {
            executeWhenAvailable { view, _, _ ->
                view.launchAddToCalendarIntent(
                    event.name,
                    event.address,
                    event.startsAt?.time ?: 0L,
                    event.endsAt?.time ?: 0L
                )
            }
        })
        data.add(
            EventDescriptionAdapterItem(
                labelStringRes = if (eventType.isCinemaEvent()) R.string.explore_cinema_event_details_label else R.string.explore_events_event_details_label,
                description = event.description
            )
        )

        if (eventType.isCinemaEvent()) {
            event.film?.get(event.document)?.run {
                director?.isNotEmpty {
                    data.add(
                        EventDetailsSubDescriptionAdapterItem(
                            R.string.explore_cinema_event_director_label,
                            it
                        )
                    )
                }
                cast?.isNotEmpty {
                    data.add(
                        EventDetailsSubDescriptionAdapterItem(
                            R.string.explore_cinema_event_cast_label,
                            it
                        )
                    )
                }
                distributor?.isNotEmpty {
                    data.add(
                        EventDetailsSubDescriptionAdapterItem(
                            R.string.explore_cinema_event_distributor_label,
                            it
                        )
                    )
                }
                year?.let {
                    data.add(
                        EventDetailsSubDescriptionAdapterItem(
                            R.string.explore_cinema_event_year_label,
                            it.toString()
                        )
                    )
                }
                runningTime?.let {
                    data.add(
                        EventDetailsSubDescriptionAdapterItem(
                            R.string.explore_cinema_event_runtime_label,
                            it.toString()
                        )
                    )
                }
                country?.isNotEmpty {
                    data.add(
                        EventDetailsSubDescriptionAdapterItem(
                            R.string.explore_cinema_event_country_label,
                            it
                        )
                    )
                }
                certificate?.isNotEmpty {
                    data.add(
                        EventDetailsSubDescriptionAdapterItem(
                            R.string.explore_cinema_event_certificate_label,
                            it
                        )
                    )
                }
                subtitles?.isNotEmpty {
                    data.add(
                        EventDetailsSubDescriptionAdapterItem(
                            R.string.explore_cinema_event_subtitles_label,
                            it
                        )
                    )
                }
                language?.isNotEmpty {
                    data.add(
                        EventDetailsSubDescriptionAdapterItem(
                            R.string.explore_cinema_event_language_label,
                            it
                        )
                    )
                }
            }

            data.last { it is EventDetailsSubDescriptionAdapterItem }.let { lastItem ->
                (lastItem as EventDetailsSubDescriptionAdapterItem).isLastItem = true
            }
        }

        event.links?.forEach { link ->
            link.description?.let { description ->
                link.url?.let { url -> data.add(EventExternalLinkAdapterItem(description, url)) }
            }
        }

        if (event.isBooked()
                .not() || (event.isBooked() && (bookingInfo?.numberOfGuests == 0 || event.isTicketless))
        ) {
            val item = createMembershipAdapterItem(event, venue, eventType)
                ?: createGuestAdapterItem()
            if (item != null) data.add(item)
        }

        bookingInfo?.numberOfGuests?.let { guestCount ->
            if (guestCount > 0) {
                val canCancel = event.cancellableUntil?.after(Date()) ?: true
                        && (bookingInfo?.state != BookingState.CONFIRMED.name || event.sendBookingConfirmationAt == null)
                data.add(EventGuestListAdapterItem(guestCount,
                    if (canCancel)
                        object : DeleteGuestListener {
                            override fun deleteGuest(newGuestCount: Int) {
                                executeWhenAvailable { view, _, _ ->
                                    view.showDeleteDialogue(
                                        newGuestCount
                                    )
                                }
                            }
                        }
                    else null,
                    event.name,
                    venue.name,
                    event.startsAt,
                    venue.timeZone
                ))
            }
        }

        if (event.isDigitalEvent.not()) {
            data.add(
                HouseDetailsAdapterItem(
                    address = if (eventType.isCinemaEvent()) getVenueAddress(
                        venue, parentVenue
                    ) else event.address,
                    onClickListener = {
                        executeWhenAvailable { view, _, _ ->
                            view.onAddressClicked(
                                event.address
                            )
                        }
                    },
                    phoneNumber = if (venue.phoneNumber.isEmpty()) parentVenue?.phoneNumber
                        ?: venue.phoneNumber else venue.phoneNumber,
                    venueName = parentVenue?.name ?: venue.name,
                    periods = venue.operatingHours.periods,
                    isOffsite = event.isOffsite
                )
            )
        }

        event.cancellableUntil?.let { date ->
            if (!event.isTicketless) {
                data.add(
                    EventCancellationAdapterItem(
                        date = date,
                        timeZone = venue.timeZone,
                        isPaid = event.priceCents > 0,
                        isNonRefundable = event.isNonRefundable
                    )
                )

                if (eventType.isCinemaEvent() && event.priceCents > 0) {
                    data.add(EventDepositPolicyAdapterItem(event.priceCents, event.priceCurrency))
                }
            }
        }
        return data
    }

    private fun createMembershipAdapterItem(
        event: Event,
        venue: Venue,
        eventType: EventType
    ): EventMembershipAdapterItem? {
        return when {
            !eventType.isFitnessEvent() -> null
            houseManager.canAccess(venue, eventType).not() -> EventMembershipAdapterItem(
                eventId = event.id,
                eventName = event.name,
                eventType = event.eventType,
                labelStringRes = R.string.explore_events_event_no_guests_label,
                hasLink = false
            )
            else -> null
        }
    }

    private fun createGuestAdapterItem(): EventGuestAdapterItem? {
        val guestCount = bookingInfo?.numberOfGuests ?: 0
        return if (!event.isTicketless && guestCount > 0) {
            EventGuestAdapterItem(
                maxGuestNum = event.maxGuestsPerBooking,
                guestNum = guestCount,
                eventId = event.id,
                eventName = event.name,
                eventType = event.eventType
            )
        } else null
    }

    @SuppressLint("CheckResult")
    fun addReminder() {
        if (EventType.get(event.eventType).isCinemaEvent()) {
            analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsAlertMe)
        }

        val action = when (eventStatusHelper.getRestrictedEventStatus(event, venue)) {
            EventStatusType.OPEN_SOON -> OPEN_FOR_BOOKING_ACTION
            else -> return
        }

        zipRequestsUtil.issueApiCall(PostNotificationSubscriptionsRequest(action, event))
            .observeOn(AndroidSchedulers.mainThread())
            .compose(errorDialogTransformer())
            .subscribe { either ->
                either.fold(
                    ifValue = {
                        reminder = it
                        executeWhenAvailable { view, _, _ ->
                            view.showRemindMeView(RemindMeButtonStatus.DELETE_REMINDER)
                        }
                    },
                    ifError = {
                        Timber.d(it.toString())
                        executeWhenAvailable { view, _, _ ->
                            view.showRemindMeView(RemindMeButtonStatus.SET_REMINDER)
                        }
                    },
                    ifEmpty = {}
                )
            }
    }

    @SuppressLint("CheckResult")
    fun deleteReminder() {
        val id = reminder?.id ?: return
        zipRequestsUtil.issueApiCall(DeleteNotificationSubscriptionRequest(id))
            .observeOn(AndroidSchedulers.mainThread())
            .compose(errorDialogTransformer())
            .subscribe { either ->
                when (either) {
                    is Either.Error -> {
                        Timber.d(either.error.toString())
                        executeWhenAvailable { view, _, _ ->
                            view.showRemindMeView(RemindMeButtonStatus.DELETE_REMINDER)
                        }
                    }
                    is Either.Empty -> executeWhenAvailable { view, _, _ ->
                        view.showRemindMeView(RemindMeButtonStatus.SET_REMINDER)
                    }
                }
            }
    }

    fun bookEvent(newTickets: Int = 0) {
        val numberOfTickets = (event.booking?.get(event.document)
            ?.takeIf { it.state == BookingState.CONFIRMED.name }?.numberOfGuests ?: 0) + newTickets
        val request = EventBookingRequest.create(event, numberOfTickets, newTickets)

        // pay if not free, or update the existing booking
        if (EventType.get(event.eventType).isFitnessEvent()) {
            analyticsManager.logEventAction(AnalyticsManager.Action.GymBookAndPay)
            bookFitnessEvent(request)
            return
        }

        if (event.priceCents > 0) {
            when (EventType.get(event.eventType)) {
                EventType.MEMBER_EVENT -> analyticsManager.logEventAction(AnalyticsManager.Action.EventsBuyTickets)
                EventType.CINEMA_EVENT -> analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsBookAndPay)
            }

            executeWhenAvailable { view, _, _ -> createBooking(view, request) }
        } else {
            when (EventType.get(event.eventType)) {
                EventType.MEMBER_EVENT -> analyticsManager.logEventAction(AnalyticsManager.Action.EventsBook)
            }
            updateBooking(
                request,
                ::onBookingUpdated,
                ::onBookingError,
                listOf(loadTransformer(), errorDialogTransformer())
            )
        }
    }

    private fun bookFitnessEvent(request: EventBookingRequest) {
        val gymMembership = userManager.gymMembership

        when {
            houseManager.canAccess(venue, EventType.FITNESS_EVENT).not() -> {
                return
            }
            venue.isActive && gymMembership.hasMembership().not() -> {
                val param = AnalyticsManager.SubscribeActive.buildParams(
                    event.id,
                    event.name,
                    event.eventType
                )
                analyticsManager.logEventAction(AnalyticsManager.Action.ActiveSubscribe, param)
                view.showActiveMembershipInfo(event.id, event.name, event.eventType)
            }
            event.isFree() && (gymMembership.isActive() || venue.isActive.not()) || gymMembership.isActivePlus() -> {
                updateBooking(
                    request,
                    ::onBookingUpdated,
                    ::onBookingError,
                    listOf(loadTransformer(), errorDialogTransformer())
                )
            }
            else -> {
                analyticsManager.logEventAction(AnalyticsManager.Action.GymBuyTicket)
                createBooking(view, request)
            }
        }
    }

    private fun onBookingUpdated(eventBooking: EventBooking) {
        showSuccessView(
            eventBooking.numberOfGuests ?: 0,
            UserBookingState.getState(
                BookingType.valueOf(eventBooking.bookingType),
                eventBooking.state?.let { BookingState.valueOf(it) })?.name ?: ""
        )
        fetchData()
    }

    fun deleteBooking() {
        deleteBooking(
            EventBookingRequest.create(event),
            ::onBookingDeleted,
            { onBookingError() },
            listOf(loadTransformer(), errorDialogTransformer())
        )
        trackDeleteBooking(event, bookingInfo, bookingType)
    }

    private fun onBookingDeleted(either: Either<ServerError, Void>) {
        when (either) {
            is Either.Error -> onBookingError()
            is Either.Empty -> {
                shouldRefreshOnResult = true
                fetchData()
            }
        }
    }

    fun deleteGuest(numberOfTickets: Int) {
        deleteGuest(
            EventBookingRequest.create(event, numberOfTickets),
            { onGuestBookingDeleted() },
            { onBookingError() },
            listOf(loadTransformer(), errorDialogTransformer())
        )
        trackDeleteGuest(event)
    }

    private fun onGuestBookingDeleted() {
        fetchData()
    }

    private fun onBookingError() {
        executeWhenAvailable { view, _, _ -> view.showBookingError() }
    }

    private fun onBookingError(detail: String) {
        if (TextUtils.isEmpty(detail)) {
            executeWhenAvailable { view, _, _ -> view.showBookingError() }
        } else {
            executeWhenAvailable { view, _, _ -> view.showBookingErrorWithMessage(detail) }
        }
    }

    fun showSuccessView(tickets: Int, state: String) {
        trackBookingSuccess(event, tickets, state)
        val bookingSuccessItem = BookingSuccessItem(
            bookingState = UserBookingState.valueOf(state),
            eventDate = event.startsAt,
            timeZone = venue.timeZone,
            eventId = event.id,
            eventName = event.name,
            eventImageUrl = event.images?.large,
            venueName = venue.name,
            venueColor = venue.venueColors.house,
            maxGuest = event.maxGuestsPerBooking,
            guestCount = tickets,
            isInduction = false,
            isPendingLotteryState = event.isPendingLotteryState(),
            eventType = event.eventType,
            isDigitalEvent = event.isDigitalEvent
        )
        executeWhenAvailable { view, _, _ -> view.showBookingSuccess(bookingSuccessItem) }
    }

    fun onUserClickCancelBooking() {
        trackUserClickCancelButton(event)
    }

    //region Error
    override fun reloadDataAfterError() {
        fetchData(true)
    }
    //endregion


    @SuppressLint("CheckResult")
    private fun retrieveEventVenue(): SingleTransformer<Either<ServerError, VenueList>, Either<ServerError, List<EventCategory>>> {
        return SingleTransformer { single ->
            return@SingleTransformer single.flatMap { either ->
                either.fold(
                    ifValue = {
                        val eventVenue = it.findById(event.venue?.get()?.id)
                        if (eventVenue != null) {
                            venue = eventVenue
                            parentVenue = it.findById(venue.parentId)
                        } else {
                            Single.just(Either.Error(ServerError.INVALID_RESPONSE))
                        }
                        categoryManager.getCategories()
                    },
                    ifEmpty = { Single.just(Either.Empty()) },
                    ifError = { Single.just(Either.Error(it)) }
                )
            }
        }
    }

    private fun composeAdapterItem(): SingleTransformer<Either<ServerError, List<EventCategory>>, Either<ServerError, List<BaseEventDetailsAdapterItem>>> {
        return SingleTransformer { upstream ->
            return@SingleTransformer upstream.flatMap { either ->
                either.fold(
                    ifValue = {
                        val category = it.firstOrNull { event.category == it.id }
                        val categoryName = category?.name ?: ""
                        val categoryUrl: String? = category?.icon?.png
                        Single.just(value(createEventDetailsData(categoryName, categoryUrl)))
                    },
                    ifEmpty = { Single.just(Either.Empty()) },
                    ifError = { Single.just(Either.Error(it)) }
                )
            }
        }
    }

    fun shouldRefreshOnResult(onFinish: (event: Event) -> Unit) {
        if (shouldRefreshOnResult && this::event.isInitialized) {
            onFinish(event)
        }
    }

    fun isDigitalEvent(): Boolean {
        if (this::event.isInitialized.not()) return false
        return event.isDigitalEvent
    }


    fun isStartingSoon(): Boolean {
        if (this::event.isInitialized.not()) return false
        return event.isStartingSoon()
    }

    fun isPastEvent(): Boolean {
        if (this::event.isInitialized.not()) return false
        return event.endsAt?.before(Date()) ?: false
    }

    fun isEventLiveNow(): Boolean {
        if (this::event.isInitialized.not()) return false
        return event.isHappeningNow()
    }

    private fun fetchVimeoVideoUrl(event: Event): String? {
        return try {
            val id = Uri.parse(event.digitalInfo?.embedUrl).lastPathSegment ?: return null
            return vimeoRequestFactory.createV2(GetVideoConfigRequest(id)).fold(
                ifValue = { it?.request?.files?.progressive?.firstOrNull()?.url },
                ifError = { null },
                ifEmpty = { null }
            )
        } catch (e: Exception) {
            ErrorReporter.logException(e)
            null
        }
    }

    fun logAddToBookings() {
        analyticsManager.logEventAction(AnalyticsManager.Action.EventsAddToBookings)
    }

    fun logMoreTicketsClick() {
        when (EventType.get(event.type)) {
            EventType.CINEMA_EVENT -> analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsMoreTickets)
            EventType.MEMBER_EVENT -> analyticsManager.logEventAction(AnalyticsManager.Action.EventsMoreTickets)
        }
    }

    fun logLessTicketsClick() {
        when (EventType.get(event.type)) {
            EventType.CINEMA_EVENT -> analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsLessTickets)
            EventType.MEMBER_EVENT -> analyticsManager.logEventAction(AnalyticsManager.Action.EventsLessTickets)
        }
    }

    fun logCancelTicketless() {
        when (EventType.get(event.type)) {
            EventType.MEMBER_EVENT -> analyticsManager.logEventAction(AnalyticsManager.Action.EventsCancelTicketless)
        }
    }

    fun logJoinLottery() {
        when (EventType.get(event.type)) {
            EventType.CINEMA_EVENT -> analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsJoinLottery)
        }
    }

    fun logJoinWaitingList() {
        when (EventType.get(event.type)) {
            EventType.CINEMA_EVENT -> analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsJoinWaitingList)
        }
    }
}
