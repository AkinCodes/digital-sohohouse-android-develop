package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.MutualConnectionRequests
import retrofit2.Call

data class PatchAcceptConnectionRequest(
    private val connection: MutualConnectionRequests,
) : CoreAPIRequest<MutualConnectionRequests> {
    override fun createCall(api: CoreApi): Call<out MutualConnectionRequests> {
        return api.patchAcceptConnectionRequest(connection.id, connection)
    }
}