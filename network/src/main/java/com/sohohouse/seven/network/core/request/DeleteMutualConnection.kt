package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import retrofit2.Call

data class DeleteMutualConnection(
    private val id: String,
) : CoreAPIRequest<Void> {
    override fun createCall(api: CoreApi): Call<Void> {
        return api.deleteMutualConnection(id)
    }
}