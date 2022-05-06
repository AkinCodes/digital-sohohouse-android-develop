package com.sohohouse.seven.profile.edit

import androidx.annotation.VisibleForTesting
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.places.City
import io.reactivex.Scheduler
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class EditCityViewModel : AutoCompleteViewModel<EditCityViewModel.CityWrapper> {
    private val citiesRepository: CitiesRepository

    @Inject
    constructor(
        occupationRepository: CitiesRepository,
        analyticsManager: AnalyticsManager
    ) : super(analyticsManager) {
        this.citiesRepository = occupationRepository
    }

    @VisibleForTesting
    constructor(
        analyticsManager: AnalyticsManager,
        occupationRepository: CitiesRepository,
        scheduler: Scheduler
    ) : super(analyticsManager, scheduler) {
        this.citiesRepository = occupationRepository
    }

    private val sessionToken: String

    init {
        sessionToken = UUID.randomUUID().toString()
    }

    override fun getSuggestions(query: String): Single<Either<ServerError, List<CityWrapper>>> {
        return citiesRepository.getCities(sessionToken, query)
            .map { response ->
                if (response is Either.Value) {
                    value(response.value.map { CityWrapper(it) })
                } else {
                    com.sohohouse.seven.network.base.model.error((response as Either.Error).error)
                }
            }
    }

    data class CityWrapper(val city: City) : AutoCompleteSuggestion {
        override val value: String
            get() = city.name
        override val key: Any?
            get() = city.name
    }

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.EditCity.name)
    }

}