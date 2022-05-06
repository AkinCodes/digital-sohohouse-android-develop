package com.sohohouse.seven.profile.edit

import android.content.Context
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.CoreRequestFactory
import com.sohohouse.seven.network.core.models.Occupation
import com.sohohouse.seven.network.core.request.GetOccupationsRequest
import io.reactivex.Single
import io.reactivex.SingleTransformer
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class OccupationsRepository @Inject constructor(
    private val coreRequestFactory: CoreRequestFactory,
    val context: Context
) {

    companion object {
        private const val LARGE_PAGE_SIZE = 1000
    }

    private var cachedOccupations: List<Occupation>? = null

    fun getOccupations(filter: String): Single<Either<ServerError, List<Occupation>>> {
        if (filter.isBlank()) return Single.just(value(emptyList()))

        return getOccupationsSingle().flatMap { result ->
            when (result) {
                is Either.Error -> Single.just(result)
                is Either.Value -> Single.just(value(result.value.filter { occupation ->
                    applyFilter(occupation, filter)
                }))
                is Either.Empty -> Single.just(result)
            }
        }
    }

    private fun applyFilter(occupation: Occupation, filter: String): Boolean {
        return occupation.name?.contains(filter, ignoreCase = true) == true
    }

    private fun getOccupationsSingle(): Single<Either<ServerError, List<Occupation>>> {
        if (cachedOccupations != null) {
            return Single.just(value(cachedOccupations!!))
        }
        return getOccupationsFromApi()
    }

    private fun getOccupationsFromApi(): Single<Either<ServerError, List<Occupation>>> {
        return coreRequestFactory.create(
            GetOccupationsRequest(
                filter = "",
                pageSize = LARGE_PAGE_SIZE
            )
        )
            .compose(cacheData())
    }

    fun prefetchData() {
        if (cachedOccupations == null) {
            getOccupationsFromApi()
                .compose(cacheData())
                .subscribe()
        }
    }

    private fun cacheData(): SingleTransformer<Either<ServerError, List<Occupation>>, Either<ServerError, List<Occupation>>> {
        return SingleTransformer { single ->
            single.flatMap {
                when (it) {
                    is Either.Value -> {
                        cachedOccupations = it.value
                        return@flatMap Single.just(value(it.value))
                    }
                    is Either.Error -> Single.just(it)
                    is Either.Empty -> Single.just(it)
                }
            }
        }

    }

}