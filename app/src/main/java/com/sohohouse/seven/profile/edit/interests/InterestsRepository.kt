package com.sohohouse.seven.profile.edit.interests

import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.CoreRequestFactory
import com.sohohouse.seven.network.core.models.Interest
import com.sohohouse.seven.network.core.request.GetInterestsRequest
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterestsRepository @Inject constructor(private val coreRequestFactory: CoreRequestFactory) {

    fun getInterests(filter: String): Single<Either<ServerError, List<Interest>>> {
        if (filter.isBlank()) return Single.just(value(emptyList()))

        return coreRequestFactory.create(GetInterestsRequest(filter))
    }

    fun getAllInterests(): Either<ServerError, List<Interest>> {
        return coreRequestFactory.createV2(
            GetInterestsRequest(
                filter = null,
                pageSize = Integer.MAX_VALUE
            )
        )
    }

}