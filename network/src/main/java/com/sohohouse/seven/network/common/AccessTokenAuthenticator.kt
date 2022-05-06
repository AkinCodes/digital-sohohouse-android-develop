package com.sohohouse.seven.network.common

import com.sohohouse.seven.network.auth.AuthFailureWhitelist
import com.sohohouse.seven.network.common.HeaderInterceptor.Companion.AUTHORIZATION_KEY
import com.sohohouse.seven.network.common.HeaderInterceptor.Companion.BEARER_IDENTIFIER_KEY
import com.sohohouse.seven.network.common.HeaderInterceptor.Companion.DID_RETRY_AUTH
import com.sohohouse.seven.network.common.interfaces.AuthHelper
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessTokenAuthenticator @Inject constructor(private val authHelper: AuthHelper) :
    Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        Timber.d("auth challenge for ${response.request.url}")

        if (response.request.header(DID_RETRY_AUTH) == "${true}") {
            Timber.e("auth retry already failed for ${response.request.url}")
            if ((response.request.url.encodedPath in AuthFailureWhitelist.endpoints).not()) {
                authHelper.forceLogout("Auth failed for: ${response.request.url.encodedPath}")
            }
            return null // we have already retried auth for this call, give up
        }

        // Current request token
        val oldToken = authHelper.token

        // Gets all 401 in sync blocks to avoid multiply token updates...
        synchronized(this) {
            var request: Request? = null

            //If other call already request a new token this should be different
            val newToken: String = authHelper.token

            // If the token has changed since the request was made, use the new token.
            if (newToken.isNotEmpty() && newToken != oldToken) {
                return rebuildRequest(response, newToken)
            }

            Timber.d("requesting fresh token for ${response.request.url}")
            authHelper.requestRefreshToken { token ->
                request = rebuildRequest(response, token)
            }

            request?.let {
                return it
            } ?: kotlin.run {
                authHelper.logout()
                return null
            }
        }

    }

    private fun rebuildRequest(response: Response, newToken: String): Request? {
        Timber.d("rebuilding & retrying request with fresh token for ${response.request.url}")
        return response.request
            .newBuilder()
            .header(AUTHORIZATION_KEY, "$BEARER_IDENTIFIER_KEY $newToken")
            .header(DID_RETRY_AUTH, "${true}")
            .build()
    }

}