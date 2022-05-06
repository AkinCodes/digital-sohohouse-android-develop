package com.sohohouse.seven.book.events

import com.sohohouse.seven.book.adapter.model.EventItem
import com.sohohouse.seven.book.base.BaseBookTabViewModel
import com.sohohouse.seven.book.base.EventsDataSource
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.EventType
import javax.inject.Inject

class EventsViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    categoryRepository: EventCategoryRepository,
    firebaseEventTracking: AnalyticsManager,
    dataSourceFactory: EventsDataSource.Factory,
    venueRepo: VenueRepo
) : BaseBookTabViewModel(
    EventType.MEMBER_EVENT,
    analyticsManager,
    categoryRepository,
    firebaseEventTracking,
    dataSourceFactory,
    venueRepo
) {

    fun logFilterClick() {
        firebaseEventTracking.logEventAction(AnalyticsManager.Action.EventsFilter)
    }

    override fun logEventClick(item: EventItem, position: Int) {
        firebaseEventTracking.logEventAction(
            if (position == 0 && item.isFeatured) AnalyticsManager.Action.EventsFeatured
            else AnalyticsManager.Action.EventsLatest
        )
    }
}
