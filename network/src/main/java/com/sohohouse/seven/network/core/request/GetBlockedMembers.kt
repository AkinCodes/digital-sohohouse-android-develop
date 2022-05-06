package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.BlockedMemberList
import retrofit2.Call

object GetBlockedMembers : CoreAPIRequest<BlockedMemberList> {
    override fun createCall(api: CoreApi): Call<BlockedMemberList> {
        return api.getBlockedMembers()
    }

}
