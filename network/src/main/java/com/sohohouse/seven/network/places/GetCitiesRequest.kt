package com.sohohouse.seven.network.places

import retrofit2.Call

class GetCitiesRequest(private val input: String, private val sessionToken: String) :
    CorePlacesRequest<PlacesResponse> {

    override fun createCall(api: PlacesApi): Call<PlacesResponse> {
        return api.getCities(input = input,
            inputType = Parameters.inputType,
            types = Parameters.types,
            language = Parameters.language,
            sessionToken = sessionToken)

    }

    object Parameters {
        const val inputType = "textQuery"
        const val types = "(cities)"
        const val language = "en"
    }

}