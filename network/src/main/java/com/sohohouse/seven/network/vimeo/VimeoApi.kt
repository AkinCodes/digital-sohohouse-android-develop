package com.sohohouse.seven.network.vimeo

import com.sohohouse.seven.network.vimeo.model.VimeoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface VimeoApi {

    @GET("{id}/config")
    fun getVideoConfig(@Path("id") id: String?): Call<VimeoResponse?>

}