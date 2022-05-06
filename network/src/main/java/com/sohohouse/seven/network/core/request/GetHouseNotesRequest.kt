package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.common.extensions.formatWithCommas
import com.sohohouse.seven.network.core.models.HouseNotes
import com.sohohouse.seven.network.core.models.Meta
import com.squareup.moshi.Moshi
import moe.banana.jsonapi2.JsonBuffer
import retrofit2.Call

class GetHouseNotesRequest(
    private val isFeatured: Boolean? = null,
    private val contentCategoryIds: Array<String>? = null,
    private val venueIds: Array<String>? = null,
    private val types: Array<String>? = null,
    override var page: Int? = null,
    override var perPage: Int? = null,
    private val includeResources: Array<String>? = null,
) : CoreAPIRequestPagable<List<HouseNotes>> {

    companion object {
        const val MAX_HOUSE_NOTES_PER_PAGE = 10
    }

    override fun createCall(api: CoreApi): Call<out List<HouseNotes>> {
        return api.getHouseNotes(
            isFeatured,
            contentCategoryIds?.formatWithCommas(),
            venueIds?.formatWithCommas(),
            types?.formatWithCommas(),
            page,
            perPage,
            includeResources?.formatWithCommas())
    }

    override fun getMeta(response: List<HouseNotes>): Meta? {
        val adapter = Moshi.Builder().build().adapter(Meta::class.java)
        if (response.isNotEmpty() && response[0].document.meta != null) {
            @Suppress("UNCHECKED_CAST")
            return (response[0].document.meta as JsonBuffer<Meta>).get(adapter)
        }
        return null
    }
}