package com.sohohouse.seven.network.common

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Singleton

@Singleton
class HeaderInterceptor(var sessionToken: String = "") : Interceptor {

    companion object {
        const val AUTHORIZATION_KEY = "Authorization"
        const val BEARER_IDENTIFIER_KEY = "Bearer"
        const val AUTHORIZATION_RESPONSE_KEY = "Www-Authenticate"
        const val BEARER_DOORKEEPER_KEY = "realm=\"Doorkeeper\""
        const val DID_RETRY_AUTH = "did-retry-auth"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        if (sessionToken.isNotEmpty()) {
            builder.header(AUTHORIZATION_KEY, "$BEARER_IDENTIFIER_KEY $sessionToken")
        } else {
            builder.removeHeader(AUTHORIZATION_KEY)
        }

        return chain.proceed(builder.build())
    }
}
