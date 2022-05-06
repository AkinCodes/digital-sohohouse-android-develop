package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Invite
import com.sohohouse.seven.network.core.models.JSON_API_TYPE_GUEST_LISTS
import moe.banana.jsonapi2.HasOne
import retrofit2.Call

class PostInviteRequest(private val guestListId: String, private val guestName: String) :
    CoreAPIRequest<Invite> {
    override fun createCall(api: CoreApi): Call<out Invite> {
        return api.postInvite(Invite(guestName = guestName,
            guestList = HasOne(JSON_API_TYPE_GUEST_LISTS, guestListId)))
    }
}