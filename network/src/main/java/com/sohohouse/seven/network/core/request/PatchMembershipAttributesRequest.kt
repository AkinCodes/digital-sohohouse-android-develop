package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Membership
import com.sohohouse.seven.network.core.models.UpdateMembership
import retrofit2.Call
import java.util.*

class PatchMembershipAttributesRequest(
    private val inductedAt: Date,
) : CoreAPIRequest<Membership> {
    companion object {
        const val USER_ID = "me"
    }

    override fun createCall(api: CoreApi): Call<out Membership> {
        val membershipUpdate = UpdateMembership(inductedAt)
        membershipUpdate.id = USER_ID
        return api.patchMembershipAttributes(membershipUpdate)
    }
}