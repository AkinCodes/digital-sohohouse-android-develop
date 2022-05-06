package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Meta
import com.sohohouse.seven.network.core.models.Perk
import com.squareup.moshi.Moshi
import moe.banana.jsonapi2.JsonBuffer
import retrofit2.Call

class GetPerksRequest(
    val region: String? = null, val cities: String? = null, override var page: Int? = null,
    override var perPage: Int? = DEFAULT_PERKS_PER_PAGE,
) : CoreAPIRequestPagable<List<Perk>> {

    companion object {
        const val DEFAULT_PERKS_PER_PAGE = 9
        const val HOME_PERKS_PER_PAGE = 10

        // In benefit details screen, we don't show the same perk in the carousel
        // So we fetch 11 items, filter out and take 10 at the end
        const val PERKS_DETAIL_PER_PAGE = 11

        fun getPerksByRegion(
            region: String,
            perPage: Int = DEFAULT_PERKS_PER_PAGE,
        ): GetPerksRequest {
            return GetPerksRequest(region = region, perPage = perPage)
        }
    }

    override fun createCall(api: CoreApi): Call<List<Perk>> {
        return api.getPerks(page, perPage, region, cities)
    }

    override fun getMeta(response: List<Perk>): Meta? {
        val adapter = Moshi.Builder().build().adapter(Meta::class.java)
        if (response.isNotEmpty() && response[0].document.meta != null) {
            @Suppress("UNCHECKED_CAST")
            return (response[0].document.meta as JsonBuffer<Meta>).get(adapter)
        }
        return null
    }
}