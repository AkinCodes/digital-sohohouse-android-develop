package com.sohohouse.seven.guests

import com.sohohouse.seven.network.core.models.Venue

sealed interface LocationType {
    val name: String

    data class City(override val name: String, val venueIds: List<String>) : LocationType
    data class SingleVenue(override val name: String, val venue: Venue) : LocationType
}