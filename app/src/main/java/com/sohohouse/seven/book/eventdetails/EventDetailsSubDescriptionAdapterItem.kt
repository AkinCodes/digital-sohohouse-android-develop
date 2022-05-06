package com.sohohouse.seven.book.eventdetails

import com.sohohouse.seven.common.views.eventdetaillist.BaseEventDetailsAdapterItem

class EventDetailsSubDescriptionAdapterItem(
    val attrStringRes: Int,
    val value: String,
    var isLastItem: Boolean = false
) :
    BaseEventDetailsAdapterItem(EventDetailsAdapterItemType.SUB_DESCRIPTION)