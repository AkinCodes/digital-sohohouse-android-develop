package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.City
import retrofit2.Call

class GetCitiesRequest : CoreAPIRequest<List<City>> {
    override fun createCall(api: CoreApi): Call<out List<City>> {
        return api.getPerksCities()
    }

}