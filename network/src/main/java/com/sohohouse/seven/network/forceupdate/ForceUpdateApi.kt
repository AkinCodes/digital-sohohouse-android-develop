package com.sohohouse.seven.network.forceupdate

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ForceUpdateApi {
    @GET("/force_update")
    fun checkForceUpdate(
        @Header("x-sh-app-version") version: String? = null,
        @Header("x-sh-app-platform") platform: String? = null,
    ): Call<Void>
}