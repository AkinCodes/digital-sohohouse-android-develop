package com.sohohouse.seven.network.cache

import android.content.Context
import com.sohohouse.seven.network.utils.NetworkUtils
import okhttp3.Interceptor
import okhttp3.Response

class CacheRequestInterceptor(private val context: Context) : CacheInterceptor() {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val headers = response.header(HEADER_CACHE_CONTROL)

        return if (NetworkUtils.isNetworkConnected(context) && (headers == null
                    || headers.contains(CACHE_CONTROL_NO_STORE)
                    || headers.contains(CACHE_CONTROL_MUST_REVALIDATE)
                    || headers.contains(CACHE_CONTROL_NO_CACHE)
                    || headers.contains(CACHE_CONTROL_MAX_AGE_ZERO)
                    || headers.contains(CACHE_CONTROL_PRIVATE))
        ) {
            response.newBuilder()
                .header(HEADER_CACHE_CONTROL, CACHE_CONTROL_PUBLIC_ONLY_IF_CACHED)
                .request(modifyRequest(request))
                .build()
        } else {
            response.newBuilder()
                .header(HEADER_CACHE_CONTROL, CACHE_CONTROL_MAX_AGE_ZERO)
                .request(modifyRequest(request))
                .build()
        }
    }
}