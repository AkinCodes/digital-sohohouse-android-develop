package com.sohohouse.seven.book.filter

import com.sohohouse.seven.base.filter.Filter
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.views.EventType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookFilterManager @Inject constructor(val userManager: UserManager) {
    private var filter: Filter = Filter(userManager.localHouseId, userManager.favouriteHouses)
    private val categoryFavourites: MutableMap<EventType, List<String>?> =
        mutableMapOf(
            Pair(EventType.MEMBER_EVENT, null),
            Pair(EventType.CINEMA_EVENT, null),
            Pair(EventType.FITNESS_EVENT, null),
            Pair(EventType.HOUSE_VISIT, null)
        )

    init {
        userManager.liveFavouriteHouses.observeForever {
            filter.selectedLocationList = it
        }
    }

    private fun getLocalHouse() = userManager.localHouseId
    private fun getFavouriteHouses() = userManager.favouriteHouses

    fun getFilterFromEventType(eventType: EventType): Filter {
        filter.selectedCategoryList = categoryFavourites[eventType]
        return filter
    }

    fun setFavourites(eventType: EventType, favourites: List<String>?) {
        categoryFavourites[eventType] = favourites
    }

    fun isDefaultSelection(eventType: EventType): Boolean {
        val filter = getFilterFromEventType(eventType)
        val selectedLocations = filter.selectedLocationList
        val categoryList = filter.selectedCategoryList ?: listOf()
        val startDate = filter.selectedStartDate

        val favouriteHouses = getFavouriteHouses()
        val defaultHouses = favouriteHouses.toMutableList()
        defaultHouses.add(getLocalHouse())

        return selectedLocations?.let {
            return it.sortedDescending() == defaultHouses.distinct().sortedDescending() &&
                    startDate == Filter.defaultDate && filter.selectedEndDate == null && categoryList.isEmpty()
        } ?: false
    }

    fun getDefaultSelection(): List<String> {
        val defaultList = getFavouriteHouses().toMutableList()
        val localHouse = getLocalHouse()
        if (!defaultList.contains(localHouse)) {
            defaultList.add(localHouse)
        }
        return if (userManager.subscriptionType == SubscriptionType.FRIENDS)
            emptyList()
        else defaultList
    }

    private fun resetToDefaultSelection(eventType: EventType) {
        val filter = getFilterFromEventType(eventType)
        filter.selectedLocationList = getDefaultSelection()
        filter.selectedStartDate = Filter.defaultDate
        filter.selectedEndDate = null
        categoryFavourites[eventType] = null
    }

    fun resetAllFiltersToDefaultSelection() {
        categoryFavourites.forEach { resetToDefaultSelection(it.key) }
    }

    fun clearData() {
        filter = Filter()
        categoryFavourites.keys.forEach { categoryFavourites[it] = null }
    }

    fun updateLocationFilter(addedVenues: List<String>, deletedVenues: List<String>) {
        val selectedList = filter.selectedLocationList?.toMutableList() ?: mutableListOf()
        selectedList.addAll(addedVenues)
        selectedList.removeAll(deletedVenues)
        filter.selectedLocationList = selectedList
    }
}
