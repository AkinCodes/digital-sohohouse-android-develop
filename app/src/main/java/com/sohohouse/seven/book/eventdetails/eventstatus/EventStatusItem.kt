package com.sohohouse.seven.book.eventdetails.eventstatus

import com.sohohouse.seven.common.views.EventStatusType
import com.sohohouse.seven.network.core.models.Event
import java.io.Serializable

data class EventStatusItem(
    val event: Event,
    val eventStatus: EventStatusType,
    val timeZone: String? = null,
    val venueName: String,
    val venueColor: String
) : Serializable