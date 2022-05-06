package com.sohohouse.seven.more.housepreferences

import android.annotation.SuppressLint
import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.base.error.ErrorDialogPresenter
import com.sohohouse.seven.base.error.ErrorViewStatePresenter
import com.sohohouse.seven.base.load.PresenterLoadable
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.house.HouseRegion.*
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerParentItem
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import javax.inject.Inject

class MoreHousePreferencesPresenter @Inject constructor(
    private val accountInteractor: AccountInteractor,
    private val houseManager: HouseManager,
    private val userManager: UserManager,
    private val venueRepo: VenueRepo,
    private val analyticsManager: AnalyticsManager
) :
    BasePresenter<MoreHousePreferencesViewController>(),
    PresenterLoadable<MoreHousePreferencesViewController>,
    ErrorDialogPresenter<MoreHousePreferencesViewController>,
    ErrorViewStatePresenter<MoreHousePreferencesViewController> {

    companion object {
        private const val TAG = "MoreHousePreferencesPresenter"
    }

    lateinit var selectedList: List<String>

    override fun onAttach(
        view: MoreHousePreferencesViewController,
        isFirstAttach: Boolean,
        isRecreated: Boolean
    ) {
        super.onAttach(view, isFirstAttach, isRecreated)
        view.setScreenName(AnalyticsManager.Screens.HousePreferences.name)
        if (isFirstAttach) {
            fetchData()
        }
    }

    @SuppressLint("CheckResult")
    private fun fetchData() {

        accountInteractor.getAccount()
            .flatMap { either ->
                either.fold(
                    ifValue = {
                        venueRepo.updateFavouriteVenuesSingle(it.favoriteVenuesResource?.map { venue -> venue.id }
                            ?: listOf())
                        Single.just(value(venueRepo.venues().filterWithTopLevel()))
                    },
                    ifError = { Single.just(Either.Error(it)) },
                    ifEmpty = { Single.just(Either.Empty()) }
                )
            }
            .flatMap { either ->
                either.fold(
                    ifValue = {
                        val organizedData = houseManager.organizeHousesForLocationRecyclerView(
                            it,
                            true,
                            includeStudios = false
                        )
                        Single.just(value(organizedData))
                    },
                    ifError = { Single.just(Either.Error(it)) },
                    ifEmpty = { Single.just(Either.Empty()) }
                )
            }
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorViewStateTransformer())
            .subscribe { either ->
                either.fold(
                    ifValue = { triple ->
                        selectedList = triple.first
                        executeWhenAvailable { view, _, _ ->
                            view.onDataReady(triple.second, triple.third)
                            view.enableApplyButton(selectedList.isNotEmpty())
                        }
                    },
                    ifError = {
                        Timber.d(it.toString())
                    },
                    ifEmpty = {}
                )
            }
    }

    fun resetDefaultSelection() {
        analyticsManager.logEventAction(AnalyticsManager.Action.ResetFavouriteHouses)
        executeWhenAvailable { view, _, _ ->
            selectedList = listOf(userManager.localHouseId)
            view.resetSelection(userManager.localHouseId)
            view.enableApplyButton(selectedList.isNotEmpty())

        }
    }

    fun onSelectedLocationsChanged(selectedList: List<String>) {
        this.selectedList = selectedList
        executeWhenAvailable { view, _, _ ->
            view.enableApplyButton(selectedList.isNotEmpty())
        }
    }

    @SuppressLint("CheckResult")
    fun onApplyButtonClicked() {
        analyticsManager.logEventAction(AnalyticsManager.Action.SaveFavouriteHouses)
        venueRepo.updateFavouriteVenuesSingle(selectedList)
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorDialogTransformer())
            .subscribe { either ->
                either.fold(
                    ifValue = {
                        analyticsManager.track(AnalyticsEvent.HousePreferences(selectedList))
                        executeWhenAvailable { view, _, _ -> view.updateSuccess() }
                    },
                    ifError = {
                        Timber.d(it.toString())
                    },
                    ifEmpty = {}
                )
            }
    }

    override fun reloadDataAfterError() {
        fetchData()
    }

    fun onRegionToggled(parentItem: LocationRecyclerParentItem) {
        if (parentItem.expanded) {
            when (parentItem.region) {
                EUROPE -> analyticsManager.logEventAction(AnalyticsManager.Action.FavouriteHousesExpandEurope)
                WORLDWIDE -> analyticsManager.logEventAction(AnalyticsManager.Action.FavouriteHousesExpandAsia)
                NORTH_AMERICA -> analyticsManager.logEventAction(AnalyticsManager.Action.FavouriteHousesExpandNorthAmerica)
                UK -> analyticsManager.logEventAction(AnalyticsManager.Action.FavouriteHousesExpandUk)
                CWH -> analyticsManager.logEventAction(AnalyticsManager.Action.FavouriteHousesExpandCwh)
            }
        }

    }
}
