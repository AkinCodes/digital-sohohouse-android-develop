package com.sohohouse.seven.book.eventdetails.model

import com.sohohouse.seven.book.eventdetails.EventDetailsAdapterItemType
import com.sohohouse.seven.common.views.eventdetaillist.BaseEventDetailsAdapterItem

data class EventExternalLinkAdapterItem(
    val description: String,
    val url: String
) : BaseEventDetailsAdapterItem(EventDetailsAdapterItemType.EXTERNAL_LINK)
