package com.sohohouse.seven.book.filter

import android.annotation.SuppressLint
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.filter.Filter
import com.sohohouse.seven.base.filter.FilterType
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.ErrorViewStateViewModel
import com.sohohouse.seven.base.mvvm.ErrorViewStateViewModelImpl
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.events.ExploreCategoryManager
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.common.views.categorylist.CategoryDataItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerChildItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerParentItem
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.EventCategory
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.*
import javax.inject.Inject

class BookFilterViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private var filterStorageManager: BookFilterManager,
    private val exploreCategoryManager: ExploreCategoryManager,
    private val ioDispatcher: CoroutineDispatcher,
    private val venueRepo: VenueRepo,
    private val houseManager: HouseManager,
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    ErrorViewStateViewModel by ErrorViewStateViewModelImpl() {

    private val _eventsFlow = MutableSharedFlow<UiEvent>(4, 3)
    val eventsFlow = _eventsFlow.asSharedFlow()

    var draftFilter: Filter = Filter()
    lateinit var filterType: FilterType
    lateinit var favouriteHousesData: List<LocationRecyclerChildItem>
    lateinit var allHousesData: List<LocationRecyclerParentItem>
    lateinit var allCategoryDataItems: List<CategoryDataItem>

    private val waitForPreFetchJob = Job()

    var eventType: EventType = EventType.MEMBER_EVENT
        set(value) {
            field = value
            trackFilterSelected()
        }

    private fun trackFilterSelected() {
        when (eventType) {
            EventType.MEMBER_EVENT -> {
                when (filterType) {
                    FilterType.LOCATION -> analyticsManager.logEventAction(AnalyticsManager.Action.EventsFilterLocation)
                    FilterType.DATE -> analyticsManager.logEventAction(AnalyticsManager.Action.EventsFilterDate)
                    FilterType.CATEGORIES -> analyticsManager.logEventAction(AnalyticsManager.Action.EventsFilterCategory)
                }
            }
            EventType.CINEMA_EVENT -> {
                when (filterType) {
                    FilterType.LOCATION -> analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsFilterLocation)
                    FilterType.DATE -> analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsFilterDate)
                    FilterType.CATEGORIES -> analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsFilterCategory)
                }
            }
            EventType.FITNESS_EVENT -> {
                when (filterType) {
                    FilterType.LOCATION -> analyticsManager.logEventAction(AnalyticsManager.Action.GymFilterLocation)
                    FilterType.DATE -> analyticsManager.logEventAction(AnalyticsManager.Action.GymFilterDate)
                    FilterType.CATEGORIES -> analyticsManager.logEventAction(AnalyticsManager.Action.GymFilterCategory)
                }
            }
            EventType.HOUSE_VISIT -> {
                when (filterType) {
                    FilterType.LOCATION -> analyticsManager.logEventAction(AnalyticsManager.Action.HouseVisitFilterLocation)
                    FilterType.DATE -> analyticsManager.logEventAction(AnalyticsManager.Action.HouseVisitFilterDate)
                    FilterType.CATEGORIES -> analyticsManager.logEventAction(AnalyticsManager.Action.HouseVisitFilterCategory)
                }
            }
        }
    }

    private var isDraftFilterInitialized: Boolean = false

    init {
        setScreenNameInternal(AnalyticsManager.Screens.Filter.name)
        //Fetching category data just to show/hide Category tab
        if (!::allCategoryDataItems.isInitialized) {
            loadCategoryDataForFirstTime()
        }
    }

    fun saveSelectionInfo() {
        val storedFilter = filterStorageManager.getFilterFromEventType(eventType)
        storedFilter.selectedLocationList = draftFilter.selectedLocationList
        storedFilter.selectedStartDate = draftFilter.selectedStartDate
        storedFilter.selectedEndDate = draftFilter.selectedEndDate
        storedFilter.selectedCategoryList = draftFilter.selectedCategoryList
        filterStorageManager.setFavourites(eventType, draftFilter.selectedCategoryList)
    }

    fun onDataFiltered() {
        when (eventType) {
            EventType.MEMBER_EVENT -> {
                when (filterType) {
                    FilterType.LOCATION -> analyticsManager.logEventAction(AnalyticsManager.Action.EventsFilterLocationConfirm)
                    FilterType.DATE -> analyticsManager.logEventAction(AnalyticsManager.Action.EventsFilterDateConfirm)
                    FilterType.CATEGORIES -> analyticsManager.logEventAction(AnalyticsManager.Action.EventsFilterCategoryConfirm)
                }
                setScreenNameInternal(AnalyticsManager.Screens.EventCategories.name)
            }
            EventType.CINEMA_EVENT -> {
                when (filterType) {
                    FilterType.LOCATION -> analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsFilterLocationConfirm)
                    FilterType.DATE -> analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsFilterDateConfirm)
                    FilterType.CATEGORIES -> analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsFilterCategoryConfirm)
                }
                setScreenNameInternal(AnalyticsManager.Screens.ScreeningsFiltered.name)
            }
            EventType.FITNESS_EVENT -> {
                when (filterType) {
                    FilterType.LOCATION -> analyticsManager.logEventAction(AnalyticsManager.Action.GymFilterLocationConfirm)
                    FilterType.DATE -> analyticsManager.logEventAction(AnalyticsManager.Action.GymFilterDateConfirm)
                    FilterType.CATEGORIES -> analyticsManager.logEventAction(AnalyticsManager.Action.GymFilterCategoryConfirm)
                }
                setScreenNameInternal(AnalyticsManager.Screens.FitnessFiltered.name)
            }
        }
    }

    private fun preFetchSelectedFilterInfo() {
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
            waitForPreFetchJob.cancel()
        }
    }

    private fun onLocationDataReadyForFirstTime() {
        saveSelectionInfo()
        onDataReady()
    }

    private fun onLocationDataReadyWithData() {
        syncLocationSelection()
        onDataReady()
    }

    @SuppressLint("CheckResult")
    fun loadCategoryDataForFirstTime() {
        exploreCategoryManager.getCategories()
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorViewStateTransformer())
            .subscribe(Consumer {
                when (it) {
                    is Either.Error -> {
                        allCategoryDataItems = emptyList()
                    }
                    is Either.Value -> {
                        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
                            CoroutineScope(waitForPreFetchJob).launch {}
                            waitForPreFetchJob.join()
                            onCategoriesReceived(it.value)
                        }
                    }
                }
            })
    }

    private fun onCategoriesReceived(categories: List<EventCategory>) {
        val eventFilter = eventType.typeFilter
        val categoryList = mutableListOf<CategoryDataItem>()
        for (eventCategory in categories) {
            eventCategory.eventTypes?.let { eventTypes ->
                for (type in eventTypes) {
                    if (eventFilter.filter.contains(type)) {
                        categoryList.add(
                            CategoryDataItem(
                                eventCategory.id,
                                eventCategory.name,
                                eventCategory.icon?.png,
                                draftFilter
                                    .selectedCategoryList?.contains(eventCategory.id) ?: false
                            )
                        )
                        break
                    }
                }
            }
        }
        allCategoryDataItems = categoryList
        checkCategoryTabNeeds()
    }

    fun checkCategoryTabNeeds() {
        if (::allCategoryDataItems.isInitialized && allCategoryDataItems.isNotEmpty())
            onCategoryLoaded()
    }

    private fun onCategoryDataReadyWithData() {
        onDataReady()
    }

    private fun onDataReady() {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            _eventsFlow.emit(UiEvent.EnableFilterButton(true))
            _eventsFlow.emit(UiEvent.OnDataReady)
        }
    }

    private fun onCategoryLoaded() {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            _eventsFlow.emit(UiEvent.ShowCategoryTab)
        }
    }

    private fun updateFilterButton() {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            _eventsFlow.emit(UiEvent.EnableFilterButton(true))
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

    fun resetToDefaultSelection() {
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

        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            _eventsFlow.emit(UiEvent.ResetFilterSelection)
            _eventsFlow.emit(UiEvent.EnableFilterButton(true))
        }
    }

    private fun isAllCategoryItemsInitialized() = ::allCategoryDataItems.isInitialized

    fun getFavouriteHouseData() = favouriteHousesData

    fun getAllCategories() = allCategoryDataItems


    private fun syncLocationSelection() {
        draftFilter.selectedLocationList?.let {
            for (item in favouriteHousesData) {
                item.selected = it.contains(item.id)
            }
        } ?: run {
            val selectedItemList = mutableListOf<String>()
            for (item in favouriteHousesData) {
                selectedItemList.add(item.id)
                item.selected = true
            }
            draftFilter.selectedLocationList = selectedItemList
        }

        draftFilter.selectedLocationList?.let {
            for (parentItem in allHousesData) {
                for (childItem in parentItem.childList) {
                    childItem.selected = it.contains(childItem.id)
                }
            }
        }
    }

    fun updateFilterType(filterType: FilterType) {
        this.filterType = filterType
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            _eventsFlow.emit(UiEvent.SwapFilterType(filterType))
        }
    }

    @SuppressLint("CheckResult")
    fun fetchSelectedFilterInfo() {
        preFetchSelectedFilterInfo()
        if (filterType == FilterType.LOCATION && !::allHousesData.isInitialized) {
            Single.just(venueRepo.venues().filterWithTopLevel())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(loadTransformer())
                .subscribe(Consumer {
                    val organizedData = houseManager.organizeHousesForLocationRecyclerView(
                        it,
                        false,
                        draftFilter.selectedLocationList,
                        includeStudios = false
                    )
                    draftFilter.selectedLocationList = organizedData.first
                    favouriteHousesData = organizedData.second
                    allHousesData = organizedData.third
                    onLocationDataReadyForFirstTime()
                })
        } else if (filterType == FilterType.LOCATION) {
            syncLocationSelection()
            onLocationDataReadyWithData()
        } else {
            onCategoryDataReadyWithData()
        }
    }

    override fun reloadDataAfterError() {
        fetchSelectedFilterInfo()
    }

    sealed class UiEvent {
        data class EnableFilterButton(val enable: Boolean) : UiEvent()
        object OnDataReady : UiEvent()
        object ShowCategoryTab : UiEvent()
        object ResetFilterSelection : UiEvent()
        data class SwapFilterType(val filterType: FilterType) : UiEvent()
    }
}
