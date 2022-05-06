package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import retrofit2.Call

class DeleteRollCallRequest(private val statusId: String) : CoreAPIRequest<Void> {
    override fun createCall(api: CoreApi): Call<out Void> {
        return api.deleteRollCall(statusId)
    }
}