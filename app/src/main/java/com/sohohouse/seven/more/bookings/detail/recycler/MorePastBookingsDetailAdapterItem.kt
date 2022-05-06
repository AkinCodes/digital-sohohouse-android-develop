package com.sohohouse.seven.more.bookings.detail.recycler

enum class MorePastBookingsDetailAdapterItemType {
    HEADER,
    BOOKING_DETAIL,
    TEXT_BODY,
    CARD,
    CONTACT
}

open class MorePastBookingsDetailAdapterItem(val itemType: MorePastBookingsDetailAdapterItemType)

class MorePastBookingsDetailHeaderAdapterItem(
    val headerString: String,
    val extraPadding: Boolean = false
) : MorePastBookingsDetailAdapterItem(MorePastBookingsDetailAdapterItemType.HEADER)

class MorePastBookingsDetailBookingDetailAdapterItem(
    val titleString: String,
    val supportingString: String,
    val isLastItem: Boolean = false
) : MorePastBookingsDetailAdapterItem(MorePastBookingsDetailAdapterItemType.BOOKING_DETAIL)

class MorePastBookingsDetailTextAdapterItem(val textString: String) :
    MorePastBookingsDetailAdapterItem(MorePastBookingsDetailAdapterItemType.TEXT_BODY)

class MorePastBookingsDetailCardAdapterItem(
    val houseName: String, val houseColor: String,
    val eventName: String,
    val dateAndTime: String, val imageUrl: String
) : MorePastBookingsDetailAdapterItem(MorePastBookingsDetailAdapterItemType.CARD)

class MorePastBookingsDetailContactAdapterItem
    : MorePastBookingsDetailAdapterItem(MorePastBookingsDetailAdapterItemType.CONTACT)