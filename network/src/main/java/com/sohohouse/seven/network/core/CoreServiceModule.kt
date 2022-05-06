package com.sohohouse.seven.network.core

import androidx.test.espresso.idling.CountingIdlingResource
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.sohohouse.seven.network.cache.CacheRequestInterceptor
import com.sohohouse.seven.network.cache.CacheResponseInterceptor
import com.sohohouse.seven.network.chat.invite.SentMessageRequest
import com.sohohouse.seven.network.common.AccessTokenAuthenticator
import com.sohohouse.seven.network.common.HeaderInterceptor
import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.*
import com.sohohouse.seven.network.core.models.housepay.Check
import com.sohohouse.seven.network.core.models.housepay.HouseCredit
import com.sohohouse.seven.network.core.models.housepay.Location
import com.sohohouse.seven.network.core.models.housepay.Payment
import com.sohohouse.seven.network.core.models.notification.DeviceRegistration
import com.sohohouse.seven.network.core.models.notification.Notification
import com.sohohouse.seven.network.core.models.notification.NotificationGroup
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import moe.banana.jsonapi2.JsonApiConverterFactory
import moe.banana.jsonapi2.ResourceAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class CoreServiceModule {

    @Provides
    @Named(NAMED_CORE_MOSHI)
    fun providesMoshi(): Moshi {
        val jsonApiAdapterFactory = ResourceAdapterFactory.builder()
            .add(Account::class.java)
            .add(Card::class.java)
            .add(Checkin::class.java)
            .add(CommunicationPreference::class.java)
            .add(ContentCategory::class.java)
            .add(Event::class.java)
            .add(EventBooking::class.java)
            .add(EventCategory::class.java)
            .add(EventInstructor::class.java)
            .add(EventResource::class.java)
            .add(EventsFilm::class.java)
            .add(EventsTranslation::class.java)
            .add(Faq::class.java)
            .add(Form::class.java)
            .add(Hotel::class.java)
            .add(HouseNotes::class.java)
            .add(Inquiry::class.java)
            .add(Membership::class.java)
            .add(Profile::class.java)
            .add(Interest::class.java)
            .add(Occupation::class.java)
            .add(RedirectUrl::class.java)
            .add(StaticPages::class.java)
            .add(Venue::class.java)
            .add(House::class.java)
            .add(RestaurantInfo::class.java)
            .add(HouseNotesSection::class.java)
            .add(HouseNotesPhotoGallery::class.java)
            .add(HouseNotesVideo::class.java)
            .add(RoomBooking::class.java)
            .add(Room::class.java)
            .add(Attendance::class.java)
            .add(Notification::class.java)
            .add(NotificationGroup::class.java)
            .add(SendVerificationLink::class.java)
            .add(TableAvailabilities::class.java)
            .add(SlotLock::class.java)
            .add(GuestList::class.java)
            .add(Invite::class.java)
            .add(Unknown::class.java)
            .add(Feature::class.java)
            .add(City::class.java)
            .add(DeviceRegistration::class.java)
            .add(Connections::class.java)
            .add(MutualConnections::class.java)
            .add(MutualConnectionRequests::class.java)
            .add(BlockedMemberList::class.java)
            .add(ProfileAvailabilityStatus::class.java)
            .add(Check::class.java)
            .add(Location::class.java)
            .add(Payment::class.java)
            .add(HouseCredit::class.java)
            .add(SentMessageRequest::class.java)
            .add(SendBirdToken::class.java)
            .add(SendBirdTokenRequest::class.java)
            .add(RecommendationDto::class.java)
            .add(CheckInReactionByUser::class.java)
            .add(AccountOnboarding::class.java)
            .build()

        return Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .add(jsonApiAdapterFactory)
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun providesCoreRequestFactory(
        @Named(NAMED_CORE_SERVICE_HOST_NAME) hostname: String,
        @Named(NAMED_CORE_MOSHI)
        moshi: Moshi,
        @Named(NAMED_CORE_OKHTTP_CLIENT)
        client: OkHttpClient,
        idlingResource: CountingIdlingResource,
    ): CoreRequestFactory {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(JsonApiConverterFactory.create(moshi))
            .client(client)
            .baseUrl(hostname)
            .build()

        return CoreRequestFactory(
            retrofit.create(CoreApi::class.java),
            idlingResource
        )
    }

    @Provides
    @Singleton
    fun providesSohoRepository(
        @Named(NAMED_CORE_SERVICE_HOST_NAME) hostname: String,
        @Named(NAMED_CORE_MOSHI)
        moshi: Moshi,
        @Named(NAMED_CORE_OKHTTP_CLIENT)
        client: OkHttpClient,
        networkErrorReporter: BaseApiService.NetworkErrorReporter,
        @Named(NAMED_IO_DISPATCHER)
        ioDispatcher: CoroutineDispatcher,
    ): SohoApiService {

        val retrofit = Retrofit.Builder()
            .addConverterFactory(JsonApiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
            .baseUrl(hostname)
            .build()

        return SohoApiService(
            retrofit.create(CoreApi::class.java),
            networkErrorReporter,
            ioDispatcher
        )
    }

    @Provides
    @Singleton
    @Named(NAMED_CORE_OKHTTP_CLIENT)
    fun providesOkHttpClient(
        headerInterceptor: HeaderInterceptor,
        accessTokenAuthenticator: AccessTokenAuthenticator,
        client: OkHttpClient,
        cacheRequestInterceptor: CacheRequestInterceptor,
        cacheResponseInterceptor: CacheResponseInterceptor,
    ): OkHttpClient {
        val clientBuilder = client.newBuilder()
            .authenticator(accessTokenAuthenticator)
            .addNetworkInterceptor(headerInterceptor)
            .addNetworkInterceptor(cacheRequestInterceptor)
            .addInterceptor(cacheResponseInterceptor)
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)

        return clientBuilder.build()
    }

    @Provides
    @Named(NAMED_IO_DISPATCHER)
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    companion object {
        const val NAMED_CORE_SERVICE_HOST_NAME = "core_service_host_name"
        const val NAMED_CORE_MOSHI = "core_moshi"
        const val NAMED_IO_DISPATCHER = "io_dispatcher"
        const val NAMED_CORE_OKHTTP_CLIENT = "NAMED_CORE_OKHTTP_CLIENT"
    }
}
