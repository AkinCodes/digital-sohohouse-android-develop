package com.sohohouse.seven.book.eventdetails

import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.isBookable
import com.sohohouse.seven.common.extensions.isDigitalEvent
import com.sohohouse.seven.common.extensions.isHappeningNow
import com.sohohouse.seven.common.extensions.isPendingLotteryState
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.views.*
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.EventBooking
import com.sohohouse.seven.network.core.models.Venue
import java.util.*

interface StepperPresenter {

    fun getStringForBooking(
        event: Event,
        venue: Venue,
        eventStatus: EventStatusType?,
        bookingInfo: EventBooking?
    ): Config

    fun shouldHideStepper(config: Config, event: Event, bookingInfo: EventBooking?): Boolean

    data class Config(
        val currentTicketsCount: Int,
        val maxAvailableTickets: Int,
        val invitingGuest: Boolean,
        val resString: Int
    ) {

        companion object {
            const val NO_ID = -1
        }
    }
}

class StepperPresenterImpl(
    private val houseManager: HouseManager,
    private val userManager: UserManager
) : StepperPresenter {

    override fun shouldHideStepper(
        config: StepperPresenter.Config,
        event: Event,
        bookingInfo: EventBooking?
    ): Boolean {
        return when {
            event.isNonRefundable && bookingInfo?.state.isNullOrEmpty().not() -> true
            event.isTicketless && EventType.get(event.eventType) == EventType.HOUSE_VISIT -> true
            event.isDigitalEvent && event.isHappeningNow() -> true
            config.resString == StepperPresenter.Config.NO_ID -> true
            bookingInfo == null -> false
            bookingInfo.state != BookingState.CONFIRMED.name || event.sendBookingConfirmationAt == null -> false
            else -> true
        }
    }

    override fun getStringForBooking(
        event: Event,
        venue: Venue,
        eventStatus: EventStatusType?,
        bookingInfo: EventBooking?
    ): StepperPresenter.Config {
        val canBook = event.isBookable()
        val canJoinWaitingList = eventStatus == EventStatusType.WAITING_LIST
        val canBookOrJoinWaitingList = canBook || canJoinWaitingList

        val guestTicketsCount = bookingInfo?.numberOfGuests ?: 0
        val maxAvailableTickets: Int
        val hasBookingInfo: Boolean

        val resString = if (bookingInfo == null) {
            getStringForNewBooking(
                event,
                venue,
                canBook,
                canJoinWaitingList,
                canBookOrJoinWaitingList
            )
        } else {
            getStringForExistingBooking(
                event,
                bookingInfo,
                canBook,
                canJoinWaitingList,
                canBookOrJoinWaitingList
            )
        }

        if (bookingInfo == null) {
            maxAvailableTickets = if (event.isTicketless) 1 else event.maxGuestsPerBooking + 1
            hasBookingInfo = false
        } else {
            maxAvailableTickets = event.maxGuestsPerBooking - guestTicketsCount
            hasBookingInfo = true
        }

        return StepperPresenter.Config(
            guestTicketsCount,
            maxAvailableTickets,
            hasBookingInfo,
            resString
        )
    }

    private fun getStringForNewBooking(
        event: Event,
        venue: Venue,
        canBook: Boolean,
        canJoinWaitingList: Boolean,
        canBookOrJoinWaitingList: Boolean
    ): Int {
        return when {
            canBookOrJoinWaitingList && event.isPendingLotteryState() -> R.string.explore_cinema_event_buy_tickets_cta
            canBookOrJoinWaitingList && event.isDigitalEvent -> R.string.explore_events_event_notify_before_starts_cta
            canBookOrJoinWaitingList && event.isTicketless -> R.string.explore_events_event_add_to_booking_cta
            canJoinWaitingList -> R.string.explore_events_event_waiting_cta
            EventType.get(event.eventType).isFitnessEvent() -> getStringForFitnessEvent(
                venue,
                canBook,
                event.isFree()
            )
            canBook && event.isFree().not() -> R.string.explore_events_event_buy_tickets_cta
            canBook -> R.string.explore_events_event_book_tickets_cta
            else -> StepperPresenter.Config.NO_ID
        }
    }

    private fun getStringForFitnessEvent(venue: Venue, canBook: Boolean, isFree: Boolean): Int {
        return when {
            houseManager.canAccess(venue, EventType.FITNESS_EVENT)
                .not() -> StepperPresenter.Config.NO_ID
            canBook && (venue.isActive.not() || userManager.gymMembership.isActive()) -> if (isFree) R.string.explore_events_event_book_tickets_cta else R.string.book_and_pay_cta
            canBook && userManager.gymMembership.isActivePlus() -> R.string.explore_events_event_book_tickets_cta
            venue.isActive && userManager.gymMembership.hasMembership()
                .not() -> R.string.find_out_more_cta
            else -> StepperPresenter.Config.NO_ID
        }
    }

    private fun getStringForExistingBooking(
        event: Event,
        bookingInfo: EventBooking?,
        canBook: Boolean,
        canJoinWaitingList: Boolean,
        canBookOrJoinWaitingList: Boolean
    ): Int {
        return when {
            event.isTicketless -> StepperPresenter.Config.NO_ID
            event.maxGuestsPerBooking <= (bookingInfo?.numberOfGuests
                ?: 0) -> StepperPresenter.Config.NO_ID
            event.cancellableUntil?.before(Date()) ?: false -> StepperPresenter.Config.NO_ID
            canBookOrJoinWaitingList && event.isPendingLotteryState()
                    || canJoinWaitingList && BookingType.WAITING_LIST.name == bookingInfo?.bookingType
                    || canBook && BookingType.GUEST_LIST.name == bookingInfo?.bookingType -> R.string.explore_events_event_invite_cta
            else -> StepperPresenter.Config.NO_ID
        }
    }
}
