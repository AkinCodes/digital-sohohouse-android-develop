package com.sohohouse.seven.network.sitecore

import androidx.test.espresso.idling.CountingIdlingResource
import com.sohohouse.seven.network.cache.CacheRequestInterceptor
import com.sohohouse.seven.network.cache.CacheResponseInterceptor
import com.sohohouse.seven.network.common.AccessTokenAuthenticator
import com.sohohouse.seven.network.common.HeaderInterceptor
import com.sohohouse.seven.network.core.CoreServiceModule
import com.sohohouse.seven.network.sitecore.models.*
import com.sohohouse.seven.network.sitecore.models.template.EditorialStoryPage
import com.sohohouse.seven.network.sitecore.models.template.HouseNotePage
import com.sohohouse.seven.network.sitecore.models.template.Template
import com.sohohouse.seven.network.sitecore.models.template.UnknownTemplate
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

@Module
class SitecoreServiceModule {

    @Provides
    @Named(NAMED_SITECORE_MOSHI)
    fun providesMoshi(): Moshi {
        return Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .add(PolymorphicJsonAdapterFactory.of(SitecoreComponent::class.java, "componentName")
                .withSubtype(SitecoreBodyEditorialBlockComponent::class.java,
                    SitecoreComponent.Type.BodyEditorialBlock.name)
                .withSubtype(SitecoreBodyEditorialVideoComponent::class.java,
                    SitecoreComponent.Type.BodyEditorialVideo.name)
                .withSubtype(SitecoreCarouselGalleryComponent::class.java,
                    SitecoreComponent.Type.CarouselGallery.name)
                .withSubtype(SitecoreGridGalleryComponent::class.java,
                    SitecoreComponent.Type.GridGallery.name)
                .withSubtype(SitecoreNoteDetailsComponent::class.java,
                    SitecoreComponent.Type.NoteDetails.name)
                .withSubtype(EditorialHeaderComponent::class.java,
                    SitecoreComponent.Type.EditorialHeader.name)
                .withSubtype(SitecoreEditorialRichTextComponent::class.java,
                    SitecoreComponent.Type.EditorialRichText.name)
                .withSubtype(SitecoreEditorialFullImageComponent::class.java,
                    SitecoreComponent.Type.EditorialFullImage.name)
                .withSubtype(SitecoreEditorialBodyThreeComponent::class.java,
                    SitecoreComponent.Type.EditorialBodyThree.name)
                .withSubtype(SitecoreTwoImageGalleryComponent::class.java,
                    SitecoreComponent.Type.TwoImageGallery.name)
                .withSubtype(SitecoreThreeImageGalleryComponent::class.java,
                    SitecoreComponent.Type.ThreeImageGallery.name)
                .withSubtype(LandscapeImageComponent::class.java,
                    SitecoreComponent.Type.LandscapeImage.name)
                .withSubtype(SitecoreEditorialLandscapeComponent::class.java,
                    SitecoreComponent.Type.EditorialLandscapeImage.name)
                .withSubtype(SitecoreEditorialPortraitComponent::class.java,
                    SitecoreComponent.Type.EditorialPortraitImage.name)
                .withSubtype(HeroComponent::class.java, SitecoreComponent.Type.Hero.name)
                .withSubtype(GalleryStackComponent::class.java,
                    SitecoreComponent.Type.GalleryStack.name)
                .withSubtype(TileImageComponent::class.java, SitecoreComponent.Type.TileImage.name)
                .withSubtype(TileImageContainerComponent::class.java,
                    SitecoreComponent.Type.TileImageContainer.name)
                .withSubtype(EditorialQuoteComponent::class.java,
                    SitecoreComponent.Type.EditorialQuote.name)
                .withSubtype(EmptyComponent::class.java, SitecoreComponent.Type.Empty.name)
                .withSubtype(UnknownComponent::class.java, SitecoreComponent.Type.Unknown.name)
                .withDefaultValue(UnknownComponent(""))
            ).add(PolymorphicJsonAdapterFactory.of(Template::class.java, Template.TEMPLATE_NAME)
                .withSubtype(HouseNotePage::class.java, Template.TEMPLATE_HOUSE_NOTE_PAGE)
                .withSubtype(EditorialStoryPage::class.java, Template.TEMPLATE_EDITORIAL_STORY_PAGE)
                .withDefaultValue(UnknownTemplate()))
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun providesSitecoreRequestFactory(
        @Named(CoreServiceModule.NAMED_CORE_SERVICE_HOST_NAME) hostname: String,
        @Named(NAMED_SITECORE_MOSHI) moshi: Moshi,
        @Named(NAMED_SITECORE_OKHTTP_CLIENT) client: OkHttpClient,
        idlingResource: CountingIdlingResource,
    ): SitecoreRequestFactory {

        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .baseUrl(hostname)
            .build()

        return SitecoreRequestFactory(retrofit.create(SitecoreApi::class.java), idlingResource)
    }

    @Provides
    @Singleton
    @Named(NAMED_SITECORE_OKHTTP_CLIENT)
    fun providesOkHttpClient(
        headerInterceptor: HeaderInterceptor,
        accessTokenAuthenticator: AccessTokenAuthenticator,
        crashReportLoggingInterceptor: HttpLoggingInterceptor,
        client: OkHttpClient,
        cacheRequestInterceptor: CacheRequestInterceptor,
        cacheResponseInterceptor: CacheResponseInterceptor,
    ): OkHttpClient {

        val clientBuilder = client.newBuilder()
            .addInterceptor(crashReportLoggingInterceptor)
            .authenticator(accessTokenAuthenticator)
            .addNetworkInterceptor(headerInterceptor)
            .addNetworkInterceptor(cacheRequestInterceptor)
            .addInterceptor(cacheResponseInterceptor)

        return clientBuilder.build()
    }

    companion object {
        const val NAMED_SITECORE_MOSHI = "sitecore_moshi"
        const val NAMED_SITECORE_OKHTTP_CLIENT = "sitecore_okhttp_client"
    }
}