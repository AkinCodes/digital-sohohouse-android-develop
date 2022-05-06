package com.sohohouse.seven.discover.benefits

import androidx.paging.PageKeyedDataSource
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.ExploreListFactory
import com.sohohouse.seven.discover.benefits.adapter.PerksItem
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.core.models.Perk
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.network.core.request.GetPerksRequest
import com.sohohouse.seven.perks.filter.manager.BenefitsFilterManager
import com.sohohouse.seven.perks.filter.manager.RegionFilterManager

class BenefitsDataSource(
    private val regionFilterManager: RegionFilterManager,
    private val citiesFilterManager: BenefitsFilterManager,
    private val venueRepo: VenueRepo,
    private val exploreFactory: ExploreListFactory,
    private val repo: BenefitsRepo,
    private val featureFlags: FeatureFlags,
    loadable: Loadable.ViewModel,
    errorable: Errorable.ViewModel
) : PageKeyedDataSource<Int, PerksItem>(),
    Loadable.ViewModel by loadable,
    Errorable.ViewModel by errorable {

    private var itemPerPage: Int = GetPerksRequest.DEFAULT_PERKS_PER_PAGE

    private var venues: List<Venue> = emptyList()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, PerksItem>
    ) {
        if (venues.isEmpty()) venues = getVenues()

        setLoadingState(LoadingState.Loading)
        var region: String? = null
        var cities: String? = null
        if (featureFlags.benefitsFilterByCity) {
            cities = citiesFilterManager.citiesFiltered.joinToString(",")
        } else {
            region = regionFilterManager.appliedFilter.selectedRegions.joinToString(separator = ",")
        }
        repo.getPerks(region = region, cities = cities, page = 1, perPage = itemPerPage).fold(
            ::onError,
            { mapLoadInitial(it, callback, 1, 2) },
            { }
        )
        setLoadingState(LoadingState.Idle)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PerksItem>) {
        var region: String? = null
        var cities: String? = null
        if (featureFlags.benefitsFilterByCity) {
            cities = citiesFilterManager.citiesFiltered.joinToString(",")
        } else {
            region = regionFilterManager.appliedFilter.selectedRegions.joinToString(separator = ",")
        }
        repo.getPerks(region = region, cities = cities, page = params.key, perPage = itemPerPage)
            .fold(
                ::onError,
                { mapLoadAfter(it, callback, params.key + 1) },
                { }
            )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, PerksItem>) {
        // Do nothing
    }

    private fun mapLoadInitial(
        perks: List<Perk>,
        callback: LoadInitialCallback<Int, PerksItem>,
        adjacentPageKey: Int,
        nextPageKey: Int
    ) {
        val items = exploreFactory.createPerksItem(perks, venues = venues)
        callback.onResult(items, adjacentPageKey, nextPageKey)
    }

    private fun mapLoadAfter(
        perks: List<Perk>,
        callback: LoadCallback<Int, PerksItem>,
        adjacentPageKey: Int
    ) {
        val items = exploreFactory.createPerksItem(perks, venues = venues)
        callback.onResult(items, adjacentPageKey)
    }

    private fun getVenues(): List<Venue> {
        return venueRepo.venues()
    }

    private fun onError(error: ServerError) {
        FirebaseCrashlytics.getInstance().log(error.toString())
        showError()
    }

}