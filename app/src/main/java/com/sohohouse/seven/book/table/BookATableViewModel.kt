package com.sohohouse.seven.book.table

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.LiveEvent
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.book.table.model.SelectedLocation
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.models.TableAvailabilities
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.network.utils.LocalDateTimeUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.util.*
import javax.inject.Inject

class BookATableViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    val houseManager: HouseManager,
    dispatcher: CoroutineDispatcher,
    val apiService: SohoApiService,
    private val stringProvider: StringProvider
) : BaseViewModel(analyticsManager, dispatcher), Loadable.ViewModel by Loadable.ViewModelImpl() {

    companion object {
        private const val DEFAULT_GUEST_VALUE = 2
    }

    private var venues: List<Venue> = emptyList()

    private var locations: List<Venue>? = null
    private val date: Date get() = selectedDate.value ?: Date()
    private val time: Date? get() = selectedTime.value
    private val seats: Int get() = selectedSeats.value ?: DEFAULT_GUEST_VALUE

    val selectedLocation = MutableLiveData<SelectedLocation>()
    val selectedDate = MutableLiveData<Date>(Date())
    val selectedTime = MutableLiveData<Date>()
    val selectedSeats = MutableLiveData<Int>(DEFAULT_GUEST_VALUE)
    private val selectedRestaurant = MutableLiveData<Venue>()

    val isUserInputCorrect = MutableLiveData<Boolean>(false)
    val venueRestaurants = MutableLiveData<List<Restaurant>>()
    val listRestaurants = MutableLiveData<List<Restaurant>>(emptyList())
    val listHouses = MutableLiveData<List<Restaurant>>(emptyList())
    val listAlternateRestaurants = MutableLiveData<List<TableBookingDetails>>(emptyList())
    val openBookingDetails = LiveEvent<Pair<TableBookingDetails, Boolean>>()

    val stateTransitions = MutableLiveData(
        TransitionToStateEvent(
            State.SEARCH_FORM,
            animate = false,
            previous = null
        )
    )

    val showRestaurantCarousels: Boolean
        get() = stateTransitions.value?.state in arrayOf(
            State.SEARCH_FORM,
            State.NO_AVAILABILITY
        )

    data class TransitionToStateEvent(
        val state: State,
        val animate: Boolean,
        val previous: TransitionToStateEvent?
    )

    enum class State {
        SEARCH_FORM,
        SELECT_RESTAURANT,
        NO_AVAILABILITY, SELECT_ALTERNATE_RESTAURANT
    }

    init {
        fillCurrentDate()
        fillCurrentTime()
        fetchRestaurants()
    }

    private fun fillCurrentTime() {
        val (hour, minute) = LocalDateTimeUtil.getNext15MinuteIntervalTime(LocalDateTime.now())
            .toDate()
            .hourMinute
        fillTime(hour, minute)
    }

    private fun fillCurrentDate() {
        with(LocalDate.now()) {
            fillDate(year, month.ordinal, dayOfMonth)
        }
    }

    fun backPressed(): Boolean {
        return stateTransitions.value?.previous?.let {
            stateTransitions.value = TransitionToStateEvent(
                it.state,
                animate = true,
                it.previous
            ) //keep the back stack
            true
        } ?: false
    }

    fun fillLocation(item: Restaurant) {
        stateTransitions.value = TransitionToStateEvent(
            State.SEARCH_FORM,
            animate = false,
            previous = null
        ) //clear the back stack
        fillLocation(item.id)
    }

    fun fillLocation(venueId: String) {
        locations = listOfNotNull(venues.find { it.id == venueId })
        getSelectedLocation()
        checkUserInput()
    }

    fun fillCityLocations(venueId: List<String>) {
        locations = venues.filter { venueId.contains(it.id) }
        getSelectedLocation()
        checkUserInput()
    }

    fun fillDate(year: Int, month: Int, dayOfMonth: Int) {
        selectedDate.value = Calendar.getInstance().apply { set(year, month, dayOfMonth) }.time
        checkUserInput()
    }

    fun fillTime(hourOfDay: Int, minute: Int) {
        selectedTime.value =
            Calendar.getInstance().apply { set(1970, 1, 1, hourOfDay, minute) }.time
        checkUserInput()
    }

    fun fillSeats(guest: Int) {
        selectedSeats.value = guest
    }

    fun checkAvailabilityForHouse(id: String) {
        viewModelScope.launch(viewModelContext) {
            setLoading()

            val restaurant = if (locations?.size ?: 0 > 1)
                getAllRestaurants().find { it.id == id }
            else findRestaurant(id)

            val availabilities = apiService.checkTableAvailability(
                id,
                combineDates().getApiFormattedDateIgnoreTimezone(),
                seats
            )

            when (availabilities) {
                is ApiResponse.Success -> onCheckAvailabilitySuccess(
                    availabilities.response,
                    restaurant
                )
                is ApiResponse.Error -> onCheckAvailabilityError()
            }
            setIdle()
        }
    }

    fun checkAvailability() {
        viewModelScope.launch(viewModelContext) {
            setLoading()
            when {
                locations?.size ?: 0 > 1 -> {
                    checkAvailabilityForMultipleRestaurants()
                }
                isLocationSingle() -> {
                    checkAvailabilityForSingleRestaurant()
                }
                else -> {
                    val location = locations?.firstOrNull()
                    val venue = location?.restaurants?.firstOrNull()
                        ?: location
                    analyticsManager.logEventAction(
                        AnalyticsManager.Action.TableBookingCheckAvailability,
                        Bundle().apply { putString("house_id", venue?.id ?: "") })

                    when (val result = apiService.checkTableAvailability(
                        venue?.restaurant?.id ?: "",
                        combineDates().getApiFormattedDateIgnoreTimezone(),
                        seats
                    )) {
                        is ApiResponse.Success -> onCheckAvailabilitySuccess(result.response, venue)
                        is ApiResponse.Error -> onCheckAvailabilityError()
                    }
                }
            }
            setIdle()
        }
    }

    private fun isLocationSingle() =
        locations?.size ?: 0 <= 1 && locations?.any { loc -> loc.restaurants.size > 1 } == true

    private fun getSelectedLocation() {
        val location = if (locations?.size ?: 0 > 1)
            SelectedLocation(name = locations?.find { it.city.isNotEmpty() }?.city ?: "")
        else {
            locations?.firstOrNull()?.let {
                SelectedLocation(id = it.id ?: "", name = it.name)
            }
        }
        location?.let {
            selectedLocation.value = it
        }
    }

    private fun getAllRestaurants(): List<Venue> {
        val restaurants = mutableListOf<Venue>()
        locations?.iterator()?.forEach {
            if (it.restaurant != null)
                restaurants.add(it)
            if (it.restaurants.size >= 1)
                restaurants.addAll(it.restaurants)
        }
        return restaurants
    }

    private fun findRestaurant(restaurantId: String): Venue? {
        venues.let { list ->
            list.filter { it.restaurants.isNotEmpty() }.forEach { venue ->
                venue.restaurants.forEach { if (it.id == restaurantId) return it }
            }
        }

        return null
    }

    private fun fetchRestaurants() {
        viewModelScope.launch(viewModelContext) {
            houseManager.getVenuesForBookATable()
                .fold(ifError = {}, //fail silently; not critical and error is auto-reported
                    ifValue = { venues ->
                        venues.takeIf { it.isNotEmpty() }?.let { onRestaurantsFetched(it) }
                    }, ifEmpty = {})
        }
    }

    private suspend fun checkAvailabilityForMultipleRestaurants() {
        when (val result = apiService.checkTableAvailability(
            getAllRestaurants().filter { it.restaurant?.bookingPartnerId?.isEmpty() == false }
                .joinToString(
                    separator = ","
                ) { it.id },
            combineDates().getApiFormattedDateIgnoreTimezone(),
            seats
        )) {
            is ApiResponse.Success -> onCheckAvailabilityMultipleRestaurantSuccess(
                result.response, getAllRestaurants()
            )
            is ApiResponse.Error -> onCheckAvailabilityError()
        }
    }

    private fun checkAvailabilityForSingleRestaurant() {
        stateTransitions.postValue(
            TransitionToStateEvent(
                State.SELECT_RESTAURANT, animate = true,
                previous = stateTransitions.value
            )
        )
        locations?.firstOrNull()?.let { location ->
            venueRestaurants.postValue(buildRestaurantItem(location))
        }
    }

    private fun onCheckAvailabilityMultipleRestaurantSuccess(
        result: List<TableAvailabilities>, restaurants: List<Venue>
    ) {
        if (result.any { it.time_slots?.isNotEmpty() == true }) {
            stateTransitions.postValue(
                TransitionToStateEvent(
                    State.SELECT_RESTAURANT, animate = true,
                    previous = stateTransitions.value
                )
            )

            venueRestaurants.postValue(restaurants.filter {
                result.filter { available ->
                    available.time_slots?.isNotEmpty() ?: false
                }.map { restaurants -> restaurants.id }.contains(it.id)
            }.map {
                Restaurant(
                    id = it.id ?: "",
                    name = it.restaurant?.bookingPartnerName ?: it.name,
                    imageUrl = it.restaurant?.restaurantImages?.firstOrNull()?.largePng
                        ?: "",
                    address = it.buildAddress(singleLine = true) ?: "",
                    date = combineDates().getFormattedDateTime(""),
                    restaurantUrl = it.restaurant?.restaurantUrl ?: ""
                )
            }.sortedBy { it.name })

        } else {
            onCheckAvailabilityError()
        }
    }

    private fun buildRestaurantItem(location: Venue): List<Restaurant> {
        return location.restaurants.map {
            Restaurant(
                id = it.id,
                name = it.restaurant?.bookingPartnerName ?: it.name,
                imageUrl = it.restaurant?.restaurantImages?.firstOrNull()?.largePng
                    ?: "",
                address = location.buildAddress(singleLine = true),
                date = combineDates().getFormattedDateTime(""),
                restaurantUrl = it.restaurant?.restaurantUrl ?: ""
            )
        }.sortedBy { it.name }
    }

    private fun onCheckAvailabilitySuccess(
        result: List<TableAvailabilities>,
        selectedRestaurant: Venue? = null
    ) {
        this.selectedRestaurant.postValue(selectedRestaurant)
        if (result.first().time_slots?.isNotEmpty() == true) {
            val location = getAllRestaurants().find { it.id == result.first().id }
            val details = if (selectedRestaurant != null) {
                getTableBookingDetails(selectedRestaurant, result.first())
            } else {
                getTableBookingDetails(location, result.first())
            }
            openBookingDetails.postValue(
                Pair(
                    details,
                    stateTransitions.value?.state == State.SELECT_RESTAURANT
                )
            )
        } else {
            onAlternativeRestaurantsFetched(result.filter {
                it.time_slots?.isNotEmpty() == true
            })
        }
    }

    private fun onAlternativeRestaurantsFetched(tableAvailabilitiesList: List<TableAvailabilities>) {
        val alternateRestaurantsList: MutableList<TableBookingDetails> = mutableListOf()
        tableAvailabilitiesList.forEach { tableAvailabilities ->
            val venue = findRestaurant(tableAvailabilities.id)
            venue?.let {
                tableAvailabilitiesList.firstOrNull { it.id == tableAvailabilities.id }?.let {
                    alternateRestaurantsList.add(getTableBookingDetails(venue, it))
                }
            }
        }
        if (alternateRestaurantsList.isNotEmpty()) onAlternateRestaurantsAvailable(
            alternateRestaurantsList
        ) else onCheckAvailabilityError()
    }

    private fun getTableBookingDetails(
        venue: Venue?,
        result: TableAvailabilities
    ): TableBookingDetails {
        return TableBookingDetails(
            id = venue?.id ?: "",
            name = venue?.restaurant?.bookingPartnerName ?: "",
            description = venue?.restaurant?.restaurantDescription ?: "",
            specialNotes = venue?.restaurant?.specialNotes ?: "",
            address = venue?.buildAddress(singleLine = true) ?: "",
            country = venue?.country ?: "",
            houseDetails = venue?.description ?: "",
            imageUrl = venue?.restaurant?.getImage() ?: "",
            menus = venue?.restaurant?.menus?.asList() ?: emptyList(),
            availabilities = result,
            date = combineDates(),
            persons = seats,
            venueId = venue?.id ?: "",
            startTimeMills = System.currentTimeMillis(),
            venueDetails = TableBookingHouseDetailsItem.from(
                venue,
                venues.firstOrNull { it.id == venue?.parentId },
                stringProvider
            ),
            formVenueInput = venue?.name ?: "",
            formTimeInput = time
        )
    }

    private fun onAlternateRestaurantsAvailable(alternateRestaurantsList: List<TableBookingDetails>) {
        stateTransitions.postValue(
            TransitionToStateEvent(
                State.SELECT_ALTERNATE_RESTAURANT, animate = false,
                previous = stateTransitions.value
            )
        )
        listAlternateRestaurants.postValue(alternateRestaurantsList)
        onAlternateRestaurantsSuggestionDisplayed(alternateRestaurantsList.map { it.id })
    }

    private fun onAlternateRestaurantsSuggestionDisplayed(restaurantIds: List<String>) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.TableBookingAlternateSuggestionDisplay,
            Bundle().apply {
                putString("initial_restaurant_id", selectedRestaurant.value?.restaurant?.id)
                putString("suggested_restaurants_ids", restaurantIds.joinToString { "\'${it}\'" })
            })
    }

    fun onAlternateRestaurantsSuggestionChangeClicked() {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.TableBookingAlternateSuggestionChangedClick,
            Bundle().apply {
                putString("initial_restaurant_id", selectedRestaurant.value?.restaurant?.id)
            })
    }

    fun onAlternateRestaurantsSuggestionItemClicked(selectedRestaurantId: String) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.TableBookingAlternateSuggestionItemSelected,
            Bundle().apply {
                putString("initial_restaurant_id", selectedRestaurant.value?.restaurant?.id)
                putString("selected_suggestion_id", selectedRestaurantId)
            })
    }

    private fun onCheckAvailabilityError() {
        stateTransitions.postValue(
            TransitionToStateEvent(
                State.NO_AVAILABILITY, animate = true,
                previous = stateTransitions.value
            )
        ) //keep the back stack
    }

    private fun onRestaurantsFetched(items: List<Venue>) {
        this.venues = items
        this.venues.filter { it.restaurants.isEmpty() }.let {
            listRestaurants.postValue(it.map { restaurantVenue ->
                Restaurant(
                    id = restaurantVenue.id,
                    name = restaurantVenue.name,
                    imageUrl = restaurantVenue.restaurant?.houseImageSet?.largePng ?: "",
                    address = restaurantVenue.buildAddress(true),
                    date = "",
                    restaurantUrl = restaurantVenue.restaurant?.restaurantUrl ?: ""
                )
            })
        }
        this.venues.filter { it.restaurants.isNotEmpty() }.let {
            listHouses.postValue(it.map { restaurantVenue ->
                Restaurant(
                    id = restaurantVenue.id,
                    name = restaurantVenue.name,
                    imageUrl = restaurantVenue.houseDetails.eatAndDrinkImageSet?.fillCrop ?: "",
                    address = restaurantVenue.buildAddress(true),
                    date = "",
                    restaurantUrl = getRestaurantUrlForHouse(restaurantVenue)
                )
            })
        }
    }

    private fun getRestaurantUrlForHouse(restaurant: Venue) =
        (restaurant.restaurant?.restaurantUrl
            ?: restaurant.restaurants.firstOrNull()?.restaurant?.restaurantUrl ?: "")

    private fun combineDates(): Date = Calendar.getInstance().apply {
        time = this@BookATableViewModel.time
        val hour = get(Calendar.HOUR_OF_DAY)
        val mins = get(Calendar.MINUTE)
        time = date
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, mins)
    }.time

    private fun checkUserInput() {
        isUserInputCorrect.value = locations != null && time != null
    }

    fun revertToSelectRestaurantState() {
        stateTransitions.value = TransitionToStateEvent(
            State.SELECT_RESTAURANT,
            animate = false,
            previous = stateTransitions.value
        )  //keep the back stack
    }

    fun onNavigatedToTimeSlots() {
        stateTransitions.value = TransitionToStateEvent(
            State.SEARCH_FORM,
            animate = false,
            previous = null
        ) //clear the back stack
    }

    fun onChangeSearchClick() {
        stateTransitions.value = TransitionToStateEvent(
            State.SEARCH_FORM,
            animate = true,
            previous = null
        ) //clear the back stack
    }

    fun onAlternateRestaurantClick() {
        stateTransitions.value = TransitionToStateEvent(
            State.SELECT_ALTERNATE_RESTAURANT,
            animate = true,
            previous = null
        ) //clear the back stack
    }

}