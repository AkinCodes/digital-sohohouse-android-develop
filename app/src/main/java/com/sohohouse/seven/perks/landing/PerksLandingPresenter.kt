package com.sohohouse.seven.perks.landing

import android.annotation.SuppressLint
import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.base.error.ErrorViewStatePresenter
import com.sohohouse.seven.base.load.PresenterLoadable
import com.sohohouse.seven.base.load.PresenterPaginationLoadable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.apihelpers.PagedRequestHelper
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.ExploreListFactory
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.Perk
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.network.core.request.GetPerksRequest
import com.sohohouse.seven.perks.filter.manager.RegionFilterManager
import com.sohohouse.seven.perks.filter.manager.RegionFilterState
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import timber.log.Timber
import javax.inject.Inject

class PerksLandingPresenter @Inject constructor(
    private val zipRequestsUtil: ZipRequestsUtil,
    private val filterManager: RegionFilterManager,
    private val venueRepo: VenueRepo,
    private val exploreFactory: ExploreListFactory
) : BasePresenter<PerksLandingViewController>(), PresenterLoadable<PerksLandingViewController>,
    PresenterPaginationLoadable<PerksLandingViewController>,
    ErrorViewStatePresenter<PerksLandingViewController> {

    companion object {
        const val TAG: String = "PerksPresenter"
    }

    private var isFiltered: Boolean = false
    private lateinit var pageHelper: PagedRequestHelper<List<Perk>, GetPerksRequest>
    private var venues: List<Venue> = listOf()

    override fun onAttach(
        view: PerksLandingViewController,
        isFirstAttach: Boolean,
        isRecreated: Boolean
    ) {
        super.onAttach(view, isFirstAttach, isRecreated)
        view.setScreenName(AnalyticsManager.Screens.PerksLanding.name)
        if (isFirstAttach) {
            fetchCurrentFilterData()
        }
    }

    private fun fetchCurrentFilterData() {
        val appliedFilter = filterManager.appliedFilter
        pageHelper = PagedRequestHelper()
        when (filterManager.filterState) {
            RegionFilterState.NO_FILTER -> {
                isFiltered = false
                fetchAllPerks()
            }
            RegionFilterState.FILTERED -> {
                isFiltered = true
                val regionList = appliedFilter.selectedRegions
                fetchFilteredPerks(regionList)
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun getPerks(request: GetPerksRequest) {
        Single.just(venueRepo.venues()).doOnSuccess {
            venues = it
        }.flatMap {
            pageHelper.prepareForPageOne(request, GetPerksRequest.DEFAULT_PERKS_PER_PAGE)
            zipRequestsUtil.issueApiCall(request)
        }.observeOn(AndroidSchedulers.mainThread()).compose(loadTransformer())
            .compose(errorViewStateTransformer())
            .subscribe(Consumer {
                when (it) {
                    is Either.Error -> {
                        executeWhenAvailable { view, _, _ ->
                            view.showErrorState()
                        }
                    }
                    is Either.Value -> {
                        pageHelper.prepareNextRequest(request, it.value)
                        executeWhenAvailable { view, _, _ ->
                            view.onDataReady(
                                exploreFactory.createExplorePerksItems(
                                    it.value,
                                    venues = venues,
                                    isFiltered = isFiltered
                                )
                            )
                        }
                    }
                    else -> {
                    }
                }
            })
    }

    @SuppressLint("CheckResult")
    private fun fetchFilteredPerks(regionList: List<String>) {
        getPerks(GetPerksRequest.getPerksByRegion(regionList.joinToString(separator = ",")))
    }

    @SuppressLint("CheckResult")
    private fun fetchAllPerks() {
        getPerks(GetPerksRequest())
    }

    override fun reloadDataAfterError() {
        fetchCurrentFilterData()
    }

    @SuppressLint("CheckResult")
    fun fetchPerksOfNextPage() {
        if (pageHelper.areMorePageAvailable) {
            pageHelper.trackPaging(zipRequestsUtil.issueApiCall(pageHelper.request))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(paginationLoadTransformer())
                .compose(errorViewStateTransformer())
                .subscribe { either ->
                    either.fold(
                        ifValue = {
                            executeWhenAvailable { view, _, _ ->
                                view.addToEndOfAdapter(
                                    exploreFactory.createExplorePerksItems(
                                        it,
                                        venues = venues
                                    )
                                )
                            }
                        },
                        ifError = { Timber.tag(TAG).d(it.toString()) },
                        ifEmpty = {}
                    )
                }
        }
    }

    fun onRegionPreferencesUpdated() {
        fetchCurrentFilterData()
    }

    fun onFilterClicked() {
        view.startFilterActivity()
    }
}