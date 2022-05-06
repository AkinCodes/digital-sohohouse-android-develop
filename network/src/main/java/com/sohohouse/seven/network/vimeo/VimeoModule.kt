package com.sohohouse.seven.network.vimeo

import androidx.test.espresso.idling.CountingIdlingResource
import com.sohohouse.seven.network.common.utils.NetworkVariantUtils
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class VimeoModule {

    @Provides
    @Singleton
    fun providesVimeoRequestFactory(
        client: OkHttpClient,
        idlingResource: CountingIdlingResource,
    ): VimeoRequestFactory {

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(VIMEO_HOST_NAME)
            .build()

        return VimeoRequestFactory(retrofit.create(VimeoApi::class.java), idlingResource)
    }

    companion object {
        private const val VIMEO_HOST_NAME = "https://player.vimeo.com/video/"
    }

}