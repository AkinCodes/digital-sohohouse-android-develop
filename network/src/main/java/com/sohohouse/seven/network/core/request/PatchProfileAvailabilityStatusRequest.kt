package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.ProfileAvailabilityStatus
import retrofit2.Call

class PatchProfileAvailabilityStatusRequest(
    private val profileAvailabilityStatus: ProfileAvailabilityStatus,
) : CoreAPIRequest<ProfileAvailabilityStatus> {
    override fun createCall(api: CoreApi): Call<out ProfileAvailabilityStatus> {
        return api.patchProfileAvailabilityStatus(profileAvailabilityStatus)
    }
}