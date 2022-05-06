package com.sohohouse.seven.common.dagger.module

import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.sohohouse.seven.App
import com.sohohouse.seven.BuildConfigManager
import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.authentication.AuthenticationFlowManager
import com.sohohouse.seven.base.mvvm.ErrorViewStateViewModel
import com.sohohouse.seven.base.mvvm.ErrorViewStateViewModelImpl
import com.sohohouse.seven.book.base.HouseRepository
import com.sohohouse.seven.book.base.HouseRepositoryImpl
import com.sohohouse.seven.book.eventdetails.StepperPresenter
import com.sohohouse.seven.book.eventdetails.StepperPresenterImpl
import com.sohohouse.seven.book.eventdetails.payment.repo.PaymentConfirmationRepo
import com.sohohouse.seven.book.events.EventCategoryRepository
import com.sohohouse.seven.book.events.EventCategoryRepositoryImpl
import com.sohohouse.seven.book.table.PhoneCodeRepository
import com.sohohouse.seven.branding.ThemeManager
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.analytics.FirebaseAnalyticsManager
import com.sohohouse.seven.common.deeplink.DeeplinkRepo
import com.sohohouse.seven.common.deeplink.DeeplinkRepoImpl
import com.sohohouse.seven.common.deeplink.DeeplinkViewModel
import com.sohohouse.seven.common.deeplink.DeeplinkViewModelImpl
import com.sohohouse.seven.common.error.ErrorReporter
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.prefs.*
import com.sohohouse.seven.common.refresh.AuthInteractor
import com.sohohouse.seven.common.user.AnalyticsConsent
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.user.UserSessionManager
import com.sohohouse.seven.common.utils.*
import com.sohohouse.seven.common.utils.encryption.PGP
import com.sohohouse.seven.common.utils.encryption.PublicKeyEncryptable
import com.sohohouse.seven.common.utils.imageloader.GlideImageLoader
import com.sohohouse.seven.common.utils.imageloader.ImageLoader
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.venue.VenueRepoImpl
import com.sohohouse.seven.common.views.EventStatusHelper
import com.sohohouse.seven.common.views.EventStatusHelperImpl
import com.sohohouse.seven.common.views.ExploreListFactory
import com.sohohouse.seven.common.work.SohoWorkerFactory
import com.sohohouse.seven.connect.filter.FilterManager
import com.sohohouse.seven.connect.filter.FilterManagerImpl
import com.sohohouse.seven.connect.filter.city.CityFilterRepository
import com.sohohouse.seven.connect.filter.city.CityFilterRepositoryImpl
import com.sohohouse.seven.connect.filter.house.HouseFilterRepository
import com.sohohouse.seven.connect.filter.house.HouseFilterRepositoryImpl
import com.sohohouse.seven.connect.filter.industry.IndustryFilterRepository
import com.sohohouse.seven.connect.filter.industry.IndustryFilterRepositoryImp
import com.sohohouse.seven.connect.filter.topic.TopicFilterRepository
import com.sohohouse.seven.connect.filter.topic.TopicFilterRepositoryImpl
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import com.sohohouse.seven.connect.mynetwork.ConnectionRepositoryImpl
import com.sohohouse.seven.connect.noticeboard.NoticeboardDataSourceFactory
import com.sohohouse.seven.connect.noticeboard.NoticeboardItemFactory
import com.sohohouse.seven.connect.noticeboard.NoticeboardRepository
import com.sohohouse.seven.connect.trafficlights.repo.TrafficLightsRepo
import com.sohohouse.seven.connect.trafficlights.repo.TrafficLightsRepoImpl
import com.sohohouse.seven.crashreports.CrashReportLogger
import com.sohohouse.seven.discover.benefits.BenefitsDataSourceFactory
import com.sohohouse.seven.discover.benefits.BenefitsRepo
import com.sohohouse.seven.discover.benefits.BenefitsRepoImpl
import com.sohohouse.seven.discover.housenotes.HouseNotesDataSourceFactory
import com.sohohouse.seven.discover.housenotes.HouseNotesRepo
import com.sohohouse.seven.discover.housenotes.HouseNotesRepoImpl
import com.sohohouse.seven.guests.GuestListRepository
import com.sohohouse.seven.guests.GuestListRepositoryImpl
import com.sohohouse.seven.home.happeningnow.HappeningNowListFactory
import com.sohohouse.seven.home.houseboard.repo.NotificationsRepo
import com.sohohouse.seven.home.houseboard.repo.NotificationsRepoImpl
import com.sohohouse.seven.home.houseboard.viewmodels.NotificationViewModel
import com.sohohouse.seven.home.houseboard.viewmodels.NotificationViewModelImpl
import com.sohohouse.seven.home.repo.EventsRepo
import com.sohohouse.seven.home.repo.EventsRepoImpl
import com.sohohouse.seven.home.repo.HomeInteractor
import com.sohohouse.seven.home.repo.HomeInteractorImpl
import com.sohohouse.seven.home.repo.*
import com.sohohouse.seven.housepay.CheckRepo
import com.sohohouse.seven.housepay.CheckRepoImpl
import com.sohohouse.seven.housepay.home.GetReceiptsUseCase
import com.sohohouse.seven.housepay.housecredit.HouseCreditRepo
import com.sohohouse.seven.housepay.housecredit.HouseCreditRepoImpl
import com.sohohouse.seven.housepay.payment.CheckPaymentMethodManager
import com.sohohouse.seven.housepay.payment.CheckPaymentMethodManagerImpl
import com.sohohouse.seven.network.cache.CacheInterceptor
import com.sohohouse.seven.network.cache.CacheRequestInterceptor
import com.sohohouse.seven.network.cache.CacheResponseInterceptor
import com.sohohouse.seven.network.common.interfaces.AuthHelper
import com.sohohouse.seven.network.core.BaseApiService
import com.sohohouse.seven.network.core.CoreRequestFactory
import com.sohohouse.seven.network.core.CoreServiceModule
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.sitecore.SitecoreRequestFactory
import com.sohohouse.seven.payment.repo.CardRepo
import com.sohohouse.seven.perks.filter.manager.BenefitsFilterManager
import com.sohohouse.seven.perks.filter.manager.RegionFilterManager
import com.sohohouse.seven.profile.ProfileRepository
import com.sohohouse.seven.profile.ProfileToMutualConnectionStatus
import com.sohohouse.seven.common.remoteconfig.RemoteConfigManager
import com.sohohouse.seven.common.uxcam.UXCamVendor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.logging.HttpLoggingInterceptor
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class, AppModuleBinds::class])
class AppModule {

    @Provides
    fun provideApplication(application: App) = application

    @Provides
    @Singleton
    fun provideGlideImageLoader(context: Context): ImageLoader = GlideImageLoader(context)

    @Provides
    @Singleton
    fun providesPGPEncryption(): PublicKeyEncryptable {
        return PGP()
    }

    @Provides
    @Synchronized
    @Singleton
    fun provideAnalyticsManager(
        analytics: FirebaseAnalytics,
        userManager: UserManager,
    ): AnalyticsManager {
        return FirebaseAnalyticsManager(Firebase.analytics, userManager).apply {
            setAnalyticsEnabled(userManager.analyticsConsent == AnalyticsConsent.ACCEPTED)
        }
    }

    @Provides
    @Singleton
    fun providesFirebaseAnalytics(context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    fun providesAuthInteractor(authInteractor: AuthInteractor): AuthHelper = authInteractor

    @Provides
    @Singleton
    fun provideStringProvider(context: Context): StringProvider {
        return StringProviderImpl(context.resources)
    }

    @Provides
    @Singleton
    fun providesRegionFilterManager(): RegionFilterManager {
        return RegionFilterManager()
    }

    @Singleton
    @Provides
    fun provideResources(context: Context): Resources {
        return context.resources
    }

    @Singleton
    @Provides
    @Named("PLACES_API_KEY")
    fun providesPlacesApiKey(): String = BuildVariantConfig.PLACES_API_KEY

    @Singleton
    @Provides
    fun providesStepperPresenter(
        houseManager: HouseManager,
        userManager: UserManager,
    ): StepperPresenter {
        return StepperPresenterImpl(houseManager, userManager)
    }

    @Singleton
    @Provides
    fun providesEventStatusHelper(
        userManager: UserManager,
        houseManager: HouseManager,
    ): EventStatusHelper {
        return EventStatusHelperImpl(userManager, houseManager)
    }

    @Singleton
    @Provides
    fun provideCrashReportLogger(userManager: UserManager): HttpLoggingInterceptor =
        HttpLoggingInterceptor(CrashReportLogger(userManager)).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun provideOkHttpCache(context: Context): Cache? = CacheInterceptor.initCache(context)

    @Singleton
    @Provides
    fun provideCacheRequestInterceptor(context: Context): CacheRequestInterceptor =
        CacheRequestInterceptor(context)

    @Singleton
    @Provides
    fun provideCacheResponseInterceptor(context: Context): CacheResponseInterceptor =
        CacheResponseInterceptor(context)

    @Singleton
    @Provides
    fun provideLocaleProvider(): LocaleProvider {
        return object : LocaleProvider {
            override fun getLocale(): Locale {
                return ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
            }
        }
    }

    @Singleton
    @Provides
    fun providePhoneCodeRepository(context: Context): PhoneCodeRepository =
        PhoneCodeRepository(context.resources)

    @Singleton
    @Provides
    fun providesNotificationsRepo(requestFactory: CoreRequestFactory): NotificationsRepo =
        NotificationsRepoImpl(requestFactory)

    @Provides
    fun provideAuthenticationFlowManager(
        userSessionManager: UserSessionManager,
        userManager: UserManager,
        featureFlags: FeatureFlags,
    ): AuthenticationFlowManager =
        AuthenticationFlowManager(userSessionManager, userManager, featureFlags)

    @Singleton
    @Provides
    fun provideDeeplinkViewModel(
        deeplinkRepo: DeeplinkRepo,
        stringProvider: StringProvider,
        eventStatusHelper: EventStatusHelper,
        zipRequestsUtil: com.sohohouse.seven.common.refresh.ZipRequestsUtil,
    ): DeeplinkViewModel {
        return DeeplinkViewModelImpl(
            deeplinkRepo,
            stringProvider,
            eventStatusHelper,
            zipRequestsUtil
        )
    }

    @Provides
    fun provideHouseNotesRepo(requestFactory: SitecoreRequestFactory): HouseNotesRepo {
        return HouseNotesRepoImpl(requestFactory)
    }

    @Provides
    fun provideHouseNoteDataSourceFactory(repo: HouseNotesRepo): HouseNotesDataSourceFactory {
        return HouseNotesDataSourceFactory(repo)
    }

    @Provides
    fun providePerksRepo(requestFactory: CoreRequestFactory): BenefitsRepo {
        return BenefitsRepoImpl(requestFactory)
    }

    @Provides
    fun provideErrorViewStateViewModel(): ErrorViewStateViewModel = ErrorViewStateViewModelImpl()

    @Provides
    fun provideNoticeboardDataSourceFactory(
        filterManager: FilterManager,
        repo: NoticeboardRepository,
        itemFactory: NoticeboardItemFactory,
    ): NoticeboardDataSourceFactory {
        return NoticeboardDataSourceFactory(filterManager, repo)
    }

    @Provides
    fun providePerksDataSourceFactory(
        filterManager: RegionFilterManager,
        venueRepo: VenueRepo,
        exploreFactory: ExploreListFactory,
        repo: BenefitsRepo,
        benefitsFilterManager: BenefitsFilterManager,
        featureFlags: FeatureFlags,
    ): BenefitsDataSourceFactory {
        return BenefitsDataSourceFactory(
            filterManager,
            benefitsFilterManager,
            venueRepo,
            exploreFactory,
            repo,
            featureFlags
        )
    }

    @Singleton
    @Provides
    fun provideDeeplinkRepo(): DeeplinkRepo = DeeplinkRepoImpl()

    @Singleton
    @Provides
    fun provideZipRequestsUtil(requestFactory: CoreRequestFactory) = ZipRequestsUtil(requestFactory)

    @Provides
    fun provideEventsRepo(requestFactory: CoreRequestFactory): EventsRepo =
        EventsRepoImpl(requestFactory)

    @Provides
    fun provideHomeInteractor(
        userManager: UserManager,
        venueRepo: VenueRepo,
        eventsRepo: EventsRepo,
        benefitsRepo: BenefitsRepo,
        profileRepo: ProfileRepository,
        houseNotesRepo: HouseNotesRepo,
        happeningNowListFactory: HappeningNowListFactory,
        prefsManager: PrefsManager,
        trafficLightsRepo: TrafficLightsRepo,
        venueAttendanceProvider: VenueAttendanceProvider,
        housePayBannerDelegate: HousePayBannerDelegate,
        ioDispatcher: CoroutineDispatcher,
        apiService: SohoApiService
    ): HomeInteractor = HomeInteractorImpl(
        userManager = userManager,
        venueRepo = venueRepo,
        eventsRepo = eventsRepo,
        benefitsRepo = benefitsRepo,
        profileRepo = profileRepo,
        houseNotesRepo = houseNotesRepo,
        happeningNowFactory = happeningNowListFactory,
        trafficLightsRepo = trafficLightsRepo,
        prefsManager = prefsManager,
        venueAttendanceProvider = venueAttendanceProvider,
        housePayBannerDelegate = housePayBannerDelegate,
        ioDispatcher = ioDispatcher,
        apiService = apiService
    )

    @Singleton
    @Provides
    fun provideBuildConfigManager(): BuildConfigManager {
        return App.buildConfigManager
    }

    @Singleton
    @Provides
    fun provideGuestListRepository(zipRequestsUtil: ZipRequestsUtil): GuestListRepository {
        return GuestListRepositoryImpl(zipRequestsUtil)
    }

    @Singleton
    @Provides
    fun provideIdlingResource(): CountingIdlingResource {
        return CountingIdlingResource("CountingIdlingResource", /* debugCounting */ true)
    }

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Provides
    fun provideThemeManager(userManager: UserManager): ThemeManager = ThemeManager(userManager)

    @Singleton
    @Provides
    fun provideNoticeboardFilterManager(): FilterManager = FilterManagerImpl()

    @Singleton
    @Provides
    fun provideRemoteConfigManager(): RemoteConfigManager = RemoteConfigManager()

    @Singleton
    @Provides
    fun provideUXCamVendor(): UXCamVendor = UXCamVendor()

    @Singleton
    @Provides
    fun provideHouseFilterRepository(
        venueRepo: VenueRepo,
        userManager: UserManager,
        accountInteractor: AccountInteractor,
    ): HouseFilterRepository {
        return HouseFilterRepositoryImpl(venueRepo, userManager, accountInteractor)
    }

    @Singleton
    @Provides
    fun provideCityFilterRepository(venueRepo: VenueRepo): CityFilterRepository {
        return CityFilterRepositoryImpl(venueRepo)
    }

    @Singleton
    @Provides
    fun provideTopicFilterRepository(
        stringProvider: StringProvider,
        userManager: UserManager,
    ): TopicFilterRepository {
        return TopicFilterRepositoryImpl(stringProvider, userManager)
    }

    @Singleton
    @Provides
    fun industryFilterRepository(stringProvider: StringProvider): IndustryFilterRepository {
        return IndustryFilterRepositoryImp(stringProvider)
    }

    @Singleton
    @Provides
    fun provideHouseRepository(zipRequestsUtil: ZipRequestsUtil): HouseRepository =
        HouseRepositoryImpl(zipRequestsUtil)

    @Singleton
    @Provides
    fun provideEventCategoryRepository(zipRequestsUtil: ZipRequestsUtil): EventCategoryRepository =
        EventCategoryRepositoryImpl(zipRequestsUtil)

    @Singleton
    @Provides
    fun provideNotificationsViewModel(notificationsRepo: NotificationsRepo): NotificationViewModel {
        return NotificationViewModelImpl(notificationsRepo)
    }

    @Singleton
    @Provides
    fun provideConnectionRepository(coreRequestFactory: CoreRequestFactory): ConnectionRepository {
        return ConnectionRepositoryImpl(coreRequestFactory)
    }

    @Singleton
    @Provides
    fun providesTrafficLightsRepo(
        zipRequestsUtil: ZipRequestsUtil,
        userManager: UserManager,
        profileToMutualConnectionStatus: ProfileToMutualConnectionStatus,
    ): TrafficLightsRepo =
        TrafficLightsRepoImpl(zipRequestsUtil, userManager, profileToMutualConnectionStatus)

    @Singleton
    @Provides
    fun providesProfileToMutualConnectionStatus(userManager: UserManager): ProfileToMutualConnectionStatus {
        return ProfileToMutualConnectionStatus.Impl(userManager = userManager)
    }

    @Provides
    fun providesVenueCache(
        context: Context,
        @Named(CoreServiceModule.NAMED_CORE_MOSHI) moshi: Moshi,
        fileCreator: FileCreator,
    ): VenueCache {
        return VenueCache.Impl(
            fileName = "venues",
            assetsFileName = "venues.json",
            cacheDir = context.cacheDir,
            moshi = moshi,
            fileCreator = fileCreator,
            assets = context.assets
        )
    }

    @Singleton
    @Provides
    fun providesVenueRepo(
        zipRequestsUtil: com.sohohouse.seven.common.refresh.ZipRequestsUtil,
        venueCache: VenueCache,
        userManager: UserManager,
        ioDispatcher: CoroutineDispatcher,
    ): VenueRepo = VenueRepoImpl(
        zipRequestsUtil = zipRequestsUtil,
        venueCache = venueCache,
        userManager = userManager,
        ioDispatcher = ioDispatcher
    )

    @Singleton
    @Provides
    fun provideLocalVenueProvider(
        venueRepo: VenueRepo,
        venueAttendanceProvider: VenueAttendanceProvider,
        userManager: UserManager,
    ): LocalVenueProvider {
        return LocalVenueProvider.Impl(venueRepo, venueAttendanceProvider, userManager)
    }

    @Singleton
    @Provides
    fun provideVenueAttendanceProvider(
        venueRepo: VenueRepo,
        accountInteractor: AccountInteractor,
    ): VenueAttendanceProvider {
        return VenueAttendanceProvider.Impl(venueRepo, accountInteractor)
    }

    @Provides
    @Singleton
    fun providesWorkManager(context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun providesWorkManagerConfig(workerFactory: SohoWorkerFactory): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    @Provides
    @Singleton
    fun providesDaggerWorkerFactory(venueRepo: VenueRepo, context: Context) = SohoWorkerFactory(
        venueRepo = venueRepo,
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    )

    @Provides
    @Singleton
    fun providesNetworkErrorReporter(): BaseApiService.NetworkErrorReporter = ErrorReporter

    @Provides
    @Singleton
    fun provideCacheDataSourceFactory(context: Context): CacheDataSource.Factory {
        val maxCacheSize = 200L * 1024 * 1024
        val evictor = LeastRecentlyUsedCacheEvictor(maxCacheSize)
        val databaseProvider: DatabaseProvider = ExoDatabaseProvider(context)
        val cacheFile = context.applicationContext.cacheDir
        val simpleCache = SimpleCache(cacheFile, evictor, databaseProvider)

        val defaultDataSourceFactory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, "SohoHouse")
        )

        return CacheDataSource.Factory()
            .apply {
                setCache(simpleCache)
                setUpstreamDataSourceFactory(defaultDataSourceFactory)
            }
    }

    @Provides
    @Singleton
    fun providesPaymentConfirmationRepo(
        sohoApiService: SohoApiService
    ): PaymentConfirmationRepo = PaymentConfirmationRepo.Impl(sohoApiService)

    @Provides
    @Singleton
    fun provideHousePayBannerDelegate(
        checkRepo: CheckRepo,
        featureFlags: FeatureFlags,
        userManager: UserManager,
        stringProvider: StringProvider,
        prefsManager: PrefsManager
    ): HousePayBannerDelegateImpl {
        return HousePayBannerDelegateImpl(
            checkRepo, featureFlags, userManager, stringProvider, prefsManager
        )
    }

    @Provides
    @Singleton
    fun provideCheckRepo(
        sohoApiService: SohoApiService
    ): CheckRepo {
        return CheckRepoImpl(
            sohoApiService
        )
    }

    @Provides
    @Singleton
    fun provideGetReceiptsUseCase(
        checkRepo: CheckRepo,
    ): GetReceiptsUseCase {
        return GetReceiptsUseCase(checkRepo)
    }

    @Provides
    @Singleton
    fun provideCardRepo(
        sohoApiService: SohoApiService
    ): CardRepo {
        return CardRepo.Impl(
            sohoApiService
        )
    }

    @Provides
    @Singleton
    fun provideIoScope() = CoroutineScope(Dispatchers.IO)

    @Provides
    @Singleton
    fun provideHouseCreditRepo(apiService: SohoApiService): HouseCreditRepo {
        return HouseCreditRepoImpl(apiService)
    }

    @Provides
    @Singleton
    fun bindCheckPaymentMethodManager(
        cardRepo: CardRepo,
        featureFlags: FeatureFlags
    ): CheckPaymentMethodManager = CheckPaymentMethodManagerImpl(
        cardRepo, featureFlags
    )
}