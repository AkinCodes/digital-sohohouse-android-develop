package com.sohohouse.seven.common.venue

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.sohohouse.seven.common.prefs.VenueCache
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.mapValue
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.network.core.request.GetVenuesRequest
import com.sohohouse.seven.network.core.request.PatchVenuesRequest
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class VenueRepoImpl(
    private val zipRequestsUtil: ZipRequestsUtil,
    private val venueCache: VenueCache,
    private val userManager: UserManager,
    private val ioDispatcher: CoroutineDispatcher
) : VenueRepo {

    private val venuesFlow = MutableStateFlow(VenueList.empty())

    override fun liveVenues(): LiveData<VenueList> = venuesFlow.asLiveData()

    override suspend fun fetchVenues(): Either<ServerError, VenueList> =
        withContext(ioDispatcher) { getVenuesFromApi() }

    override fun fetchVenuesSingle(): Single<Either<ServerError, VenueList>> {
        return Single.fromCallable {
            getVenuesFromApi()
        }.subscribeOn(Schedulers.io())
    }

    override fun venues(): VenueList {
        if (venuesFlow.value.isEmpty()) {
            venuesFlow.value = VenueList(venueCache.read())
        }
        return venuesFlow.value
    }

    override suspend fun updateFavouriteVenues(favouriteVenueIds: List<String>): Either<ServerError, VenueList> {
        return zipRequestsUtil.issueApiCallV2(PatchVenuesRequest(favouriteVenueIds)).asVenueList()
            .ifValue {
                onFavouriteVenuesUpdated(it)
            }
    }

    private fun onFavouriteVenuesUpdated(it: VenueList) {
        venuesFlow.value = it
        venueCache.write(it)
        userManager.favouriteHouses = it.map { value -> value.id }
    }

    override fun updateFavouriteVenuesSingle(favouriteVenueIds: List<String>): Single<Either<ServerError, VenueList>> {
        return zipRequestsUtil.issueApiCall(PatchVenuesRequest(favouriteVenueIds))
            .mapValue {
                VenueList(it)
            }.doOnSuccess {
                if (it is Either.Value) {
                    onFavouriteVenuesUpdated(it.value)
                }
            }
    }

    private fun getVenuesFromApi(): Either<ServerError, VenueList> {
        return zipRequestsUtil.issueApiCallV2(GetVenuesRequest()).asVenueList().ifValue {
            venuesFlow.value = it
            venueCache.write(it)
        }
    }

    private fun Either<ServerError, List<Venue>>.asVenueList() = fold(
        ifValue = { value(VenueList(it)) },
        ifEmpty = { Either.Empty() },
        ifError = { Either.Error(it) }
    )

}