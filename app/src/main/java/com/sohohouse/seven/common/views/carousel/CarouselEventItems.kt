package com.sohohouse.seven.common.views.carousel

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.views.EventStatusType
import com.sohohouse.seven.network.core.models.Event

interface CarouselEventItems : DiffItem {
    var event: Event
    val eventStatusType: EventStatusType
    val venueName: String
    val venueTimeZone: String
    val venueColor: String
    val itemType: Int

    companion object {
        const val ITEM_TYPE_CONTENT = 0
        const val ITEM_TYPE_FULL_BLEED_CONTENT = 1
    }
}