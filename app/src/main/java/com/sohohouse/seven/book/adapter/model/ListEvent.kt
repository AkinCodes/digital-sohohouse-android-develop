package com.sohohouse.seven.book.adapter.model

import com.sohohouse.seven.common.views.EventStatusType
import com.sohohouse.seven.network.core.models.Event

data class ListEvent(
    override var event: Event,
    override val venueName: String,
    override val venueColor: String,
    override val venueTimeZone: String?,
    override val eventStatus: EventStatusType,
    override val categoryName: String?,
    override val categoryUrl: String?
) : EventBookAdapterItem