package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Meta
import com.sohohouse.seven.network.core.models.MutualConnections
import com.squareup.moshi.Moshi
import moe.banana.jsonapi2.JsonBuffer
import retrofit2.Call

class GetConnections(override var page: Int?, override var perPage: Int?) :
    CoreAPIRequestPagable<List<MutualConnections>> {

    override fun createCall(api: CoreApi): Call<out List<MutualConnections>> {
        return api.getConnections(perPage, page)
    }

    override fun getMeta(response: List<MutualConnections>): Meta? {
        return GetConnections.getMeta(response)
    }

    companion object {
        fun getMeta(response: List<MutualConnections>): Meta? {
            val adapter = Moshi.Builder().build().adapter(Meta::class.java)
            if (response.isNotEmpty() && response[0].document.meta != null) {
                @Suppress("UNCHECKED_CAST")
                return (response[0].document.meta as JsonBuffer<Meta>).get(adapter)
            }
            return null
        }
    }
}
