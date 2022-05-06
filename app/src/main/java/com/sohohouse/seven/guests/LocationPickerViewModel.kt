package com.sohohouse.seven.guests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.isOpenForBusiness
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.house.HouseType
import com.sohohouse.seven.common.house.LocationPickerType
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.locationlist.LocationCityItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerChildItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerParentItem
import com.sohohouse.seven.network.core.models.Venue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationPickerViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    val houseManager: HouseManager,
    private val venueRepo: VenueRepo,
    private val dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager) {

    private var _selection: String? = null
        set(value) {
            selection.value = value
            field = value
        }

    private var citySelection: List<String>? = null

    var selection = MutableLiveData<String>(_selection)

    private var bookATable: Boolean = false

    private val _items = MutableLiveData<Data>()
    val items: LiveData<Data> get() = _items

    private val _confirmEnabled = MutableLiveData(false)
    val confirmEnabled: LiveData<Boolean> get() = _confirmEnabled

    private val _selectedLocationTab = MutableStateFlow(0)
    val selectedLocationTab get() = _selectedLocationTab.asStateFlow()

    private val _tabIsSelected = MutableStateFlow(false)
    val tabIsSelected get() = _tabIsSelected.asStateFlow()

    private var venues: List<Venue>? = null

    val selectedLocation: LocationType?
        get() =
            if (citySelection.isNullOrEmpty())
                venues?.firstOrNull { _selection == it.id }?.let {
                    LocationType.SingleVenue(
                        it.name, it
                    )
                }
            else
                venues?.filter { citySelection?.contains(it.id) ?: false }?.let { venues ->
                    LocationType.City(
                        venues.first().city,
                        venues.map { filtered -> filtered.id })
                }


    fun init(selectedLocation: String?, isBookATable: Boolean) {
        _selection = selectedLocation
        this.bookATable = isBookATable
        if (isBookATable) {
            fetchRestaurants()
        } else {
            fetchVenues()
        }
    }

    private fun fetchRestaurants() {
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            houseManager.getVenuesForBookATable()
                .fold(ifError = {}, //tODO
                    ifValue = { venues ->
                        venues.takeIf { it.isNotEmpty() }?.let { onVenuesFetched(it) }
                    }, ifEmpty = {})
        }
    }

    private fun fetchVenues() {
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            venueRepo.venues().filterWithTopLevel().filter {
                it.isOpenForBusiness()
            }.takeIf { it.isNotEmpty() }?.let { onVenuesFetched(it) }
        }
    }

    private fun onVenuesFetched(venues: List<Venue>) {
        this.venues = venues

        val houseVenues = venues.filter {
            it.venueType in arrayOf(
                HouseType.HOUSE.name,
                HouseType.STUDIO.name,
                HouseType.CWH.name
            )
        }

        val restaurantVenues = venues.filter { it.venueType == HouseType.RESTAURANT.name }

        val houseItems = (houseManager.organizeHousesForLocationRecyclerView(
            houseVenues,
            hasDisabledState = false,
            includeCWH = !bookATable,
            includeStudios = true,
            selectedLocationList = listOfNotNull(_selection),
            includeAccessibleVenuesOnly = true,
            includeOpenHousesOnly = true,
            includeFavourites = !bookATable
        ))

        val restaurantItems =
            if (bookATable)
                houseManager.organizeHousesForLocationRecyclerView(
                    restaurantVenues,
                    hasDisabledState = false,
                    includeCWH = !bookATable,
                    selectedLocationList = listOfNotNull(_selection),
                    includeAccessibleVenuesOnly = true,
                    includeRestaurants = true,
                    includeFavourites = false,
                    showVenueIcon = false
                )
            else
                Triple(emptyList(), emptyList(), emptyList())

        val cityVenues =
            venues.filter { it.venueType != HouseType.GYM.name && it.city.isNotEmpty() }
                .groupBy { it.city }
                .filter { it.value.size > 1 }

        val cityItems = if (bookATable && !cityVenues.isNullOrEmpty())
            houseManager.getOrganizedVenuesByCity(_selection, cityVenues)
        else
            emptyList()

        Data(houseItems, restaurantItems, cityItems).let {
            _items.postValue(it)
            rememberSelectedTab(it)
        }
    }

    fun onSelection(venueIds: List<String>) {
        if (venueIds.size > 1) {
            this.citySelection = venueIds
            this._selection = null
        } else {
            this._selection = venueIds.first()
            this.citySelection = null
        }
        enableConfirmation()
    }

    private fun rememberSelectedTab(
        data: Data
    ) {
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            when {
                data.houses.third.flatMap { it.childList }
                    .any { it.selected } -> {
                    _selectedLocationTab.emit(LocationPickerType.HOUSE.id)
                }

                data.restaurants.third.flatMap { it.childList }
                    .any { it.selected } -> _selectedLocationTab.emit(
                    LocationPickerType.RESTAURANT.id
                )

                data.cities.any { it.selected } -> _selectedLocationTab.emit(
                    LocationPickerType.CITY.id
                )
            }
            _tabIsSelected.emit(true)
        }
    }

    private fun enableConfirmation() {
        _confirmEnabled.value =
            !this.citySelection.isNullOrEmpty() || !this._selection.isNullOrEmpty()
    }

    data class Data(
        val houses: Triple<List<String>, List<LocationRecyclerChildItem>, List<LocationRecyclerParentItem>>,
        val restaurants: Triple<List<String>, List<LocationRecyclerChildItem>, List<LocationRecyclerParentItem>>,
        val cities: List<LocationCityItem>
    )
}
