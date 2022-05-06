package com.sohohouse.seven.apponboarding.housepreferences

import android.annotation.SuppressLint
import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.base.error.ErrorDialogPresenter
import com.sohohouse.seven.base.error.ErrorViewStatePresenter
import com.sohohouse.seven.base.load.PresenterLoadable
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class OnboardingHousePreferencesPresenter @Inject constructor(
    private val houseManager: HouseManager,
    private val analyticsManager: AnalyticsManager,
    private val venueRepo: VenueRepo
) :
    BasePresenter<OnboardingHousePreferencesViewController>(),
    PresenterLoadable<OnboardingHousePreferencesViewController>,
    ErrorDialogPresenter<OnboardingHousePreferencesViewController>,
    ErrorViewStatePresenter<OnboardingHousePreferencesViewController> {

    companion object {
        private const val TAG = "OnboardingHousePreferencesPresenter"
    }

    lateinit var selectedLocations: List<String>
    private var isInErrorState = false

    override fun onAttach(
        view: OnboardingHousePreferencesViewController,
        isFirstAttach: Boolean,
        isRecreated: Boolean
    ) {
        super.onAttach(view, isFirstAttach, isRecreated)
        view.setScreenName(AnalyticsManager.Screens.OnboardHousePreferences.name)
        if (isFirstAttach) {
            fetchVenues()
        }
    }

    @SuppressLint("CheckResult")
    private fun fetchVenues() {
        Single.just(venueRepo.venues().filterWithTopLevel())
            .flatMap {
                val organizedData = houseManager.organizeHousesForLocationRecyclerView(
                    it, true, includeStudios = false
                )
                Single.just(value(organizedData))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorViewStateTransformer())
            .subscribe(Consumer {
                when (it) {
                    is Either.Error -> {
                        Timber.d(it.error.toString())
                        isInErrorState = true
                    }
                    is Either.Value -> {
                        isInErrorState = false
                        val organizedData = it.value
                        selectedLocations = organizedData.first
                        executeWhenAvailable { view, _, _ ->
                            view.onDataReady(organizedData.second, organizedData.third)
                        }
                    }
                }
            })
    }

    override fun reloadDataAfterError() {
        fetchVenues()
    }

    fun selectedLocationsUpdated(locations: List<String>) {
        selectedLocations = locations
    }

    @SuppressLint("CheckResult")
    fun updateLocations() {
        venueRepo.updateFavouriteVenuesSingle(selectedLocations)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorDialogTransformer())
            .subscribe(Consumer {
                when (it) {
                    is Either.Error -> {
                        Timber.d(it.error.toString())
                    }
                    is Either.Value -> {
                        analyticsManager.track(
                            AnalyticsEvent.AppOnBoarding.HousePreferences(
                                selectedLocations
                            )
                        )
                        executeWhenAvailable { view, _, _ ->
                            view.updateSuccess()
                        }
                    }
                }
            })
    }

    fun continueClicked() {
        if (isInErrorState) {
            //on error, let user continue thru onboarding as tailor is not mandatory
            executeWhenAvailable { view, _, _ -> view.updateSuccess() }
        } else {
            updateLocations()
        }
    }
}
