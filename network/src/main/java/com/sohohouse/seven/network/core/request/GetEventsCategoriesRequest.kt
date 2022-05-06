package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.EventCategory
import retrofit2.Call

class GetEventsCategoriesRequest() : CoreAPIRequest<List<EventCategory>> {
    override fun createCall(api: CoreApi): Call<out List<EventCategory>> {
        return api.getEventsCategories()
    }
}