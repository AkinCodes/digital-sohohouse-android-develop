package com.sohohouse.seven.common.views

import com.sohohouse.seven.common.extensions.isBookable
import com.sohohouse.seven.common.extensions.isDigitalEvent
import com.sohohouse.seven.common.extensions.isHappeningNow
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.Venue
import java.util.*

interface EventStatusHelper {

    fun getRestrictedEventStatus(event: Event, venue: Venue? = null): EventStatusType

}

class EventStatusHelperImpl(
    private val userManager: UserManager,
    private val houseManager: HouseManager
) : EventStatusHelper {

    override fun getRestrictedEventStatus(event: Event, venue: Venue?): EventStatusType {
        val eventType = EventType.get(event.eventType)

        if (eventType.isFitnessEvent()) {
            if (houseManager.canAccess(venue, eventType)
                    .not()
            ) return EventStatusType.HOUSE_MEMBER_ONLY

            val activeVenue = venue?.isActive ?: false
            val hasMembership = userManager.hasMembership(eventType)

            if (event.isBookable() && (hasMembership || activeVenue.not())) return EventStatusType.OPEN_FOR_BOOKING

            if (activeVenue && hasMembership.not()) return EventStatusType.ACTIVE_MEMBERS_ONLY
        }

        if (event.bookable == EventBookableStatus.NOT_A_MEMBER_OF_VENUE.name) {
            return EventStatusType.HOUSE_MEMBER_ONLY
        }

        event.endsAt?.let {
            if (it.before(Date()))
                return EventStatusType.EVENT_PAST
        }

        if (event.bookable == EventBookableStatus.BOOKING_NOT_OPEN_YET.name)
            return EventStatusType.OPEN_SOON

        if (event.bookable == EventBookableStatus.BOOKING_CLOSED.name
            //ticketless events should monitor state, as bookable may still be IS_BOOKABLE
            || event.isTicketless && event.state == EventState.CLOSED_FOR_BOOKING.name
        )
            return EventStatusType.EVENT_CLOSED

        if (event.bookable == EventBookableStatus.FULLY_BOOKED.name && event.hasWaitingList)
            return EventStatusType.WAITING_LIST

        if (event.bookable == EventBookableStatus.FULLY_BOOKED.name)
            return EventStatusType.FULLY_BOOKED

        if (event.postponed)
            return EventStatusType.EVENT_POSTPONED

        if (event.isDigitalEvent && event.isHappeningNow()) {
            return EventStatusType.LIVE_NOW
        }

        return EventStatusType.OPEN_FOR_BOOKING
    }
}
