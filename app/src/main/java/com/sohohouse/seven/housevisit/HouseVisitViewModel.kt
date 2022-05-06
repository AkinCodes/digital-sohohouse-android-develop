package com.sohohouse.seven.housevisit

import com.sohohouse.seven.book.adapter.model.EventItem
import com.sohohouse.seven.book.base.BaseBookTabViewModel
import com.sohohouse.seven.book.base.EventsDataSource
import com.sohohouse.seven.book.events.EventCategoryRepository
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.EventType
import javax.inject.Inject

class HouseVisitViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    venueRepo: VenueRepo,
    categoryRepository: EventCategoryRepository,
    firebaseEventTracking: AnalyticsManager,
    dataSourceFactory: EventsDataSource.Factory
) : BaseBookTabViewModel(
    EventType.HOUSE_VISIT,
    analyticsManager,
    categoryRepository,
    firebaseEventTracking,
    dataSourceFactory,
    venueRepo
) {

    override fun logEventClick(item: EventItem, position: Int) {
        firebaseEventTracking.logEventAction(
            if (position == 0 && item.isFeatured) AnalyticsManager.Action.HouseVisitFeatured
            else AnalyticsManager.Action.HouseVisitLatest
        )
    }

    fun logFilterClick() {
        firebaseEventTracking.logEventAction(AnalyticsManager.Action.HouseVisitFilter)
    }

}