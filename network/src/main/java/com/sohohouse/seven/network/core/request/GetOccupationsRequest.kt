package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Occupation
import retrofit2.Call

class GetOccupationsRequest(private val filter: String, private val pageSize: Int) :
    CoreAPIRequest<List<Occupation>> {
    override fun createCall(api: CoreApi): Call<out List<Occupation>> {
        return api.getOccupations(filter, pageSize)
    }

}