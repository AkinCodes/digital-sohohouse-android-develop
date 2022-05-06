package com.sohohouse.seven.discover.houses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.prefs.LocalVenueProvider
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.discover.houses.adapter.BaseHouseItem
import com.sohohouse.seven.discover.houses.adapter.HeaderItem
import com.sohohouse.seven.discover.houses.adapter.HouseItem
import com.sohohouse.seven.discover.houses.adapter.RegionItem
import com.sohohouse.seven.network.core.models.Venue
import javax.inject.Inject

class HousesViewModel @Inject constructor(
    private val venueRepo: VenueRepo,
    private val localVenueProvider: LocalVenueProvider,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    private val _houses: MutableLiveData<List<BaseHouseItem>> = MutableLiveData()

    val houses: LiveData<List<BaseHouseItem>>
        get() = _houses

    init {
        loadHouses()
    }

    fun loadHouses() {
        val items = mutableListOf<BaseHouseItem>().apply {
            add(HeaderItem())
            addAll(mapVenues(venueRepo.venues().filterWithTopLevel()))
        }
        _houses.postValue(items)
        setIdle()
    }

    private fun mapVenues(venues: VenueList): List<RegionItem> {
        return venues.organizedRegionVenueList(
            includeCWH = false, localVenue = localVenueProvider.localVenue.value
                ?: Venue()
        ).map {
            RegionItem(it.first.stringRes, buildHouseItem(it.second))
        }
    }

    private fun buildHouseItem(venues: List<Venue>): List<HouseItem> {
        return venues.map {
            HouseItem(
                id = it.id,
                title = it.name,
                description = it.description,
                city = "",  //TODO city currently not available from API
                imageUrl = it.house.get(it.document)?.houseImageSet?.largePng,
                slug = it.slug
            )
        }.sortedBy { it.title }
    }

    fun logView() {
        analyticsManager.logEventAction(AnalyticsManager.Action.DiscoverHouses)
    }

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.Houses.name)
    }

}
