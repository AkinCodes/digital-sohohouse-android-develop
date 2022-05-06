package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Perk
import retrofit2.Call

class GetPerkDetailsRequest(private val perkId: String) : CoreAPIRequest<Perk> {

    override fun createCall(api: CoreApi): Call<out Perk> {
        return api.getPerkbyId(perkId)
    }
}