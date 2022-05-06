package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Membership
import retrofit2.Call

class GetMembershipRequest : CoreAPIRequest<Membership> {
    override fun createCall(api: CoreApi): Call<out Membership> {
        return api.getMembership()
    }
}