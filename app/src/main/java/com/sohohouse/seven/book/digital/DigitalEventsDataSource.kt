package com.sohohouse.seven.book.digital

import androidx.paging.PageKeyedDataSource
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.book.adapter.model.EventListItem
import com.sohohouse.seven.common.extensions.getFormattedDateTime
import com.sohohouse.seven.common.extensions.isDigitalEvent
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.utils.ZipRequestsUtil
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.request.GetEventsRequest

class DigitalEventsDataSource(
    private val zipRequestsUtil: ZipRequestsUtil,
    private val venueRepo: VenueRepo,
    private val stringProvider: StringProvider,
    loadable: Loadable.ViewModel,
    errorable: Errorable.ViewModel
) : PageKeyedDataSource<Int, EventListItem>(),
    Loadable.ViewModel by loadable,
    Errorable.ViewModel by errorable {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, EventListItem>
    ) {
        setLoading()
        zipRequestsUtil.issueApiCall(GetEventsRequest.getPastDigitalEvents(1)).fold(
            ifValue = { callback.onResult(it.map { event -> buildEvenListItem(event) }, 1, 2) },
            ifError = { showError(it.toString()) },
            ifEmpty = { callback.onResult(emptyList(), 1, 2) }
        )
        setIdle()
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, EventListItem>) {
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, EventListItem>) {
        setLoading()
        zipRequestsUtil.issueApiCall(GetEventsRequest.getPastDigitalEvents(params.key)).fold(
            ifValue = {
                callback.onResult(
                    it.map { event -> buildEvenListItem(event) },
                    params.key + 1
                )
            },
            ifError = { showError(it.toString()) },
            ifEmpty = { callback.onResult(emptyList(), params.key + 1) }
        )
        setIdle()
    }

    private fun buildEvenListItem(event: Event): EventListItem {
        val venue = venueRepo.venues().findById(event.venue?.get()?.id)
        return EventListItem(
            id = event.id,
            title = event.name,
            subtitle = event.startsAt?.getFormattedDateTime(venue?.timeZone),
            label = if (event.isDigitalEvent) stringProvider.getString(R.string.event_digital_event) else venue?.name,
            imageUrl = event.images?.large
        )
    }
}