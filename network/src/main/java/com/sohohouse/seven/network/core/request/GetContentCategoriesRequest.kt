package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.ContentCategory
import retrofit2.Call

class GetContentCategoriesRequest() : CoreAPIRequest<List<ContentCategory>> {
    override fun createCall(api: CoreApi): Call<out List<ContentCategory>> {
        return api.getContentCategories()
    }
}