package com.sohohouse.seven.book.base

import androidx.paging.PageKeyedDataSource
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.filter.Filter
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.book.adapter.model.ListEvent
import com.sohohouse.seven.book.cinema.CinemaDataSourceHelper
import com.sohohouse.seven.book.events.EventsDataSourceHelper
import com.sohohouse.seven.book.filter.BookFilterManager
import com.sohohouse.seven.book.fitness.FitnessDataSourceHelper
import com.sohohouse.seven.common.extensions.getApiFormattedDate
import com.sohohouse.seven.common.extensions.getFilterApiFormattedDate
import com.sohohouse.seven.common.extensions.getLocationColor
import com.sohohouse.seven.common.utils.ErrorInteractor
import com.sohohouse.seven.common.utils.ZipRequestsUtil
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.views.EventStatusHelper
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.common.views.ExploreListFactory
import com.sohohouse.seven.housevisit.HouseVisitDataSourceHelper
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.EventCategory
import com.sohohouse.seven.network.core.request.GetEventsRequest
import com.sohohouse.seven.network.core.request.GetEventsRequest.Companion.MAX_OTHER_EVENTS_PER_CALL
import com.sohohouse.seven.network.core.request.GetEventsRequest.Companion.getFeaturedEvents
import kotlinx.coroutines.runBlocking
import java.util.*


abstract class DataSourceHelper(
    protected val eventType: EventType,
    protected val zipRequestsUtil: ZipRequestsUtil,
    protected val filterManager: BookFilterManager,
    protected val errorInteractor: ErrorInteractor,
    protected val exploreFactory: ExploreListFactory,
    protected val eventStatusHelper: EventStatusHelper,
    protected val venues: VenueList,
    protected val eventCategories: List<EventCategory>,
    loadable: Loadable.ViewModel,
    errorable: Errorable.ViewModel
) : Loadable.ViewModel by loadable,
    Errorable.ViewModel by errorable {

    protected val filter: Filter
        get() = filterManager.getFilterFromEventType(eventType)

    protected val isFiltered: Boolean
        get() = filterManager.isDefaultSelection(eventType).not()

    protected open val locations: List<String>
        get() = filter.selectedLocationList ?: listOf()

    protected val startDate: String
        get() = if (isFiltered) filter.selectedStartDate.getFilterApiFormattedDate() else Filter.defaultDate.getFilterApiFormattedDate()

    protected val endsAtFrom: String
        get() = Calendar.getInstance().time.getApiFormattedDate()

    private val filterCategories: List<String>
        get() = filter.selectedCategoryList ?: listOf()

    private val endDate: String?
        get() = if (isFiltered) filter.selectedEndDate?.getFilterApiFormattedDate() else null

    protected fun getAllEventsRequest(page: Int): GetEventsRequest {
        return GetEventsRequest.getAllEvents(
            eventTypeFilter = eventType.typeFilter,
            locationFilters = locations.toTypedArray(),
            filterCategories = filterCategories.toTypedArray(),
            startDate = startDate,
            endDate = endDate,
            endsAtFrom = endsAtFrom,
            page = page,
            perPage = MAX_OTHER_EVENTS_PER_CALL
        )
    }

    open fun loadBefore(
        params: PageKeyedDataSource.LoadParams<Int>,
        callback: (List<DiffItem>) -> Unit
    ) {
    }

    open fun loadInitial(
        params: PageKeyedDataSource.LoadInitialParams<Int>,
        callback: (List<DiffItem>) -> Unit
    ) = runBlocking {
        setLoadingState(LoadingState.Loading)
        if (isFiltered) {
            zipRequestsUtil.issueApiCall(getAllEventsRequest(1)).fold(
                ifValue = { callback(buildItems(it)) },
                ifEmpty = { callback(emptyList()) },
                ifError = { showError(it.toString()) }
            )
        } else {
            errorInteractor.pairError(
                zipRequestsUtil.issueApiCall(
                    one = getFeaturedEvents(
                        eventTypeFilter = eventType.typeFilter,
                        locationFilters = locations.toTypedArray(),
                        startDate = startDate,
                        endsAtFrom = Calendar.getInstance().time.getApiFormattedDate()
                    ),
                    two = getAllEventsRequest(1)
                )
            ).fold(
                ifValue = { callback(buildItems(it)) },
                ifEmpty = { callback(emptyList()) },
                ifError = { showError(it.toString()) }
            )
        }
        setLoadingState(LoadingState.Idle)
    }

    open fun loadAfter(
        params: PageKeyedDataSource.LoadParams<Int>,
        callback: (List<DiffItem>) -> Unit
    ) {
        setLoadingState(LoadingState.Loading)
        zipRequestsUtil.issueApiCall(getAllEventsRequest(params.key)).fold(
            ifValue = { events ->
                val data = events.map { event ->
                    val venue = venues.findById(event.venue?.get()?.id)
                    val eventStatus = eventStatusHelper.getRestrictedEventStatus(event, venue)
                    val category = eventCategories.firstOrNull { it.id == event.category }
                    ListEvent(
                        event = event,
                        venueName = venue?.name ?: "",
                        venueColor = venue?.getLocationColor() ?: "",
                        venueTimeZone = venue?.timeZone,
                        eventStatus = eventStatus,
                        categoryName = category?.name,
                        categoryUrl = category?.icon?.png
                    )
                }
                callback(data)
            },
            ifEmpty = { callback(emptyList()) },
            ifError = { showError(it.toString()) }
        )
        setLoadingState(LoadingState.Idle)
    }

    protected open fun buildItems(events: List<Event>): List<DiffItem> = emptyList()

    protected open fun buildItems(pair: Pair<List<Event>, List<Event>>): List<DiffItem> =
        emptyList()

    protected open fun buildItems(triple: Triple<List<Event>, List<Event>, List<Event>>): List<DiffItem> =
        emptyList()

    companion object {
        fun init(
            event: EventType,
            zipRequestsUtil: ZipRequestsUtil,
            filterManager: BookFilterManager,
            errorInteractor: ErrorInteractor,
            exploreFactory: ExploreListFactory,
            eventStatusHelper: EventStatusHelper,
            venues: VenueList,
            categories: List<EventCategory>,
            loadable: Loadable.ViewModel,
            errorable: Errorable.ViewModel
        ): DataSourceHelper {
            return when (event) {
                EventType.HOUSE_VISIT -> HouseVisitDataSourceHelper(
                    event,
                    zipRequestsUtil,
                    filterManager,
                    errorInteractor,
                    exploreFactory,
                    eventStatusHelper,
                    venues,
                    categories,
                    loadable,
                    errorable
                )
                EventType.MEMBER_EVENT -> EventsDataSourceHelper(
                    event,
                    zipRequestsUtil,
                    filterManager,
                    errorInteractor,
                    exploreFactory,
                    eventStatusHelper,
                    venues,
                    categories,
                    loadable,
                    errorable
                )
                EventType.CINEMA_EVENT -> CinemaDataSourceHelper(
                    event,
                    zipRequestsUtil,
                    filterManager,
                    errorInteractor,
                    exploreFactory,
                    eventStatusHelper,
                    venues,
                    categories,
                    loadable,
                    errorable
                )
                EventType.FITNESS_EVENT -> FitnessDataSourceHelper(
                    event,
                    zipRequestsUtil,
                    filterManager,
                    errorInteractor,
                    exploreFactory,
                    eventStatusHelper,
                    venues,
                    categories,
                    loadable,
                    errorable
                )
            }
        }
    }
}