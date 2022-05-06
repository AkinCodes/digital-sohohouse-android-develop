package com.sohohouse.seven.network.auth.request

import com.sohohouse.seven.network.auth.AuthApi
import com.sohohouse.seven.network.auth.error.AuthError
import com.sohohouse.seven.network.auth.model.LoginResponse
import com.sohohouse.seven.network.auth.model.TokenRequest
import com.sohohouse.seven.network.base.error.ServerError
import retrofit2.Call
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

class LoginRequest(
    private val email: String,
    private val password: String,
    private val clientSecret: String,
    private val clientID: String,
) : AuthenticationAPIRequest<LoginResponse> {

    override fun createCall(api: AuthApi): Call<out LoginResponse> {
        val requestBody = TokenRequest()
        requestBody.let {
            it.email = email
            it.password = password
            it.clientId = clientID
            it.clientSecret = clientSecret
            it.grantType = TokenRequest.GrantTypeEnum.PASSWORD
        }
        return api.login(requestBody)
    }

    override fun mapError(statusCode: Int, rawBody: String): ServerError {
        return when (statusCode) {
            HTTP_UNAUTHORIZED -> AuthError.INVALID_CREDENTIALS
            else -> throw IllegalStateException("Unexpected login response")
        }
    }
}