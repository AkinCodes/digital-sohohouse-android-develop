package com.sohohouse.seven.discover.benefits

import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.CoreRequestFactory
import com.sohohouse.seven.network.core.models.Perk
import com.sohohouse.seven.network.core.request.GetPerkDetailsRequest
import com.sohohouse.seven.network.core.request.GetPerksRequest
import javax.inject.Inject

class BenefitsRepoImpl @Inject constructor(private val requestFactory: CoreRequestFactory) :
    BenefitsRepo {

    override fun getPerks(
        region: String?,
        cities: String?,
        page: Int,
        perPage: Int
    ): Either<ServerError, List<Perk>> {
        return requestFactory.createV2(GetPerksRequest(region, cities, page, perPage))
    }

    override fun getPerk(id: String): Either<ServerError, Perk> {
        return requestFactory.createV2(GetPerkDetailsRequest(id))
    }
}