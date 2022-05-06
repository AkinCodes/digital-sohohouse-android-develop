package com.sohohouse.seven.perks.filter

import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.CoreRequestFactory
import com.sohohouse.seven.network.core.models.City
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.network.core.request.GetCitiesRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CitiesRepository @Inject constructor(
    private val venueRepo: VenueRepo,
    private val coreRequestFactory: CoreRequestFactory
) {

    private var cities: List<City>? = null

    fun getCities(): Either<ServerError, List<City>> {
        return fetchCities(venueRepo.venues())
    }

    private fun fetchCities(venues: List<Venue>): Either<ServerError, List<City>> {
        cities?.let { return value(it) }

        val cityRegionMap = venues.map { it.city to it.region }.toMap()
        return coreRequestFactory.createV2(GetCitiesRequest()).fold(
            ifValue = { cities ->
                cities.forEach { city -> city.region = cityRegionMap[city.name] ?: "" }
                this.cities = cities
                Either.Value(cities)
            },
            ifError = { Either.Error(it) },
            ifEmpty = { Either.Empty() }
        )
    }

}