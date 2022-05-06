package com.sohohouse.seven.network.auth.request

import com.sohohouse.seven.network.auth.AuthApi
import com.sohohouse.seven.network.auth.error.AuthError
import com.sohohouse.seven.network.auth.model.LoginResponse
import com.sohohouse.seven.network.auth.model.TokenRequest
import com.sohohouse.seven.network.base.error.ServerError
import retrofit2.Call
import java.net.HttpURLConnection

class RefreshTokenRequest(private val refreshToken: String) :
    AuthenticationAPIRequest<LoginResponse> {
    override fun createCall(api: AuthApi): Call<out LoginResponse> {
        val tokenRequest = TokenRequest()
        tokenRequest.let {
            it.refreshToken = refreshToken
            it.grantType = TokenRequest.GrantTypeEnum.REFRESH_TOKEN
        }
        return api.login(tokenRequest)
    }

    override fun mapError(statusCode: Int, rawBody: String): ServerError {
        return when (statusCode) {
            HttpURLConnection.HTTP_UNAUTHORIZED -> AuthError.INVALID_CREDENTIALS
            else -> throw IllegalStateException("Unexpected login response")
        }
    }
}