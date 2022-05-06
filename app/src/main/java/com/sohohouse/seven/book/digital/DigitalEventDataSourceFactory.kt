package com.sohohouse.seven.book.digital

import androidx.paging.DataSource
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.book.adapter.model.EventListItem
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.utils.ZipRequestsUtil
import com.sohohouse.seven.common.venue.VenueRepo
import javax.inject.Inject

class DigitalEventDataSourceFactory @Inject constructor(
    private val zipRequestsUtil: ZipRequestsUtil,
    private val venueRepo: VenueRepo,
    private val stringProvider: StringProvider,
) : DataSource.Factory<Int, EventListItem>(),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    override fun create(): DataSource<Int, EventListItem> {
        return DigitalEventsDataSource(zipRequestsUtil, venueRepo, stringProvider, this, this)
    }
}