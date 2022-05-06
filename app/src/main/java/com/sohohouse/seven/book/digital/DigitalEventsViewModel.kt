package com.sohohouse.seven.book.digital

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.book.adapter.model.EventListItem
import com.sohohouse.seven.common.analytics.AnalyticsManager
import javax.inject.Inject

class DigitalEventsViewModel @Inject constructor(
    private val dataSourceFactory: DigitalEventDataSourceFactory,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by dataSourceFactory,
    Errorable.ViewModel by dataSourceFactory {

    val events: LiveData<PagedList<EventListItem>> = LivePagedListBuilder(
        object : DataSource.Factory<Int, EventListItem>() {
            override fun create(): DataSource<Int, EventListItem> = dataSourceFactory.create()
        },
        PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(10).build()
    ).build()
}