package com.sohohouse.seven.home.repo

import com.sohohouse.seven.common.utils.isVenueOpen
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.CoreRequestFactory
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.network.core.request.GetEventsRequest
import com.sohohouse.seven.network.utils.getApiFormattedDate
import kotlinx.coroutines.coroutineScope
import org.threeten.bp.LocalDateTime

interface EventsRepo {

    suspend fun getHappeningNowEvents(
        attendingVenue: Venue?,
        locationList: List<String>
    ): Either<ServerError, List<Event>>

    suspend fun getDynamicHouseEvents(attendingVenue: Venue?): Either<ServerError, List<Event>>

    suspend fun getMemberEventsOnDemand(): Either<ServerError, List<Event>>
}

class EventsRepoImpl(private val requestFactory: CoreRequestFactory) : EventsRepo {

    override suspend fun getHappeningNowEvents(
        attendingVenue: Venue?,
        locationList: List<String>
    ): Either<ServerError, List<Event>> {
        return coroutineScope {
            val currentDate = LocalDateTime.now().getApiFormattedDate()
            val dynamicVenueIfOpen = attendingVenue.takeIf { it.isVenueOpen() }?.id
            requestFactory.createV2(
                GetEventsRequest.getHappeningNowEvents(
                    locationList,
                    currentDate,
                    dynamicVenueIfOpen
                )
            )
        }
    }

    override suspend fun getDynamicHouseEvents(attendingVenue: Venue?): Either<ServerError, List<Event>> {
        return coroutineScope {
            val currentDate = LocalDateTime.now().getApiFormattedDate()
            val venueId = attendingVenue?.takeIf { it.isVenueOpen() }?.id
            requestFactory.createV2(GetEventsRequest.getDynamicHouseEvents(venueId, currentDate))
        }
    }

    override suspend fun getMemberEventsOnDemand(): Either<ServerError, List<Event>> {
        return requestFactory.createV2(GetEventsRequest.getPastDigitalEvents(1, 10))
    }
}
