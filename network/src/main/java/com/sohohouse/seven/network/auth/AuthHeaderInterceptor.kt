package com.sohohouse.seven.network.auth

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Singleton

@Singleton
class AuthHeaderInterceptor : Interceptor {

    companion object {
        private const val ACCEPT_KEY = "ACCEPT"
        private const val ACCEPT_VALUE = "application/json"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.header(ACCEPT_KEY, ACCEPT_VALUE)

        return chain.proceed(builder.build())
    }
}