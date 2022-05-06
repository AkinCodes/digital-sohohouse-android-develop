package com.sohohouse.seven.network.sitecore

import com.sohohouse.seven.network.sitecore.models.HouseNotesResponse
import com.sohohouse.seven.network.sitecore.models.SitecoreResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SitecoreApi {

    @GET("/sitecore/sitecore/api/layout/render/jss")
    fun getHouseNoteSitecore(@Query(value = "item") articleSlug: String): Call<SitecoreResponse>

    @GET("/sitecore/sitecore/api/ssc/aggregate/content/Items")
    fun getHouseNotes(
        @Query("\$filter") filter: String,
        @Query("\$top") top: Int,
        @Query("\$skip") skip: Int,
        @Query("\$count") count: Boolean,
        @Query("\$expand") expand: String,
        @Query("\$orderby") orderby: String,
        @Query("sc_apikey") apikey: String,
    ): Call<HouseNotesResponse>
}