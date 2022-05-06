package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.CommunicationPreference
import retrofit2.Call

class GetCommunicationPreferenceRequest
    : CoreAPIRequest<CommunicationPreference> {
    override fun createCall(api: CoreApi): Call<out CommunicationPreference> {
        return api.getCommunicationPreferences()
    }
}