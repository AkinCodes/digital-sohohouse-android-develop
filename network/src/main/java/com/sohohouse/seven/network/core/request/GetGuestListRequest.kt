package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.GuestList
import retrofit2.Call

class GetGuestListRequest(val id: String) : CoreAPIRequest<GuestList> {

    companion object {
        const val INCLUDES = "invites,venue"
    }

    override fun createCall(api: CoreApi): Call<out GuestList> {
        return api.getGuestList(id, INCLUDES)
    }

}