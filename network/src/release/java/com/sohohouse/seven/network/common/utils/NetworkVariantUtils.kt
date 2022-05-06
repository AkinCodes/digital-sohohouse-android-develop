package com.sohohouse.seven.network.common.utils

import okhttp3.OkHttpClient

object NetworkVariantUtils {
    infix fun addLoggingInterceptor(
            @Suppress("UNUSED_PARAMETER") builder: OkHttpClient.Builder) {
        // do nothing
    }
}