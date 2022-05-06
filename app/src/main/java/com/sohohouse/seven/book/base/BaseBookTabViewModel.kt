package com.sohohouse.seven.book.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.Paginatable
import com.sohohouse.seven.book.adapter.model.EventBookAdapterItem
import com.sohohouse.seven.book.adapter.model.EventItem
import com.sohohouse.seven.book.events.EventCategoryRepository
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.EventCategory

abstract class BaseBookTabViewModel(
    eventType: EventType,
    analyticsManager: AnalyticsManager,
    private val categoryRepository: EventCategoryRepository,
    protected val firebaseEventTracking: AnalyticsManager,
    private val dataSourceFactory: EventsDataSource.Factory,
    private val venueRepo: VenueRepo
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by dataSourceFactory,
    Errorable.ViewModel by dataSourceFactory,
    Paginatable.ViewModel by Paginatable.ViewModelImpl() {

    init {
        dataSourceFactory.setEventType(eventType)
    }

    val events: LiveData<PagedList<DiffItem>> = LivePagedListBuilder(
        dataSourceFactory,
        PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(10).build()
    ).build()

    val isReady: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        fun onChanged(venues: VenueList, categories: List<EventCategory>) {
            if (venues.isEmpty() || categories.isEmpty()) return

            dataSourceFactory.invalidate(venues, categories)
            postValue(true)
        }

        addSource(venueRepo.liveVenues()) {
            onChanged(it, categoryRepository.categories.value ?: emptyList())
        }
        addSource(categoryRepository.categories) {
            onChanged(venueRepo.venues(), it)
        }
    }

    override fun onCleared() {
        categoryRepository.cancel()
        super.onCleared()
    }

    fun getEventIndex(event: Event): Int? {
        return events.value?.firstOrNull { it is EventBookAdapterItem && it.event.id == event.id }
            ?.let { events.value?.indexOf(it) }
    }

    open fun invalidate() {
        dataSourceFactory.invalidate()
    }

    abstract fun logEventClick(item: EventItem, position: Int)

}