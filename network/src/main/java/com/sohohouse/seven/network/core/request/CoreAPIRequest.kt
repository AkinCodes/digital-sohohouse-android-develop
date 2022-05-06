package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.auth.error.AuthError
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.request.APIRequest
import com.sohohouse.seven.network.core.api.CoreApi
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection

interface CoreAPIRequest<S> : APIRequest<CoreApi, S> {
    override fun mapError(statusCode: Int, rawBody: String): ServerError {
        return when (statusCode) {
            HttpURLConnection.HTTP_NOT_FOUND -> ServerError.NOT_FOUND
            HttpURLConnection.HTTP_UNAUTHORIZED -> AuthError.INVALID_OAUTH_TOKEN
            else -> {
                val errors = JSONObject(rawBody).opt("errors") as JSONArray
                val errorsArray = Array<String>(errors.length()) { index ->
                    ((errors[index]) as JSONObject).getString("code")
                }

                ServerError.ApiError(*errorsArray)
            }
        }
    }
}