package com.sohohouse.seven.common.house

import androidx.annotation.StringRes
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.buildAddress
import com.sohohouse.seven.common.extensions.isOpenForBusiness
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.prefs.LocalVenueProvider
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.common.views.locationlist.LocationCityItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerChildItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerParentItem
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.network.core.request.GetVenuesRequest
import javax.inject.Inject
import javax.inject.Singleton

enum class HouseRegion(val id: String) {
    EUROPE("EUROPE"),
    WORLDWIDE("WORLDWIDE"),
    NORTH_AMERICA("NORTH_AMERICA"),
    UK("UK"),
    CWH("CWH"),
    RESTAURANTS("RESTAURANTS"),
    HOUSES("HOUSES");

    val stringRes: Int
        get() = when (this) {
            NORTH_AMERICA -> R.string.explore_events_na_label
            UK -> R.string.explore_events_uk_label
            EUROPE -> R.string.explore_events_europe_label
            WORLDWIDE -> R.string.explore_events_worldwide_label
            CWH -> R.string.region_cwh
            RESTAURANTS -> R.string.book_a_table_restaurant
            HOUSES -> R.string.book_a_table_houses
        }
}

enum class HouseType {
    HOUSE,
    CWH,
    STUDIO,
    RESTAURANT,
    GYM;

    @StringRes
    fun getLabel(): Int {
        return when (this) {
            HOUSE -> R.string.label_houses
            CWH -> R.string.label_cwh
            STUDIO -> R.string.label_studio_spaces
            RESTAURANT -> R.string.label_restaurants
            GYM -> R.string.label_gyms
        }
    }
}

enum class LocationPickerType(val id: Int) {
    HOUSE(0),
    RESTAURANT(1),
    CITY(2);

    @StringRes
    fun getLabel(): Int {
        return when (this) {
            HOUSE -> R.string.label_houses
            RESTAURANT -> R.string.label_restaurants
            CITY -> R.string.label_cities
        }
    }
}

@Singleton
class HouseManager @Inject constructor(
    private val zipRequestsUtil: ZipRequestsUtil,
    private val userManager: UserManager,
    private val accountInteractor: AccountInteractor,
    private val localVenueProvider: LocalVenueProvider
) {

    fun getVenuesForBookATable(): Either<ServerError, List<Venue>> {
        return getRestaurantsFromApi().let { either ->
            when (either) {
                is Either.Value -> {
                    value(either.value.first.filter {
                        accountInteractor.canAccess(it)
                    })
                }
                is Either.Error -> either
                is Either.Empty -> either
            }
        }
    }

    private var allRestaurants: Pair<List<Venue>, Map<String, Venue>>? = null
    private fun getRestaurantsFromApi(): Either<ServerError, Pair<List<Venue>, Map<String, Venue>>> {
        allRestaurants?.let { return value(it) }
        return zipRequestsUtil.issueApiCallV2(GetVenuesRequest()).let { either ->
            when (either) {
                is Either.Value -> {
                    val capturedAllVenues = either.value.associateBy { venue -> venue.id }
                    val filteredVenues = ArrayList<Venue>()

                    val topVenues = HashMap<Venue, ArrayList<Venue>>()

                    either.value.filter { !it.isTopLevel && it.restaurant != null }.forEach {
                        val parent = either.value.find { parent -> parent.id == it.parentId }
                        parent?.let { parent ->
                            if (parent.isOpenForBusiness() && !it.restaurant?.bookingPartnerId.isNullOrEmpty()) {
                                if (topVenues.containsKey(parent)) {
                                    topVenues[parent]?.add(it)
                                } else {
                                    topVenues[parent] = ArrayList()
                                    topVenues[parent]?.add(it)
                                }
                            }
                        }
                    }

                    topVenues.keys.forEach { it.restaurants.addAll(topVenues[it] ?: emptyList()) }

                    filteredVenues.addAll(either.value.filter { it.isTopLevel && it.restaurant != null && it.isOpenForBusiness() })
                    filteredVenues.addAll(topVenues.keys)

                    allRestaurants = Pair(filteredVenues, capturedAllVenues)
                    return value(allRestaurants!!)
                }
                is Either.Error -> {
                    either
                }
                is Either.Empty -> {
                    either
                }
            }
        }
    }

    fun getLocalHouseId(): String {
        return userManager.localHouseId
    }

    private fun isMyRegion(region: HouseRegion) =
        region.name == localVenueProvider.localVenue.value?.region

    fun organizeHousesForLocationRecyclerView(
        venues: List<Venue>,
        hasDisabledState: Boolean,
        selectedLocationList: List<String>? = null,
        includeCWH: Boolean = true,
        includeAccessibleVenuesOnly: Boolean = false,
        includeOpenHousesOnly: Boolean = false,
        includeStudios: Boolean = false,
        includeRestaurants: Boolean = false,
        includeFavourites: Boolean = true,
        showVenueIcon: Boolean = true
    ):
            Triple<List<String>, List<LocationRecyclerChildItem>, List<LocationRecyclerParentItem>> {
        if (venues.isEmpty()) return Triple(emptyList(), emptyList(), emptyList())

        val regionHouseList =
            getOrganizedRegionVenueList(venues, includeCWH, includeStudios, includeRestaurants)
        val favouriteHouseIds = userManager.favouriteHouses
        val favouriteHouses = mutableListOf<LocationRecyclerChildItem>()
        val allHouses = mutableListOf<LocationRecyclerParentItem>()
        val selectedItemList = mutableListOf<String>()

        for (regionHousePair in regionHouseList) {
            val regionChildItems = mutableListOf<LocationRecyclerChildItem>()
            var selectedCount = 0
            for (venue in regionHousePair.second) {
                val isFavourite =
                    venue.id == getLocalHouseId() || favouriteHouseIds.contains(venue.id)
                val selected = selectedLocationList == null && isFavourite ||
                        selectedLocationList != null && selectedLocationList.contains(venue.id)

                val location = venue.buildAddress(singleLine = true)

                val canAccess = accountInteractor.canAccess(venue)
                val isOpenForBusiness = venue.isOpenForBusiness()
                val enabled = !hasDisabledState || (canAccess && venue.id != getLocalHouseId())
                val childItem = LocationRecyclerChildItem(
                    venue.id,
                    venue.name,
                    venue.venueIcons.darkPng,
                    selected,
                    enabled,
                    location = location,
                    showIcon = showVenueIcon
                )
                if ((canAccess || !includeAccessibleVenuesOnly) && (isOpenForBusiness || !includeOpenHousesOnly)) {
                    regionChildItems.add(childItem)
                    if (selected) {
                        selectedItemList.add(childItem.id)
                        selectedCount++
                    }
                    if (includeFavourites && isFavourite) {
                        favouriteHouses.add(childItem.copy())
                    }
                }
            }
            if (regionChildItems.isNotEmpty()) {
                allHouses.add(
                    LocationRecyclerParentItem(
                        regionHousePair.first,
                        regionChildItems,
                        expanded = if (regionChildItems.any { it.selected }) true else isMyRegion(
                            regionHousePair.first
                        )
                    )
                )
            }
        }

        if (allHouses.filter { it.expanded }.size > 1) {
            allHouses.map {
                if (isMyRegion(it.region)) it.expanded = false
            }
        }

        return Triple(selectedItemList, favouriteHouses, allHouses)
    }

    fun getOrganizedRegionVenueList(
        venues: List<Venue>,
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

        val venuesList = venues.filter { it.venueType in venueTypes }

        val regionList = venuesList.map { HouseRegion.valueOf(it.region) }.distinct().sortedBy {
            if (it.name == localVenueProvider.localVenue.value?.region) 0 else 1
        }

        val regionVenueList = regionList.map {
            Pair(
                it,
                venuesList.filter { venue -> venue.region == it.id }
                    .sortedBy { venue -> venue.name })
        } as MutableList

        if (includeCWH) {
            val cWHVenues = venues.filter { it.venueType == HouseType.CWH.name }
                .sortedBy { venue -> venue.name }
            if (cWHVenues.isNotEmpty()) {
                regionVenueList.add(Pair(HouseRegion.CWH, cWHVenues))
            }
        }

        return regionVenueList
    }

    fun canAccess(venue: Venue?, eventType: EventType = EventType.MEMBER_EVENT): Boolean {
        if (venue == null) return false
        return if (eventType.isFitnessEvent()) {
            accountInteractor.canAccess(venue.id) || accountInteractor.canAccess(venue._parent?.get()?.id)
                    || accountInteractor.canAccess(venue.activeParentVenue?.get()?.id)
        } else {
            accountInteractor.canAccess(venue.id)
        }
    }

    fun getOrganizedVenuesByCity(
        selectedCity: String?,
        venues: Map<String, List<Venue>>
    ): List<LocationCityItem> {
        return venues.map {
            val ids = it.value.map { venue -> venue.id }
            LocationCityItem(
                name = it.key,
                venueIdsInCity = ids,
                region = it.value.map { venue -> HouseRegion.valueOf(venue.region) }.first(),
                selected = it.key == selectedCity
            )
        }
    }
}
