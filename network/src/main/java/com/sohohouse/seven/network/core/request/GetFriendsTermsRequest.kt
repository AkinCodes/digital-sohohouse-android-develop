package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.StaticPages
import retrofit2.Call

class GetFriendsTermsRequest : CoreAPIRequest<List<StaticPages>> {

    override fun createCall(api: CoreApi): Call<out List<StaticPages>> {
        return api.getFriendsTerms()
    }
}