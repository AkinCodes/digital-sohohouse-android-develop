package com.sohohouse.seven.connect.filter.city

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.sohohouse.seven.common.house.HouseRegion
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.connect.filter.base.*
import com.sohohouse.seven.network.core.models.Venue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class CityFilterRepository : FilterRepository()

class CityFilterRepositoryImpl(private val venueRepo: VenueRepo) : CityFilterRepository() {

    override val items: LiveData<List<Filterable>> = getVenues().asLiveData()

    private fun getVenues(): Flow<List<Filterable>> = flow {
        emit(buildItems(venues = venueRepo.venues().filterWithTopLevel()))
    }

    private fun buildItems(venues: Collection<Venue>): List<Filterable> {
        val citiesByRegion = venues.map { it.city to it.region }.toMap().toSortedMap()
        val regions = venues.map { HouseRegion.valueOf(it.region) }.distinct()
        val items = mutableListOf<Filterable>()
        val map =
            regions.associateWith { region -> citiesByRegion.filter { it.key.isNotEmpty() && it.value == region.id } }
                .toMap()
        items.addAll(map.entries.sortedBy { it.key.id }.map { entry ->
            SectionItem(id = entry.key.id,
                title = entry.key.stringRes,
                items = entry.value.map {
                    FilterItem(
                        Filter(id = it.key, title = it.key),
                        enabled = true,
                        tag = entry.key.id
                    )
                })
        })
        return items
    }

}