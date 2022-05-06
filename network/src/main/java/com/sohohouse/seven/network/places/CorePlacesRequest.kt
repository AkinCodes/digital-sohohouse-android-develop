package com.sohohouse.seven.network.places

import com.sohohouse.seven.network.auth.error.AuthError
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.request.APIRequest
import java.net.HttpURLConnection

interface CorePlacesRequest<S> : APIRequest<PlacesApi, S> {
    override fun mapError(statusCode: Int, rawBody: String): ServerError {
        return when (statusCode) {
            HttpURLConnection.HTTP_UNAUTHORIZED -> AuthError.INVALID_OAUTH_TOKEN
            else -> {
                ServerError.COMPLETE_MELTDOWN
            }
        }
    }
}