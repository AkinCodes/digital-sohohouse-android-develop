package com.sohohouse.seven.book.fitness

import com.sohohouse.seven.book.adapter.model.EventItem
import com.sohohouse.seven.book.base.BaseBookTabViewModel
import com.sohohouse.seven.book.base.EventsDataSource
import com.sohohouse.seven.book.events.EventCategoryRepository
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.EventType
import javax.inject.Inject

class FitnessViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    categoryRepository: EventCategoryRepository,
    firebaseEventTracking: AnalyticsManager,
    venueRepo: VenueRepo,
    dataSourceFactory: EventsDataSource.Factory
) : BaseBookTabViewModel(
    EventType.FITNESS_EVENT,
    analyticsManager,
    categoryRepository,
    firebaseEventTracking,
    dataSourceFactory,
    venueRepo
) {

    fun logFilterClick() {
        firebaseEventTracking.logEventAction(AnalyticsManager.Action.GymFilter)
    }

    override fun logEventClick(item: EventItem, position: Int) {
        firebaseEventTracking.logEventAction(
            if (position == 0 && item.isFeatured) AnalyticsManager.Action.GymFeatured
            else AnalyticsManager.Action.GymLatest
        )
    }

}