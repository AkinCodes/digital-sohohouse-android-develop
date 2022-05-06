package com.sohohouse.seven.memberonboarding.induction.booking

import android.annotation.SuppressLint
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.base.error.ErrorDialogPresenter
import com.sohohouse.seven.base.error.ErrorViewStatePresenter
import com.sohohouse.seven.base.load.PresenterLoadable
import com.sohohouse.seven.book.eventdetails.payment.BookEventHelper
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.getApiFormattedDate
import com.sohohouse.seven.common.extensions.getFilterApiFormattedDate
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.flatMapValue
import com.sohohouse.seven.network.base.model.mapValue
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.network.core.request.GetEventsRequest
import com.sohohouse.seven.network.core.request.PatchMembershipAttributesRequest
import com.sohohouse.seven.network.core.request.PostInquiryRequest
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject

enum class IntroBookingState { AVAILABLE, AVAILABLE_SELECTED, AVAILABLE_CONFIRMED, NONE_AVAILABLE, FOLLOWUP_CONFIRMED }

class InductionBookingPresenter @Inject constructor(
    private val zipRequestsUtil: ZipRequestsUtil, private val userManager: UserManager,
    private val venueRepo: VenueRepo, private val bookEventHelper: BookEventHelper,
    private val analyticsManager: AnalyticsManager
) :
    BasePresenter<InductionBookingViewController>(),
    PresenterLoadable<InductionBookingViewController>,
    ErrorViewStatePresenter<InductionBookingViewController>,
    ErrorDialogPresenter<InductionBookingViewController> {
    companion object {
        private const val TAG = "InductionBookingPresenter"
        private const val INDUCTION_ENQUIRY_TYPE = "MEMBERSHIP_ENQUIRY"
        private const val INDUCTION_ENQUIRY_REASON = "INDUCTION"
    }

    private lateinit var localHouse: Venue
    private lateinit var eventList: List<Event>

    private var bookingState: IntroBookingState = IntroBookingState.AVAILABLE
    private var selectedEvent: Event? = null
    private var selectedEventId: String? = null
    var bookingID: String? = null
    var isIndependent: Boolean = false

    override fun onAttach(
        view: InductionBookingViewController,
        isFirstAttach: Boolean,
        isRecreated: Boolean
    ) {
        super.onAttach(view, isFirstAttach, isRecreated)
        view.setScreenName(AnalyticsManager.Screens.InductionBooking.name)
        if (isFirstAttach) {
            fetchTimes()
        }
    }

    override fun reloadDataAfterError() {
        fetchTimes()
    }

    @SuppressLint("CheckResult")
    private fun fetchTimes() {
        val startDate = Calendar.getInstance().time.getFilterApiFormattedDate()
        val date = Calendar.getInstance()
        date.add(Calendar.DAY_OF_YEAR, 14)
        val endDate = date.time.getFilterApiFormattedDate()
        //get houses first

        venueRepo.fetchVenuesSingle().flatMapValue { venues ->
            val localHouseVenues = venues.filter { it.id == userManager.localHouseId }
            return@flatMapValue if (localHouseVenues.isNotEmpty()) {
                localHouse = localHouseVenues[0]
                zipRequestsUtil.issueApiCall(
                    GetEventsRequest.getInductionEvents(
                        locationFilters = arrayOf(userManager.localHouseId),
                        startsAtFrom = startDate,
                        startsAtTo = endDate,
                        endsAtFrom = Calendar.getInstance().time.getApiFormattedDate()
                    )
                )
            } else {
                Single.just(com.sohohouse.seven.network.base.model.error(ServerError.ApiError()))
            }
        }.mapValue {
            eventList = it
            val dataList: List<BaseInductItem> = if (eventList.isEmpty()) {
                bookingState = IntroBookingState.NONE_AVAILABLE
                InductionBookingListFactory().getNoneScheduledList(
                    localHouse,
                    isIndependent
                )
            } else {
                bookingState = IntroBookingState.AVAILABLE
                InductionBookingListFactory().getTimeItemList(
                    localHouse,
                    eventList,
                    isIndependent
                )
            }
            dataList
        }
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorViewStateTransformer())
            .subscribe { either ->
                either.fold(
                    ifValue = {
                        executeWhenAvailable { view, _, _ -> view.setData(it) }
                        updateBookButton()
                    },
                    ifError = { Timber.d(it.toString()) },
                    ifEmpty = {}
                )
            }
    }

    fun onAppointmentSelected(eventId: String) {
        selectedEventId = eventId
        selectedEvent = eventList.filter { it.id == eventId }[0]
        bookingState = IntroBookingState.AVAILABLE_SELECTED
        updateBookButton()
        executeWhenAvailable { view, _, _ -> view.updateSelectedDate(eventId) }
    }

    fun onAppointmentChangeClicked() {
        selectedEventId = null
        bookingState = IntroBookingState.AVAILABLE
        val dataList =
            InductionBookingListFactory().getTimeItemList(localHouse, eventList, isIndependent)
        executeWhenAvailable { view, _, _ -> view.setData(dataList) }
        updateBookButton()
    }

    private fun updateBookButton() {
        var resID = 0
        var isEnabled = false
        when (bookingState) {
            IntroBookingState.AVAILABLE -> {
                resID = R.string.onboarding_intro_next_cta
                isEnabled = false
            }
            IntroBookingState.AVAILABLE_SELECTED -> {
                resID = R.string.onboarding_intro_next_cta
                isEnabled = true
            }
            IntroBookingState.AVAILABLE_CONFIRMED, IntroBookingState.FOLLOWUP_CONFIRMED -> {
                resID = R.string.onboarding_intro_booked_done_cta
                isEnabled = true
            }
            IntroBookingState.NONE_AVAILABLE -> {
                resID = R.string.onboarding_intro_contact_cta
                isEnabled = true
            }
        }
        executeWhenAvailable { view, _, _ -> view.updateBookButtonText(resID, isEnabled) }
    }

    fun onActionPress() {
        when (bookingState) {
            IntroBookingState.AVAILABLE_SELECTED -> postBooking()
            IntroBookingState.NONE_AVAILABLE -> postRequestFollowup()
            else -> {
                //will not be enabled otherwise, so do nothing
            }
        }
    }

    fun onReturnFromConfirmation() {
        val selectedEvent = selectedEvent
        val bookingID = bookingID
        if (bookingState == IntroBookingState.AVAILABLE_CONFIRMED && selectedEvent != null && bookingID != null) {
            executeWhenAvailable { view, _, _ ->
                view.navigateAfterAppointmentSuccess(
                    selectedEvent,
                    localHouse,
                    bookingID
                )
            }
        } else {
            executeWhenAvailable { view, _, _ -> view.navigateAfterFollowUpSuccess(localHouse) }
        }
    }

    private fun postBooking() {
        val eventId = selectedEventId
        val event = selectedEvent
        if (eventId != null && event != null) {
            bookEventHelper.bookOrUpdateEvent(eventId, bookingID, null, 0, {
                bookingID = it.id
                bookingState = IntroBookingState.AVAILABLE_CONFIRMED
                event.startsAt?.let { startsAt ->
                    analyticsManager.track(
                        AnalyticsEvent.MemberOnBoarding.HouseIntroduction.Success(
                            startsAt
                        )
                    )
                }
                executeWhenAvailable { view, _, _ ->
                    view.showAppointmentSuccessModal(
                        eventId = eventId,
                        eventDate = event.startsAt,
                        timeZone = localHouse.timeZone,
                        imageURL = localHouse.house.get(localHouse.document)?.houseImageSet?.mediumPng,
                        houseName = localHouse.name,
                        houseColor = localHouse.venueColors.house
                    )
                }
            }, {
                executeWhenAvailable { view, _, _ ->
                    view.showBookingError()
                }
            }, true)
                .compose(loadTransformer())
                .compose(errorDialogTransformer())
                .subscribe()
        }
    }

    @SuppressLint("CheckResult")
    private fun postRequestFollowup() {
        zipRequestsUtil.issueApiCall(
            PostInquiryRequest(
                INDUCTION_ENQUIRY_TYPE,
                INDUCTION_ENQUIRY_REASON,
                null,
                null,
                null
            )
        )
            .flatMap { either ->
                return@flatMap either.fold(
                    ifValue = {
                        zipRequestsUtil.issueApiCall(
                            PatchMembershipAttributesRequest(
                                inductedAt = Date()
                            )
                        )
                    },
                    ifEmpty = { Single.just(either) },
                    ifError = { Single.just(either) }
                )
            }
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorDialogTransformer())
            .subscribe { either ->
                either.fold(
                    ifValue = {
                        onFollupConfirmed()
                    }, ifEmpty = {
                        onFollupConfirmed()
                    }, ifError = {
                        executeWhenAvailable { view, _, _ ->
                            view.showBookingError()
                        }
                    }
                )
            }
    }

    private fun onFollupConfirmed() {
        executeWhenAvailable { view, _, _ ->
            analyticsManager.track(AnalyticsEvent.MemberOnBoarding.HouseIntroduction.FollowUp)
            //show confirmation activity
            bookingState = IntroBookingState.FOLLOWUP_CONFIRMED
            view.showFollowupSuccessModal(
                eventId = "",
                imageURL = localHouse.house.get(localHouse.document)?.houseImageSet?.mediumPng,
                houseName = localHouse.name,
                houseColor = localHouse.venueColors.house
            )
        }
    }

    fun onRequestFollowupClicked() {
        postRequestFollowup()
    }
}
