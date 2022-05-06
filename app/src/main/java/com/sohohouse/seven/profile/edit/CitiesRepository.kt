package com.sohohouse.seven.profile.edit

import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.places.City
import com.sohohouse.seven.network.places.GetCitiesRequest
import com.sohohouse.seven.network.places.PlacesRequestFactory
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CitiesRepository @Inject constructor(private val requestFactory: PlacesRequestFactory) {

    fun getCities(sessionToken: String, filter: String): Single<Either<ServerError, List<City>>> {
        if (filter.isBlank()) return Single.just(value(emptyList()))
        return requestFactory.create(GetCitiesRequest(filter, sessionToken))
            .map { response ->
                if (response is Either.Value) {
                    value(response.value.predictions)
                } else {
                    com.sohohouse.seven.network.base.model.error((response as Either.Error).error)
                }
            }
    }
}