package com.sohohouse.seven.network.forceupdate

import com.sohohouse.seven.network.cache.CacheRequestInterceptor
import com.sohohouse.seven.network.cache.CacheResponseInterceptor
import com.sohohouse.seven.network.common.utils.NetworkVariantUtils
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import moe.banana.jsonapi2.JsonApiConverterFactory
import moe.banana.jsonapi2.ResourceAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Named

@Module
class ForceUpdateModule {

    companion object {
        const val NAMED_FORCE_UPDATE_HOST_NAME = "force_update_host_name"
    }

    @Provides
    fun providesForceUpdateRequestFactory(
        @Named(NAMED_FORCE_UPDATE_HOST_NAME) hostname: String,
        cacheRequestInterceptor: CacheRequestInterceptor,
        cacheResponseInterceptor: CacheResponseInterceptor,
        client: OkHttpClient
    ): ForceUpdateRequestFactory {
        val clientBuilder = client.newBuilder()
            .addNetworkInterceptor(cacheRequestInterceptor)
            .addInterceptor(cacheResponseInterceptor)

        val retrofit = Retrofit.Builder()
            .addConverterFactory(JsonApiConverterFactory.create(getMosh()))
            .client(clientBuilder.build())
            .baseUrl(hostname)
            .build()

        return ForceUpdateRequestFactory(retrofit.create(ForceUpdateApi::class.java))
    }

    private fun getMosh(): Moshi {
        return Moshi.Builder()
            .add(ResourceAdapterFactory.builder().build())
            .build()
    }
}