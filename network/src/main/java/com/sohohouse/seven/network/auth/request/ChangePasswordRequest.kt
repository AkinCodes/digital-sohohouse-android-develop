package com.sohohouse.seven.network.auth.request

import com.sohohouse.seven.network.auth.AuthApi
import com.sohohouse.seven.network.auth.error.AuthError
import com.sohohouse.seven.network.auth.model.ChangePasswordRequest
import com.sohohouse.seven.network.base.error.ServerError
import retrofit2.Call
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

class ChangePasswordRequest(
    private val oldPassword: String,
    private val newPassword: String,
    private val newPasswordConfirm: String,
) : AuthenticationAPIRequest<Void> {

    override fun createCall(api: AuthApi): Call<Void> {
        val requestBody = ChangePasswordRequest()
        requestBody.let {
            it.oldPassword = oldPassword
            it.newPassword = newPassword
            it.newPasswordConfirm = newPasswordConfirm
        }
        return api.changePassword(requestBody)
    }

    override fun mapError(statusCode: Int, rawBody: String): ServerError {
        return when (statusCode) {
            HTTP_UNAUTHORIZED -> AuthError.INVALID_CREDENTIALS
            else -> throw IllegalStateException("Unexpected login response")
        }
    }
}