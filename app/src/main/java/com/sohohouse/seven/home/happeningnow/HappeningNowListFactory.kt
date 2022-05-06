package com.sohohouse.seven.home.happeningnow

import com.sohohouse.seven.R
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.views.EventStatusHelper
import com.sohohouse.seven.common.views.carousel.CarouselEventItems
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.Venue
import javax.inject.Inject

class HappeningNowListFactory @Inject constructor(
    private val strings: StringProvider,
    private val eventStatusHelper: EventStatusHelper
) {
    fun getUpcomingEvents(
        events: List<Event>,
        houseList: VenueList
    ): BaseAdapterItem.HappeningNowItem.Container? {
        if (events.isEmpty()) return null

        val items = mutableListOf<CarouselEventItems>().also { items ->
            if (events.size == 1) {
                val event = events.first()
                val venue = houseList.findById(event.venue?.get()?.id) ?: Venue()
                items.add(
                    BaseAdapterItem.HappeningNowItem.Content(
                        event = event,
                        eventStatusType = eventStatusHelper.getRestrictedEventStatus(event, venue),
                        venueTimeZone = venue.timeZone,
                        venueName = venue.name,
                        venueColor = venue.venueColors.house
                    )
                )
                return@also
            }
            items.addAll(events.map { event ->
                val venue = houseList.findById(event.venue?.get()?.id) ?: Venue()
                val eventStatus = eventStatusHelper.getRestrictedEventStatus(event, venue)

                BaseAdapterItem.HappeningNowItem.Content(
                    event = event,
                    eventStatusType = eventStatus,
                    venueTimeZone = venue.timeZone,
                    venueName = venue.name,
                    venueColor = venue.venueColors.house
                )
            }.take(MAX_EVENT_COUNT))
        }

        return BaseAdapterItem.HappeningNowItem.Container(
            dataItems = items,
            headerText = strings.getString(R.string.home_happening_now_header),
            captionText = strings.getString(R.string.home_happening_now_supporting)
        )
    }

    fun getDynamicHouseEvents(
        events: List<Event>,
        lastAttendedVenue: Venue?
    ): BaseAdapterItem.HappeningNowItem.Container? {

        if (events.isEmpty() || lastAttendedVenue == null) return null

        val items = mutableListOf<CarouselEventItems>().also { items ->
            if (events.size == 1) {
                val event = events.first()
                items.add(
                    BaseAdapterItem.HappeningNowItem.Content(
                        event = event,
                        venueTimeZone = lastAttendedVenue.timeZone,
                        venueName = lastAttendedVenue.name,
                        venueColor = lastAttendedVenue.venueColors.house,
                        eventStatusType = eventStatusHelper.getRestrictedEventStatus(
                            event,
                            lastAttendedVenue
                        ),
                        itemType = CarouselEventItems.ITEM_TYPE_FULL_BLEED_CONTENT
                    )
                )
                return@also
            }
            items.addAll(events.map { event ->
                BaseAdapterItem.HappeningNowItem.Content(
                    event = event,
                    venueTimeZone = lastAttendedVenue.timeZone,
                    venueName = lastAttendedVenue.name,
                    venueColor = lastAttendedVenue.venueColors.house,
                    eventStatusType = eventStatusHelper.getRestrictedEventStatus(
                        event,
                        lastAttendedVenue
                    ),
                    itemType = CarouselEventItems.ITEM_TYPE_CONTENT
                )
            }.take(MAX_EVENT_COUNT))
        }
        return BaseAdapterItem.HappeningNowItem.Container(
            dataItems = items,
            headerText = lastAttendedVenue.name,
            captionText = strings.getString(R.string.home_house_supporting),
            isDynamicHouseCarousel = true
        )
    }

    companion object {
        private const val MAX_EVENT_COUNT = 10
    }
}
