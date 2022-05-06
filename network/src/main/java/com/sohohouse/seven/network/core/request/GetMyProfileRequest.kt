package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Profile
import retrofit2.Call

class GetMyProfileRequest() : CoreAPIRequest<Profile> {

    override fun createCall(api: CoreApi): Call<out Profile> {
        return api.getMyProfile()
    }
}