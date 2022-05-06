package com.sohohouse.seven.book.filter

import android.annotation.SuppressLint
import com.sohohouse.seven.base.filter.BaseFilterPresenter
import com.sohohouse.seven.base.filter.Filter
import com.sohohouse.seven.base.filter.FilterType.*
import com.sohohouse.seven.base.filter.FilterViewController
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.events.ExploreCategoryManager
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.common.views.categorylist.CategoryDataItem
import com.sohohouse.seven.network.base.model.Either
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class BookFilterPresenter @Inject constructor(
    private var filterStorageManager: BookFilterManager,
    authInteractor: ZipRequestsUtil,
    houseManager: HouseManager,
    private val exploreCategoryManager: ExploreCategoryManager,
    private val analyticsManager: AnalyticsManager,
    venueRepo: VenueRepo
) : BaseFilterPresenter<FilterViewController>(
    authInteractor,
    houseManager,
    analyticsManager,
    venueRepo
) {

    companion object {
        private const val TAG = "ExploreFilterPresenter"
    }

    var eventType: EventType = EventType.MEMBER_EVENT
        set(value) {
            field = value
            trackFilterSelected()
        }

    private fun trackFilterSelected() {
        when (eventType) {
            EventType.MEMBER_EVENT -> {
                when (filterType) {
                    LOCATION -> analyticsManager.logEventAction(AnalyticsManager.Action.EventsFilterLocation)
                    DATE -> analyticsManager.logEventAction(AnalyticsManager.Action.EventsFilterDate)
                    CATEGORIES -> analyticsManager.logEventAction(AnalyticsManager.Action.EventsFilterCategory)
                }
            }
            EventType.CINEMA_EVENT -> {
                when (filterType) {
                    LOCATION -> analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsFilterLocation)
                    DATE -> analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsFilterDate)
                    CATEGORIES -> analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsFilterCategory)
                }
            }
            EventType.FITNESS_EVENT -> {
                when (filterType) {
                    LOCATION -> analyticsManager.logEventAction(AnalyticsManager.Action.GymFilterLocation)
                    DATE -> analyticsManager.logEventAction(AnalyticsManager.Action.GymFilterDate)
                    CATEGORIES -> analyticsManager.logEventAction(AnalyticsManager.Action.GymFilterCategory)
                }
            }
            EventType.HOUSE_VISIT -> {
                when (filterType) {
                    LOCATION -> analyticsManager.logEventAction(AnalyticsManager.Action.HouseVisitFilterLocation)
                    DATE -> analyticsManager.logEventAction(AnalyticsManager.Action.HouseVisitFilterDate)
                    CATEGORIES -> analyticsManager.logEventAction(AnalyticsManager.Action.HouseVisitFilterCategory)
                }
            }
        }
    }

    private var isDraftFilterInitialized: Boolean = false

    init {
        draftFilter = Filter()
    }

    override fun saveSelectionInfo() {
        val storedFilter = filterStorageManager.getFilterFromEventType(eventType)
        storedFilter.selectedLocationList = draftFilter.selectedLocationList
        storedFilter.selectedStartDate = draftFilter.selectedStartDate
        storedFilter.selectedEndDate = draftFilter.selectedEndDate
        storedFilter.selectedCategoryList = draftFilter.selectedCategoryList
        filterStorageManager.setFavourites(eventType, draftFilter.selectedCategoryList)
    }

    override fun onDataFiltered() {
        when (eventType) {
            EventType.MEMBER_EVENT -> {
                when (filterType) {
                    LOCATION -> analyticsManager.logEventAction(AnalyticsManager.Action.EventsFilterLocationConfirm)
                    DATE -> analyticsManager.logEventAction(AnalyticsManager.Action.EventsFilterDateConfirm)
                    CATEGORIES -> analyticsManager.logEventAction(AnalyticsManager.Action.EventsFilterCategoryConfirm)
                }
                executeWhenAvailable { view, _, _ -> view.setScreenName(AnalyticsManager.Screens.EventCategories.name) }
            }
            EventType.CINEMA_EVENT -> {
                when (filterType) {
                    LOCATION -> analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsFilterLocationConfirm)
                    DATE -> analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsFilterDateConfirm)
                    CATEGORIES -> analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsFilterCategoryConfirm)
                }
                executeWhenAvailable { view, _, _ -> view.setScreenName(AnalyticsManager.Screens.ScreeningsFiltered.name) }
            }
            EventType.FITNESS_EVENT -> {
                when (filterType) {
                    LOCATION -> analyticsManager.logEventAction(AnalyticsManager.Action.GymFilterLocationConfirm)
                    DATE -> analyticsManager.logEventAction(AnalyticsManager.Action.GymFilterDateConfirm)
                    CATEGORIES -> analyticsManager.logEventAction(AnalyticsManager.Action.GymFilterCategoryConfirm)
                }
                executeWhenAvailable { view, _, _ -> view.setScreenName(AnalyticsManager.Screens.FitnessFiltered.name) }
            }
        }
    }

    override fun preFetchSelectedFilterInfo() {
        if (!isDraftFilterInitialized) {
            val storedFilter = filterStorageManager.getFilterFromEventType(eventType)
            draftFilter.selectedLocationList = draftFilter.selectedLocationList
                ?: storedFilter.selectedLocationList
            draftFilter.selectedStartDate =
                if (draftFilter.selectedStartDate.after(storedFilter.selectedStartDate)) {
                    draftFilter.selectedStartDate
                } else {
                    storedFilter.selectedStartDate
                }
            draftFilter.selectedEndDate = draftFilter.selectedEndDate
                ?: storedFilter.selectedEndDate
            draftFilter.selectedCategoryList = draftFilter.selectedCategoryList
                ?: storedFilter.selectedCategoryList ?: listOf()
            isDraftFilterInitialized = true
        }
    }

    override fun onLocationDataReadyForFirstTime() {
        saveSelectionInfo()
        onDataReady()
    }

    override fun onLocationDataReadyWithData() {
        syncLocationSelection()
        onDataReady()
    }

    @SuppressLint("CheckResult")
    override fun loadCategoryDataForFirstTime() {
        exploreCategoryManager.getCategories()
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorViewStateTransformer())
            .subscribe(Consumer {
                when (it) {
                    is Either.Error -> {
                        allCategoryDataItems = emptyList()
                        Timber.tag(TAG).d(it.error.toString())
                    }
                    is Either.Value -> {
                        val response = it.value
                        val eventFilter = eventType.typeFilter
                        val categoryList = mutableListOf<CategoryDataItem>()
                        for (eventCategory in response) {
                            eventCategory.eventTypes?.let { eventTypes ->
                                for (type in eventTypes) {
                                    if (eventFilter.filter.contains(type)) {
                                        categoryList.add(
                                            CategoryDataItem(
                                                eventCategory.id,
                                                eventCategory.name,
                                                eventCategory.icon?.png,
                                                draftFilter.selectedCategoryList?.contains(
                                                    eventCategory.id
                                                )
                                                    ?: false
                                            )
                                        )
                                        break
                                    }
                                }
                            }
                        }
                        allCategoryDataItems = categoryList
                        if (allCategoryDataItems.isNotEmpty()) onCategoryLoaded()
                    }
                }
            })
    }

    override fun onCategoryDataReadyWithData() {
        onDataReady()
    }

    private fun onDataReady() {
        executeWhenAvailable { view, _, _ ->
            updateFilterButton()
            view.onDataReady()
        }
    }

    private fun onCategoryLoaded() {
        executeIfAvailable {
            view.showCategoryTab()
        }
    }

    private fun updateFilterButton() {
        executeWhenAvailable { view, _, _ ->
            //            view.enableFilterButton(draftFilter.selectedLocationList?.isNotEmpty() ?: false)
            view.enableFilterButton(true)
        }
    }

    fun updateLocationSelection(locationList: List<String>) {
        draftFilter.selectedLocationList = locationList
        updateFilterButton()
    }

    fun updateDateSelection(date: Date?, isStartDate: Boolean) {
        if (isStartDate) {
            date?.let { draftFilter.selectedStartDate = it }
        } else {
            draftFilter.selectedEndDate = date
        }

        updateFilterButton()
    }

    fun updateCategorySelection(selectedItems: List<String>) {
        draftFilter.selectedCategoryList = selectedItems
    }

    override fun resetToDefaultSelection() {
        when (eventType) {
            EventType.MEMBER_EVENT -> analyticsManager.track(AnalyticsEvent.Explore.Events.FilterReset)
            EventType.CINEMA_EVENT -> analyticsManager.track(AnalyticsEvent.Explore.Cinema.FilterReset)
            EventType.FITNESS_EVENT -> analyticsManager.track(AnalyticsEvent.Explore.Fitness.FilterReset)
            EventType.HOUSE_VISIT -> analyticsManager.track(AnalyticsEvent.Explore.HouseVisit.FilterReset)
        }
        draftFilter.selectedLocationList = filterStorageManager.getDefaultSelection()
        draftFilter.selectedStartDate = Filter.defaultDate
        draftFilter.selectedEndDate = null
        draftFilter.selectedCategoryList = listOf()
        if (isAllCategoryItemsInitialized()) {
            allCategoryDataItems =
                allCategoryDataItems.map { CategoryDataItem(it.id, it.name, it.imageUrl, false) }
        }

        executeWhenAvailable { view, _, _ ->
            view.resetFilterSelection()
            updateFilterButton()
        }
    }

    fun getFavouriteHouseData() = favouriteHousesData
    fun getAllCategories() = allCategoryDataItems
}
