package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Account
import com.sohohouse.seven.network.core.models.ProfileAccountUpdate
import retrofit2.Call

class PatchProfileAccountRequest(private val accountUpdate: ProfileAccountUpdate) :
    CoreAPIRequest<Account> {
    companion object {
        const val USER_ID = "me"
    }

    override fun createCall(api: CoreApi): Call<out Account> {
        accountUpdate.id = USER_ID
        return api.patchProfileAccount(accountUpdate)
    }
}