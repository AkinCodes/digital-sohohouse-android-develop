package com.sohohouse.seven.book.adapter.model

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.views.EventStatusType
import com.sohohouse.seven.network.core.models.Event

interface EventBookAdapterItem : DiffItem, EventItem {
    var event: Event

    val venueName: String

    val venueColor: String

    val venueTimeZone: String?

    val eventStatus: EventStatusType

    val categoryName: String?

    val categoryUrl: String?

    override val key: Any?
        get() = event.id

    override val id: String
        get() = event.id

    override val title: String
        get() = event.name

    override val imageUrl: String?
        get() = event.images?.large
}