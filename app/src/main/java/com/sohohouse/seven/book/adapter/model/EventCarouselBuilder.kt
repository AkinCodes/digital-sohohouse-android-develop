package com.sohohouse.seven.book.adapter.model

import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.extensions.getFormattedDateTime
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.network.core.models.Event

object EventCarouselBuilder {
    fun buildOnDemandEventCarousel(events: List<Event>, venues: VenueList): List<DiffItem>? {
        if (events.isNullOrEmpty()) return null

        return mutableListOf<DiffItem>().apply {
            add(
                EventCarouselHeader(
                    title = R.string.event_member_events_on_demand,
                    subtitle = R.string.event_member_events_on_demand_description,
                    hasMore = true
                )
            )
            add(EventCarousel(events.map { buildOnDemandEventItems(it, venues) }))
        }
    }

    private fun buildOnDemandEventItems(event: Event, venues: VenueList): EventCarouselItem {
        return EventCarouselItem(
            id = event.id,
            title = event.name,
            subtitle = event.startsAt?.getFormattedDateTime(venues.findById(event.venue?.get()?.id)?.timeZone)
                ?: "",
            caption = R.string.event_digital_event,
            imageUrl = event.images?.large,
            isFeatured = event.featured ?: false
        )
    }
}