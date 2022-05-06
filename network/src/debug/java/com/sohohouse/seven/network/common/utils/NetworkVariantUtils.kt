package com.sohohouse.seven.network.common.utils

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object NetworkVariantUtils {
    fun addLoggingInterceptor(builder: OkHttpClient.Builder) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)
    }
}
