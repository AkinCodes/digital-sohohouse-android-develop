package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.sitecore.SitecoreApi
import com.sohohouse.seven.network.sitecore.SitecoreApiRequest
import com.sohohouse.seven.network.sitecore.models.SitecoreResponse
import retrofit2.Call

class GetHouseNoteSitecoreRequest(private val articleSlug: String) :
    SitecoreApiRequest<SitecoreResponse> {
    override fun createCall(api: SitecoreApi): Call<out SitecoreResponse> {
        return api.getHouseNoteSitecore(articleSlug)
    }
}