package com.sohohouse.seven.book.eventdetails.eventstatus

import com.sohohouse.seven.common.views.eventdetaillist.BaseEventDetailsAdapterItem
import com.sohohouse.seven.book.eventdetails.EventDetailsAdapterItemType

class EventStatusAdapterItem(
    val eventStatusTitle: String,
    val eventDate: String?,
    val eventName: String, val eventImageUrl: String?,
    val venueName: String, val venueColor: String,
    val isStatusViewVisible: Boolean,
    val isCinemaSupporting: Boolean
) :
    BaseEventDetailsAdapterItem(EventDetailsAdapterItemType.BOOKING_SUCCESS_OVERVIEW)
