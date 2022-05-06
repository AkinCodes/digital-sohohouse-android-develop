package com.sohohouse.seven.discover.housenotes

import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.request.GetHouseNotesSitecoreRequest
import com.sohohouse.seven.network.sitecore.SitecoreRequestFactory
import com.sohohouse.seven.network.sitecore.models.template.Template
import javax.inject.Inject

class HouseNotesRepoImpl @Inject constructor(private val requestFactory: SitecoreRequestFactory) :
    HouseNotesRepo {

    override fun getAll(top: Int, skip: Int): Either<ServerError, List<Template>> {
        return requestFactory.createV2(GetHouseNotesSitecoreRequest(top = top, skip = skip)).fold(
            { Either.Error(it) },
            { Either.Value(it.value) },
            { Either.Empty() }
        )
    }

}