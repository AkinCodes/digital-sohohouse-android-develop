package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Invite
import com.sohohouse.seven.network.core.models.InviteUpdate
import retrofit2.Call

class PatchInviteRequest(val invite: InviteUpdate) : CoreAPIRequest<Invite> {
    override fun createCall(api: CoreApi): Call<out Invite> {
        return api.patchInvite(invite, invite.id)
    }

}