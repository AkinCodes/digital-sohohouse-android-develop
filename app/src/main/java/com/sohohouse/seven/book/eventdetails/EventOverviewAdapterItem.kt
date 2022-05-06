package com.sohohouse.seven.book.eventdetails

import com.sohohouse.seven.common.views.EventStatusType
import com.sohohouse.seven.common.views.UserBookingState
import com.sohohouse.seven.common.views.eventdetaillist.BaseEventDetailsAdapterItem
import java.util.*

class EventOverviewAdapterItem(
    val eventName: String,
    val houseName: String,
    val categoryUrl: String?,
    val categoryName: String,
    val houseColor: String,
    val eventStatus: EventStatusType? = null,
    val isCancelled: Boolean = false,
    val bookingState: UserBookingState? = null,
    val openingCancellationDate: Date? = null,
    val isPendingLotteryState: Boolean = false,
    val isTicketless: Boolean = false,
    val timeZone: String,
    val instructor: String = "",
    var numberOfGuests: Int,
    val isDigitalEvent: Boolean,
    val isNonRefundable: Boolean
) :
    BaseEventDetailsAdapterItem(EventDetailsAdapterItemType.OVERVIEW) {
    fun hideActionButton(): Boolean {
        return openingCancellationDate?.before(Date()) == true || hideCancelButton()
    }


    private fun hideCancelButton(): Boolean {
        return (bookingState == UserBookingState.HELD
                || bookingState == UserBookingState.GUEST_LIST) && isNonRefundable
    }
}