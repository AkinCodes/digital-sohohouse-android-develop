package com.sohohouse.seven.network.cache

import android.content.Context
import com.sohohouse.seven.network.utils.NetworkUtils
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

class CacheResponseInterceptor(private val context: Context) : CacheInterceptor() {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = if (!NetworkUtils.isNetworkConnected(context)) {
            chain.request()
                .run { modifyRequest(this) }
                .newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}