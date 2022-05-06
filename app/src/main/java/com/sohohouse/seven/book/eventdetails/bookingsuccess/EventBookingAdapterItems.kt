package com.sohohouse.seven.book.eventdetails.bookingsuccess

import com.sohohouse.seven.common.views.UserBookingState
import com.sohohouse.seven.common.views.eventdetaillist.BaseEventDetailsAdapterItem
import com.sohohouse.seven.common.views.eventdetaillist.DeleteGuestListener
import com.sohohouse.seven.book.eventdetails.EventDetailsAdapterItemType
import java.util.*

class EventBookingSuccessAdapterItem(
    val bookingState: UserBookingState,
    val eventDate: String?,
    val eventName: String,
    val eventImageUrl: String?,
    val venueName: String,
    val venueColor: String,
    val isPendingLotteryState: Boolean,
    val isInduction: Boolean,
    val isDigitalEvent: Boolean
) :
    BaseEventDetailsAdapterItem(EventDetailsAdapterItemType.BOOKING_SUCCESS_OVERVIEW)

class EventBookingTicketlessAdapterItem(
    val eventName: String,
    val eventDate: String?,
    val eventImageUrl: String?,
    val venueName: String,
    val venueColor: String
) :
    BaseEventDetailsAdapterItem(EventDetailsAdapterItemType.BOOKING_SUCCESS_TICKETLESS_OVERVIEW)

class EventBookingSuccessDescriptionItem(
    val label: String = "",
    val description: String = ""
) :
    BaseEventDetailsAdapterItem(adapterItemType = EventDetailsAdapterItemType.DESCRIPTION)


data class EventGuestListAdapterItem(
    val guestNum: Int,
    val deleteGuestListener: DeleteGuestListener? = null,
    val eventName: String,
    val venueName: String,
    val startDate: Date?,
    val timeZone: String?
) :
    BaseEventDetailsAdapterItem(adapterItemType = EventDetailsAdapterItemType.GUEST_LIST)