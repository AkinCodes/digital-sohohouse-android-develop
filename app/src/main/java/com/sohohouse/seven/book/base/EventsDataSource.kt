package com.sohohouse.seven.book.base

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.book.filter.BookFilterManager
import com.sohohouse.seven.common.utils.ErrorInteractor
import com.sohohouse.seven.common.utils.ZipRequestsUtil
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.views.EventStatusHelper
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.common.views.ExploreListFactory
import com.sohohouse.seven.network.core.models.EventCategory
import javax.inject.Inject

class EventsDataSource(private val helper: DataSourceHelper?) :
    PageKeyedDataSource<Int, DiffItem>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, DiffItem>
    ) {
        helper?.loadInitial(params) { items -> callback.onResult(items, 1, 2) }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, DiffItem>) {
        helper?.loadBefore(params) {}
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, DiffItem>) {
        helper?.loadAfter(params) { items -> callback.onResult(items, params.key + 1) }
    }

    /***
     * EventsDataSource.Factory
     */
    class Factory @Inject constructor(
        private val zipRequestsUtil: ZipRequestsUtil,
        private val filterManager: BookFilterManager,
        private val errorInteractor: ErrorInteractor,
        private val exploreFactory: ExploreListFactory,
        private val eventStatusHelper: EventStatusHelper
    ) : DataSource.Factory<Int, DiffItem>(),
        Loadable.ViewModel by Loadable.ViewModelImpl(),
        Errorable.ViewModel by Errorable.ViewModelImpl() {

        private lateinit var eventType: EventType

        private var venues: VenueList = VenueList.empty()

        private var categories: List<EventCategory> = emptyList()

        private var dataSource: DataSource<Int, DiffItem>? = null

        fun setEventType(eventType: EventType) {
            this.eventType = eventType
        }

        override fun create(): DataSource<Int, DiffItem> {
            if (this::eventType.isInitialized.not()) throw Exception("EventType is not initialised!")

            val helper = DataSourceHelper.init(
                event = eventType,
                zipRequestsUtil = zipRequestsUtil,
                filterManager = filterManager,
                errorInteractor = errorInteractor,
                exploreFactory = exploreFactory,
                eventStatusHelper = eventStatusHelper,
                venues = venues,
                categories = categories,
                loadable = this,
                errorable = this
            )
            return EventsDataSource(helper).apply { dataSource = this }
        }

        fun invalidate(
            venues: VenueList = this.venues,
            categories: List<EventCategory> = this.categories
        ) {
            this.venues = venues
            this.categories = categories
            dataSource?.invalidate()
        }
    }
}