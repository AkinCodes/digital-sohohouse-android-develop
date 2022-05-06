package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Interest
import retrofit2.Call

class GetInterestsRequest(
    private val filter: String? = null,
    private val pageSize: Int? = null,
) : CoreAPIRequest<List<Interest>> {

    override fun createCall(api: CoreApi): Call<out List<Interest>> {
        return api.getInterests(filter, perPage = pageSize)
    }

}