package com.sohohouse.seven.common.venue

import com.sohohouse.seven.common.house.HouseRegion
import com.sohohouse.seven.common.house.HouseType
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.network.core.request.GetVenuesRequest

class VenueList(list: List<Venue>) : List<Venue> by list {

    constructor(singleVenue: Venue) : this(listOf(singleVenue))

    fun findById(id: String?): Venue? {
        return find { it.id.equals(id, ignoreCase = true) }
    }

    fun organizedRegionVenueList(
        localVenue: Venue,
        includeCWH: Boolean,
        includeStudios: Boolean = false,
        includeRestaurants: Boolean = false
    ): List<Pair<HouseRegion, List<Venue>>> {
        val venueTypes = mutableListOf(HouseType.HOUSE)
            .apply {
                if (includeStudios) add(HouseType.STUDIO)
                if (includeRestaurants) add(HouseType.RESTAURANT)
            }.map { it.name }
            .toTypedArray()

        val venuesList = filter { it.venueType in venueTypes }

        val regionList = venuesList.map { HouseRegion.valueOf(it.region) }.distinct().sortedBy {
            if (it.name == localVenue.region) 0 else 1
        }

        val regionVenueList = regionList.map {
            Pair(
                it,
                venuesList.filter { venue -> venue.region == it.id }
                    .sortedBy { venue -> venue.name })
        } as MutableList

        if (includeCWH) {
            val cWHVenues =
                filter { it.venueType == HouseType.CWH.name }.sortedBy { venue -> venue.name }
            if (cWHVenues.isNotEmpty()) {
                regionVenueList.add(Pair(HouseRegion.CWH, cWHVenues))
            }
        }

        return regionVenueList
    }

    fun filterWithTopLevel(): VenueList = VenueList(filter { venue ->
        venue.isTopLevel
                && (venue.venueType == GetVenuesRequest.CWH_VENUE_TYPE
                || venue.venueType == GetVenuesRequest.HOUSE_VENUE_TYPE
                || venue.venueType == HouseType.STUDIO.name)
    })

    companion object {
        fun empty() = VenueList(emptyList())
    }

}