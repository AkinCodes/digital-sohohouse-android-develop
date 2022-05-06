package com.sohohouse.seven.network.sitecore

import com.sohohouse.seven.network.auth.error.AuthError
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.request.APIRequest
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection

interface SitecoreApiRequest<S> : APIRequest<SitecoreApi, S> {
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