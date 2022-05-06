package com.sohohouse.seven.network.auth

import androidx.test.espresso.idling.CountingIdlingResource
import com.sohohouse.seven.network.cache.CacheRequestInterceptor
import com.sohohouse.seven.network.cache.CacheResponseInterceptor
import com.sohohouse.seven.network.common.HeaderInterceptor
import com.sohohouse.seven.network.common.utils.NetworkVariantUtils
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
class AuthenticationModule {

    companion object {
        const val NAMED_AUTH_HOST_NAME = "auth_host_name"
    }

    @Provides
    @Singleton
    fun providesHeaderInterceptor(): HeaderInterceptor {
        return HeaderInterceptor("")
    }

    @Provides
    @Singleton
    fun providesAuthHeaderInterceptor(): AuthHeaderInterceptor {
        return AuthHeaderInterceptor()
    }

    @Provides
    @Singleton
    fun providesAuthenticationRequestFactory(
        @Named(NAMED_AUTH_HOST_NAME) hostname: String,
        headerInterceptor: HeaderInterceptor,
        authHeaderInterceptor: AuthHeaderInterceptor,
        cacheRequestInterceptor: CacheRequestInterceptor,
        cacheResponseInterceptor: CacheResponseInterceptor,
        idlingResource: CountingIdlingResource,
        client: OkHttpClient
    ): AuthenticationRequestFactory {

        val clientBuilder = client.newBuilder()
            .addNetworkInterceptor(headerInterceptor)
            .addNetworkInterceptor(authHeaderInterceptor)
            .addNetworkInterceptor(cacheRequestInterceptor)
            .addInterceptor(cacheResponseInterceptor)

        val gson = JSON().gson
        val api = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonCustomConverterFactory.create(gson))
            .client(clientBuilder.build())
            .baseUrl(hostname)
            .build()
            .create(AuthApi::class.java)

        return AuthenticationRequestFactory(api, idlingResource)
    }
}