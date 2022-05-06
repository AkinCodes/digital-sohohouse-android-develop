package com.sohohouse.seven.network.places

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApi {

    @GET("/maps/api/place/autocomplete/json")
    fun getCities(
        @Query("input") input: String,
        @Query("inputType") inputType: String,
        @Query("types") types: String,
        @Query("language") language: String,
        @Query("sessiontoken") sessionToken: String,
    ): Call<PlacesResponse>

}