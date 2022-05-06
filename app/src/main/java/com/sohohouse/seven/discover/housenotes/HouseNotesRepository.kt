package com.sohohouse.seven.discover.housenotes

import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.request.GetHouseNoteSitecoreRequest
import com.sohohouse.seven.network.sitecore.SitecoreRequestFactory
import com.sohohouse.seven.network.sitecore.models.SitecoreRoute
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HouseNotesRepository @Inject constructor(private val sitecoreRequestFactory: SitecoreRequestFactory) {

    fun getHouseNoteDetails(articleSlug: String): Single<Either<ServerError, SitecoreRoute?>> {
        return sitecoreRequestFactory.create(GetHouseNoteSitecoreRequest(articleSlug))
            .map {
                when (it) {
                    is Either.Error -> Either.Error(it.error)
                    is Either.Value -> Either.Value(it.value.sitecore.route)
                    is Either.Empty -> Either.Empty()
                }
            }
    }

}