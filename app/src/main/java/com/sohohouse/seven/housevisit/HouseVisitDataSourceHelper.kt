package com.sohohouse.seven.housevisit

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
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

class HouseVisitDataSourceHelper(
    eventType: EventType,
    zipRequestsUtil: ZipRequestsUtil,
    filterManager: BookFilterManager,
    errorInteractor: ErrorInteractor,
    exploreFactory: ExploreListFactory,
    eventStatusHelper: EventStatusHelper,
    venues: VenueList,
    eventCategories: List<EventCategory>,
    loadable: Loadable.ViewModel,
    errorable: Errorable.ViewModel
) : DataSourceHelper(
    eventType,
    zipRequestsUtil,
    filterManager,
    errorInteractor,
    exploreFactory,
    eventStatusHelper,
    venues,
    eventCategories,
    loadable,
    errorable
) {

    override fun buildItems(events: List<Event>): List<DiffItem> {
        return exploreFactory.createHouseVisitItems(
            screeningList = events,
            isFiltered = true,
            venues = venues
        )
    }

    override fun buildItems(pair: Pair<List<Event>, List<Event>>): List<DiffItem> {
        return exploreFactory.createHouseVisitItems(
            featuredList = pair.first,
            screeningList = pair.second,
            venues = venues
        )
    }
}