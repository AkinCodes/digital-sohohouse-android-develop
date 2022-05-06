package com.sohohouse.seven.connect.filter.house

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.sohohouse.seven.R
import com.sohohouse.seven.common.house.HouseRegion
import com.sohohouse.seven.common.house.HouseType
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.connect.filter.base.*
import com.sohohouse.seven.connect.filter.base.SectionItem.Companion.SECTION_FAVOURITES
import com.sohohouse.seven.network.core.models.Venue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class HouseFilterRepository : FilterRepository()

class HouseFilterRepositoryImpl(
    private val venueRepo: VenueRepo,
    private val userManager: UserManager,
    private val interactor: AccountInteractor
) : HouseFilterRepository() {

    override val items: LiveData<List<Filterable>> = getVenues().asLiveData()

    private fun getVenues(): Flow<List<Filterable>> = flow {
        emit(buildItems(venues = venueRepo.venues().filterWithTopLevel()))
    }

    private fun buildItems(
        venues: List<Venue>,
        includeCWH: Boolean = true,
        enabled: Boolean = true
    ): List<Filterable> {
        val venuesByRegion =
            buildVenuesByRegion(venues, includeCWH).takeIf { it.isNotEmpty() } ?: return emptyList()
        val favouriteHouseIds = userManager.favouriteHouses
        val localHouseId = userManager.localHouseId

        val regions = mutableListOf<Filterable>()
        val favourites = mutableListOf<FilterItem>()

        venuesByRegion.forEach {
            val items = mutableListOf<FilterItem>()
            it.second.iterator().forEach { venue ->
                val isFavourite = venue.id == localHouseId || favouriteHouseIds.contains(venue.id)
                val canAccess = interactor.canAccess(venue)
                val isEnabled = enabled || canAccess && venue.id != localHouseId

                items.add(
                    FilterItem(
                        Filter(id = venue.id, title = venue.name),
                        enabled = isEnabled,
                        tag = it.first.id
                    )
                )

                if (isFavourite) favourites.add(
                    FilterItem(
                        Filter(
                            id = venue.id,
                            title = venue.name
                        ), enabled = isEnabled, tag = SECTION_FAVOURITES
                    )
                )
            }

            if (items.isNotEmpty()) {
                regions.add(SectionItem(it.first.id, it.first.stringRes, items))
            }
        }
        return mutableListOf<Filterable>().apply {
            if (favourites.isNotEmpty()) {
                add(
                    SectionItem(
                        SECTION_FAVOURITES,
                        R.string.connect_filter_favourite_houses,
                        favourites,
                        true
                    )
                )
            }
            addAll(regions)
        }
    }

    private fun buildVenuesByRegion(
        venues: List<Venue>,
        includeCWH: Boolean
    ): List<Pair<HouseRegion, List<Venue>>> {
        val filteredVenues = venues.filter { it.venueType == HouseType.HOUSE.name }
        val regions = filteredVenues.map { HouseRegion.valueOf(it.region) }.distinct().sorted()

        return mutableListOf<Pair<HouseRegion, List<Venue>>>().apply {
            this.addAll(regions.map {
                Pair(
                    it,
                    filteredVenues.filter { venue -> venue.region == it.id }
                        .sortedBy { venue -> venue.name })
            })
            if (includeCWH) {
                filteredVenues.filter { it.venueType == HouseType.CWH.name }.sortedBy { it.name }
                    .takeIf { it.isNotEmpty() }?.let { this.add(Pair(HouseRegion.CWH, it)) }
            }
        }
    }
}