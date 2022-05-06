package com.sohohouse.seven.browsehouses

import android.annotation.SuppressLint
import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.base.error.ErrorDialogPresenter
import com.sohohouse.seven.base.error.ErrorViewStatePresenter
import com.sohohouse.seven.base.load.PresenterLoadable
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.extensions.defaultIfMinus1
import com.sohohouse.seven.common.prefs.LocalVenueProvider
import com.sohohouse.seven.common.prefs.VenueAttendanceProvider
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.network.core.models.Venue
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import javax.inject.Inject

class BrowseAllHousePresenter @Inject constructor(
    private val venueRepo: VenueRepo,
    private val venueAttendanceProvider: VenueAttendanceProvider,
    private val localVenueProvider: LocalVenueProvider
) :
    BasePresenter<BrowseAllHouseViewController>(),
    PresenterLoadable<BrowseAllHouseViewController>,
    ErrorDialogPresenter<BrowseAllHouseViewController>,
    ErrorViewStatePresenter<BrowseAllHouseViewController> {

    override fun reloadDataAfterError() {
        fetchData()
    }

    override fun onAttach(
        view: BrowseAllHouseViewController,
        isFirstAttach: Boolean,
        isRecreated: Boolean
    ) {
        super.onAttach(view, isFirstAttach, isRecreated)
        if (isFirstAttach) fetchData()

    }

    @SuppressLint("CheckResult")
    private fun fetchData() {
        val attendingVenue = venueAttendanceProvider.attendingVenue
        Single.just(venueRepo.venues().filterWithTopLevel())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .subscribe(Consumer {
                executeWhenAvailable { view, _, _ ->
                    val items = getBrowseHousesList(it)
                    val selectedPosition = items.indexOfFirst { item ->
                        item is BaseAdapterItem.BrowseHousesItem.Content && item.house == attendingVenue
                    }.defaultIfMinus1(0) ?: 0
                    view.onDataReady(items, selectedPosition)
                }
            })
    }

    private fun getBrowseHousesList(venues: VenueList): List<BaseAdapterItem.BrowseHousesItem> {
        if (venues.isEmpty()) {
            return listOf()
        }
        val result = mutableListOf<BaseAdapterItem.BrowseHousesItem>()

        val organizedList = venues.organizedRegionVenueList(
            includeCWH = false, localVenue = localVenueProvider.localVenue.value
                ?: Venue()
        )
        for (pair in organizedList) {
            result.add(BaseAdapterItem.BrowseHousesItem.RegionHeader(pair.first.stringRes))
            val isLastPair = organizedList.indexOf(pair) == organizedList.lastIndex
            for (venue in pair.second) {
                val isLastItem = pair.second.indexOf(venue) == pair.second.lastIndex && isLastPair
                result.add(BaseAdapterItem.BrowseHousesItem.Content(venue, isLastItem))
            }
        }

        return result
    }

}
