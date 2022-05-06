package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.network.core.models.ProfilePhotoUpdate
import retrofit2.Call

class PatchProfilePhotoRequest(
    private val profileId: String,
    private val profilePhotoData: String,
) : CoreAPIRequest<Profile> {
    override fun createCall(api: CoreApi): Call<out Profile> {
        return api.patchProfilePhoto(ProfilePhotoUpdate(profilePhotoData).apply {
            this.id = profileId
        })
    }

}