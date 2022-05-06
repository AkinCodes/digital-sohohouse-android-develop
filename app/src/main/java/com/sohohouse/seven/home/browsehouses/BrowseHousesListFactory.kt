package com.sohohouse.seven.home.browsehouses

import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.network.core.models.Venue

class BrowseHousesListFactory(val houseManager: HouseManager) {
    fun getBrowseHousesList(venues: List<Venue>): List<BaseAdapterItem.BrowseHousesItem> {
        if (venues.isEmpty()) {
            return listOf()
        }
        val result = mutableListOf<BaseAdapterItem.BrowseHousesItem>()

        val organizedList = houseManager.getOrganizedRegionVenueList(venues, false)
        for (pair in organizedList) {
            result.add(BaseAdapterItem.BrowseHousesItem.RegionHeader(pair.first.stringRes))
            val isLastPair = organizedList.indexOf(pair) == organizedList.lastIndex
            for (venue in pair.second) {
                val isLastItem = pair.second.indexOf(venue) == pair.second.lastIndex && isLastPair
                result.add(BaseAdapterItem.BrowseHousesItem.Content(venue, isLastItem))
            }
        }

        return result
    }
}