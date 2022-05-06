package com.sohohouse.seven.book.events

import androidx.paging.PageKeyedDataSource
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.book.base.DataSourceHelper
import com.sohohouse.seven.book.filter.BookFilterManager
import com.sohohouse.seven.common.utils.ErrorInteractor
import com.sohohouse.seven.common.utils.ZipRequestsUtil
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.views.EventStatusHelper
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.common.views.ExploreListFactory
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.EventCategory
import com.sohohouse.seven.network.core.request.GetEventsRequest.Companion.getFeaturedEvents
import com.sohohouse.seven.network.core.request.GetEventsRequest.Companion.getPastDigitalEvents
import kotlinx.coroutines.runBlocking

class EventsDataSourceHelper(
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
) : DataSourceHelper(
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
) {

    override fun loadInitial(
        params: PageKeyedDataSource.LoadInitialParams<Int>,
        callback: (List<DiffItem>) -> Unit
    ) = runBlocking {
        setLoadingState(LoadingState.Loading)
        if (isFiltered) {
            zipRequestsUtil.issueApiCall(
                getAllEventsRequest(1)
            ).fold(
                ifValue = { callback(buildItems(it)) },
                ifEmpty = { callback(emptyList()) },
                ifError = { showError(it.toString()) }
            )
        } else {
            errorInteractor.tripError(
                zipRequestsUtil.issueApiCall(
                    one = getFeaturedEvents(
                        eventTypeFilter = eventType.typeFilter,
                        locationFilters = locations.toTypedArray(),
                        startDate = startDate,
                        endsAtFrom = endsAtFrom
                    ),
                    two = getAllEventsRequest(1),
                    three = getPastDigitalEvents(page = 1, perPage = 10)
                )
            ).fold(
                ifValue = { callback(buildItems(it)) },
                ifEmpty = { callback(emptyList()) },
                ifError = { showError(it.toString()) }
            )
        }
        setLoadingState(LoadingState.Idle)
    }

    override fun buildItems(events: List<Event>): List<DiffItem> {
        return exploreFactory.createExploreEventsItems(
            venues = venues,
            isFiltered = isFiltered,
            allList = events,
            categories = eventCategories
        )
    }

    override fun buildItems(triple: Triple<List<Event>, List<Event>, List<Event>>): List<DiffItem> {
        return exploreFactory.createExploreEventsItems(
            featuredList = triple.first,
            venues = venues,
            isFiltered = isFiltered,
            allList = triple.second,
            categories = eventCategories,
            digitalEvents = triple.third
        )
    }
}