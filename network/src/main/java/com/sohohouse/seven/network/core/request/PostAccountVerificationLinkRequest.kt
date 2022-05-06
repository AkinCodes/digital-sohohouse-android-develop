package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.SendVerificationLink
import moe.banana.jsonapi2.HasOne
import retrofit2.Call

class PostAccountVerificationLinkRequest(private val globalId: String) :
    CoreAPIRequest<SendVerificationLink> {
    override fun createCall(api: CoreApi): Call<out SendVerificationLink> {

        return api.postAccountVerificationEmail(SendVerificationLink(HasOne("accounts", globalId)))
    }
}