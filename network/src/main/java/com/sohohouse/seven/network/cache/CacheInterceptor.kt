package com.sohohouse.seven.network.cache

import android.content.Context
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.Request
import timber.log.Timber
import java.io.File

abstract class CacheInterceptor : Interceptor {

    companion object {
        const val HEADER_CACHE_CONTROL = "Cache-Control"
        const val CACHE_CONTROL_PUBLIC_ONLY_IF_CACHED = "public, only-if-cached"
        const val CACHE_CONTROL_NO_STORE = "no-store"
        const val CACHE_CONTROL_MUST_REVALIDATE = "must-revalidate"
        const val CACHE_CONTROL_NO_CACHE = "no-cache"
        const val CACHE_CONTROL_PRIVATE = "private"
        const val CACHE_CONTROL_MAX_AGE_ZERO = "max-age=0"

        private val DATE_TIME_QUERIES = listOf(
            "filter[venue_date][from]",
            "filter[venue_date][to]",
            "filter[venue_end_date][from]",
            "filter[venue_end_date][to]",
            "filter[starts_at][from]",
            "filter[starts_at][to]",
            "filter[ends_at][from]")

        private const val DISK_CACHE_SIZE = 10L * 1024 * 1024 // 10 MB

        fun initCache(context: Context): Cache? {
            return try {
                val cacheDir: File = context.externalCacheDir ?: context.cacheDir
                Cache(cacheDir, DISK_CACHE_SIZE)
            } catch (e: Exception) {
                Timber.e("Error creating OkHttp cache: " + e.message)
                null
            }
        }
    }

    protected fun modifyRequest(request: Request): Request {
        val queries = request.url.queryParameterNames.filter { DATE_TIME_QUERIES.contains(it) }
        val builder = request.url.newBuilder()
        queries.forEach {
            builder.removeAllQueryParameters(it)
            builder.removeAllEncodedQueryParameters(it)
        }
        val uri = builder.build()
        return request.newBuilder().url(uri).build()
    }
}