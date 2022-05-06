package com.sohohouse.seven.network.places

import com.sohohouse.seven.network.cache.CacheRequestInterceptor
import com.sohohouse.seven.network.cache.CacheResponseInterceptor
import com.sohohouse.seven.network.common.utils.NetworkVariantUtils
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class PlacesServiceModule {

    @Provides
    @Singleton
    fun providesPlacesRequestFactory(@Named(PLACE_SERVICE_API) client: OkHttpClient): PlacesRequestFactory {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(BASE_URL)
            .build()

        return PlacesRequestFactory(retrofit.create(PlacesApi::class.java))
    }

    @Provides
    @Singleton
    @Named(PLACE_SERVICE_API)
    fun provideOkHttpClient(
        @Named("PLACES_API_KEY") apiKey: String,
        crashReportLoggingInterceptor: HttpLoggingInterceptor,
        cache: Cache?,
        cacheRequestInterceptor: CacheRequestInterceptor,
        cacheResponseInterceptor: CacheResponseInterceptor,
        client: OkHttpClient,
    ): OkHttpClient {
        return client.newBuilder()
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    var request = chain.request()
                    val url = request.url.newBuilder().addQueryParameter("key", apiKey).build()
                    request = request.newBuilder().url(url).build()
                    return chain.proceed(request)
                }
            })
            .build()
    }

    companion object {
        const val BASE_URL = "https://maps.googleapis.com"
        const val PLACE_SERVICE_API = "PLACE_SERVICE_API"
    }

}