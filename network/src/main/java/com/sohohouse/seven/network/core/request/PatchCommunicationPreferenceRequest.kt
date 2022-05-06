package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.CommunicationPreference
import retrofit2.Call

class PatchCommunicationPreferenceRequest(private val communicationPreference: CommunicationPreference) :
    CoreAPIRequest<CommunicationPreference> {

    companion object {
        private const val MEMBER_ID = "me"
    }

    override fun createCall(api: CoreApi): Call<out CommunicationPreference> {
        communicationPreference.id = MEMBER_ID
        return api.patchCommunicationPreferences(communicationPreference)
    }
}