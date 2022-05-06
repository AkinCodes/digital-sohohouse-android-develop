package com.sohohouse.seven.network.di

import com.sohohouse.seven.network.BuildConfig
import com.sohohouse.seven.network.auth.AuthenticationModule
import com.sohohouse.seven.network.common.utils.NetworkVariantUtils
import com.sohohouse.seven.network.core.CoreServiceModule
import com.sohohouse.seven.network.forceupdate.ForceUpdateModule
import com.sohohouse.seven.network.places.PlacesServiceModule
import com.sohohouse.seven.network.sitecore.SitecoreServiceModule
import com.sohohouse.seven.network.vimeo.VimeoModule
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module(includes = [
    CoreServiceModule::class,
    ForceUpdateModule::class,
    PlacesServiceModule::class,
    VimeoModule::class,
    AuthenticationModule::class,
    SitecoreServiceModule::class,

])
class NetworkModule {

    @Provides
    @Singleton
    fun okhttpClient(
        crashReportLoggingInterceptor: HttpLoggingInterceptor,
        cache: Cache?,
    ): OkHttpClient {

        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(crashReportLoggingInterceptor)
            .cache(cache)

        NetworkVariantUtils.addLoggingInterceptor(clientBuilder)

        if (BuildConfig.DEBUG) {
            clientBuilder.ignoreAllSSLErrors()
        }
        return clientBuilder.build()
    }
}

fun OkHttpClient.Builder.ignoreAllSSLErrors(): OkHttpClient.Builder {
    val naiveTrustManager = object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) = Unit
        override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) = Unit
    }

    val insecureSocketFactory = SSLContext.getInstance("TLSv1.2").apply {
        val trustAllCerts = arrayOf<TrustManager>(naiveTrustManager)
        init(null, trustAllCerts, SecureRandom())
    }.socketFactory

    sslSocketFactory(insecureSocketFactory, naiveTrustManager)
    hostnameVerifier(HostnameVerifier { _, _ -> true })
    return this
}
