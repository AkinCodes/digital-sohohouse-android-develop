package com.sohohouse.seven.network.auth.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.NewRedirectUrl
import com.sohohouse.seven.network.core.models.RedirectUrl
import com.sohohouse.seven.network.core.request.CoreAPIRequest
import moe.banana.jsonapi2.ObjectDocument
import retrofit2.Call

class OneTimeAuthTokenRequest(val webRedirewct: String) : CoreAPIRequest<RedirectUrl> {
    override fun createCall(api: CoreApi): Call<out RedirectUrl> {
        val document = ObjectDocument<NewRedirectUrl>()
        val redirectUrl = NewRedirectUrl(redirectUri = webRedirewct)
        document.set(redirectUrl)
        return api.postRedirectUrls(document)
    }
}