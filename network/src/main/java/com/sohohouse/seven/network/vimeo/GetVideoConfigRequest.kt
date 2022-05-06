package com.sohohouse.seven.network.vimeo

import com.sohohouse.seven.network.auth.error.AuthError
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.request.APIRequest
import com.sohohouse.seven.network.vimeo.model.VimeoResponse
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import java.net.HttpURLConnection

class GetVideoConfigRequest(private val id: String) : APIRequest<VimeoApi, VimeoResponse?> {

    override fun createCall(api: VimeoApi): Call<VimeoResponse?> {
        return api.getVideoConfig(id)
    }

    override fun mapError(statusCode: Int, rawBody: String): ServerError {
        return when (statusCode) {
            HttpURLConnection.HTTP_UNAUTHORIZED -> AuthError.INVALID_OAUTH_TOKEN
            else -> {
                val errors = JSONObject(rawBody).opt("errors") as? JSONArray

                val errorsArray = if (errors != null) {
                    Array<String>(errors.length()) { index ->
                        ((errors[index]) as JSONObject).getString("code")
                    }
                } else {
                    emptyArray()
                }

                ServerError.ApiError(*errorsArray)
            }
        }
    }
}