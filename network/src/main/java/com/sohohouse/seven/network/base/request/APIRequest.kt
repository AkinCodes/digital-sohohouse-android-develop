package com.sohohouse.seven.network.base.request

import com.sohohouse.seven.network.base.error.ServerError
import retrofit2.Call

@Deprecated("This class is deprecated. Try to use SohoApiService instead.")
interface APIRequest<API, out S> {
    fun createCall(api: API): Call<out S>
    fun mapError(statusCode: Int, rawBody: String): ServerError
}