package com.sohohouse.seven.common.venue

import androidx.lifecycle.LiveData
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import io.reactivex.Single

interface VenueRepo {

    fun liveVenues(): LiveData<VenueList>

    suspend fun fetchVenues(): Either<ServerError, VenueList>

    fun fetchVenuesSingle(): Single<Either<ServerError, VenueList>>

    fun venues(): VenueList

    suspend fun updateFavouriteVenues(favouriteVenueIds: List<String>): Either<ServerError, VenueList>

    fun updateFavouriteVenuesSingle(favouriteVenueIds: List<String>): Single<Either<ServerError, VenueList>>

}