package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Meta
import com.sohohouse.seven.network.core.models.ProfileAvailabilityStatus
import com.squareup.moshi.Moshi
import moe.banana.jsonapi2.JsonBuffer
import retrofit2.Call

class GetProfileAvailabilityStatusPageableRequest(
    override var page: Int?,
    override var perPage: Int?,
) : CoreAPIRequestPagable<List<ProfileAvailabilityStatus>> {

    override fun createCall(api: CoreApi): Call<out List<ProfileAvailabilityStatus>> {
        return api.getProfileAvailabilityStatuses(perPage = perPage ?: 10, page = page ?: 1)
    }

    override fun getMeta(response: List<ProfileAvailabilityStatus>): Meta? {
        val adapter = Moshi.Builder().build().adapter(Meta::class.java)
        if (response.isNotEmpty() && response[0].document.meta != null) {
            @Suppress("UNCHECKED_CAST")
            return (response[0].document.meta as JsonBuffer<Meta>).get(adapter)
        }
        return null
    }
}