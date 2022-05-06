package com.sohohouse.seven.discover.benefits

import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.Perk
import com.sohohouse.seven.network.core.request.GetPerksRequest

interface BenefitsRepo {

    fun getPerks(
        region: String? = null,
        cities: String? = null,
        page: Int = 1,
        perPage: Int = GetPerksRequest.DEFAULT_PERKS_PER_PAGE
    ): Either<ServerError, List<Perk>>

    fun getPerk(id: String): Either<ServerError, Perk>

}