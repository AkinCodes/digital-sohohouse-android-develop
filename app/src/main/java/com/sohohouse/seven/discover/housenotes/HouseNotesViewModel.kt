package com.sohohouse.seven.discover.housenotes

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.DataSource.Factory
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PagedList.Config.Builder
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import javax.inject.Inject

class HouseNotesViewModel @Inject constructor(
    private val dataSourceFactory: HouseNotesDataSourceFactory,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by dataSourceFactory,
    Errorable.ViewModel by dataSourceFactory {

    val houseNotes: LiveData<PagedList<DiffItem>> = LivePagedListBuilder(
        object : Factory<Int, DiffItem>() {
            override fun create(): DataSource<Int, DiffItem> = dataSourceFactory.create()
        },
        Builder().setEnablePlaceholders(false).setPageSize(10).build()
    ).build()

    fun onHouseNoteClicked(id: String, position: Int) {
        if (position == 0) {
            analyticsManager.logEventAction(AnalyticsManager.Action.DiscoverFeaturedHouseNote)
        } else {
            analyticsManager.logEventAction(AnalyticsManager.Action.DiscoverLatestHouseNote)
        }
    }

    fun logView() {
        analyticsManager.logEventAction(AnalyticsManager.Action.DiscoverHouseNotes)
    }

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.HouseNotes.name)
    }

    fun invalidate() {
        dataSourceFactory.invalidate()
    }
}
