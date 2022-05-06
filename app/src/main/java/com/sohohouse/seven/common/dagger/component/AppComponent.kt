package com.sohohouse.seven.common.dagger.component

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.idling.CountingIdlingResource
import com.sohohouse.sendbird.SendBirdModule
import com.sohohouse.seven.App
import com.sohohouse.seven.accountstatus.AccountStatusFlowManager
import com.sohohouse.seven.apponboarding.AppOnboardingViewModel
import com.sohohouse.seven.apponboarding.housepreferences.OnboardingHousePreferencesPresenter
import com.sohohouse.seven.authentication.AuthenticationFlowManager
import com.sohohouse.seven.base.BaseActivity
import com.sohohouse.seven.book.eventdetails.EventDetailsPresenter
import com.sohohouse.seven.book.eventdetails.payment.BookingPaymentPresenter
import com.sohohouse.seven.book.eventdetails.payment.PaymentConfirmationPresenter
import com.sohohouse.seven.book.filter.BookFilterManager
import com.sohohouse.seven.book.filter.BookFilterPresenter
import com.sohohouse.seven.browsehouses.BrowseAllHousePresenter
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.dagger.module.*
import com.sohohouse.seven.common.error.ErrorInteractor
import com.sohohouse.seven.common.events.ExploreCategoryManager
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.interactors.CategoryInteractor
import com.sohohouse.seven.common.refresh.AuthInteractor
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.user.AppManager
import com.sohohouse.seven.common.user.UserSessionManager
import com.sohohouse.seven.common.utils.LogoutUtil
import com.sohohouse.seven.common.utils.encryption.PublicKeyEncryptable
import com.sohohouse.seven.common.utils.imageloader.ImageLoader
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.ExploreListFactory
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.fcm.FirebaseRegistrationService
import com.sohohouse.seven.housenotes.detail.HouseNoteDetailPresenter
import com.sohohouse.seven.housenotes.viewholders.HouseNotesCarouselAdapter
import com.sohohouse.seven.memberonboarding.induction.booking.InductionBookingPresenter
import com.sohohouse.seven.memberonboarding.induction.confirmation.InductionConfirmationPresenter
import com.sohohouse.seven.more.bookings.detail.EventBookingDetailsPresenter
import com.sohohouse.seven.more.change.password.ChangePasswordPresenter
import com.sohohouse.seven.more.contact.MoreContactPresenter
import com.sohohouse.seven.more.housepreferences.MoreHousePreferencesPresenter
import com.sohohouse.seven.more.payment.AddPaymentPresenter
import com.sohohouse.seven.more.payment.MorePaymentPresenter
import com.sohohouse.seven.more.profile.crop.preview.ProfileImageCropperInteractor
import com.sohohouse.seven.more.synccalendar.SyncCalendarPresenter
import com.sohohouse.seven.network.auth.AuthenticationModule
import com.sohohouse.seven.network.chat.ChatChannelsRepo
import com.sohohouse.seven.network.core.CoreServiceModule
import com.sohohouse.seven.network.di.NetworkModule
import com.sohohouse.seven.network.forceupdate.ForceUpdateModule
import com.sohohouse.seven.perks.filter.PerksFilterPresenter
import com.sohohouse.seven.perks.filter.manager.RegionFilterManager
import com.sohohouse.seven.perks.landing.PerksLandingPresenter
import com.sohohouse.seven.profile.ProfileRepository
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        ActivityModule::class,
        NetworkModule::class,
        RaygunModule::class,
        ServiceModule::class,
        SendBirdModule::class,
        FragmentModule::class,
    ]
)
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: App): Builder

        @BindsInstance
        fun context(context: Context): Builder

        @BindsInstance
        fun authHostName(@Named(AuthenticationModule.NAMED_AUTH_HOST_NAME) authHostName: String): Builder

        @BindsInstance
        fun coreServiceHostName(@Named(CoreServiceModule.NAMED_CORE_SERVICE_HOST_NAME) coreServiceHostName: String): Builder

        @BindsInstance
        fun forceUpdateHostName(@Named(ForceUpdateModule.NAMED_FORCE_UPDATE_HOST_NAME) forceUpdateHostName: String): Builder

        @BindsInstance
        fun sendBirdBaseUrl(@Named(SendBirdModule.SEND_BIRD_BASE_URL) baseUrl: String): Builder

        fun build(): AppComponent
    }

    fun inject(application: App)

    val imageLoader: ImageLoader

    val bookFilterManager: BookFilterManager

    val userSessionManager: UserSessionManager

    var regionFilterManager: RegionFilterManager

    val userManager: UserManager

    val appManager: AppManager

    val venueRepo: VenueRepo

    val chatChannelsRepo: ChatChannelsRepo

    val houseManager: HouseManager

    val exploreCategoryManager: ExploreCategoryManager

    val publicKeyEncryptor: PublicKeyEncryptable

    val exploreFactory: ExploreListFactory

    fun generateErrorInteractor(): ErrorInteractor {
        return ErrorInteractor()
    }

    fun inject(application: HouseNotesCarouselAdapter.HouseNoteCarouselItemViewHolder)
    fun inject(baseActivity: BaseActivity)
    fun inject(reloadableErrorStateView: ReloadableErrorStateView)

    val analyticsManager: AnalyticsManager

    val zipRequestsUtil: ZipRequestsUtil

    val accountInteractor: AccountInteractor

    val profileImageCropperInteractor: ProfileImageCropperInteractor

    val categoryInteractor: CategoryInteractor

    val appOnboardingPresenter: AppOnboardingViewModel

    val perksPresenter: PerksLandingPresenter

    val perksFilterPresenter: PerksFilterPresenter

    val onboardingHousePreferencesPresenter: OnboardingHousePreferencesPresenter

    val eventDetailsPresenter: EventDetailsPresenter

    val paymentConfirmationPresenter: PaymentConfirmationPresenter

    val houseNoteDetailPresenter: HouseNoteDetailPresenter

    val inductionBookingPresenter: InductionBookingPresenter

    val addPaymentPresenter: AddPaymentPresenter

    val bookFilterPresenter: BookFilterPresenter

    val bookingPaymentPresenter: BookingPaymentPresenter

    val browseAllHousesPresenter: BrowseAllHousePresenter

    val inductionConfirmationPresenter: InductionConfirmationPresenter

    val moreContactPresenter: MoreContactPresenter

    val morePaymentPresenter: MorePaymentPresenter

    val moreHousePreferencesPresenter: MoreHousePreferencesPresenter

    val eventBookingDetailsPresenter: EventBookingDetailsPresenter

    val changePasswordPresenter: ChangePasswordPresenter

    val syncCalendarPresenter: SyncCalendarPresenter

    val logoutUtil: LogoutUtil

    //TODO remove when all activities support injection
    val profileRepo: ProfileRepository

    //TODO remove when all activities support injection
    val accountStatusFlowManager: AccountStatusFlowManager

    //TODO remove when all activities support injection
    val authenticationFlowManager: AuthenticationFlowManager

    @VisibleForTesting
    val idlingResource: CountingIdlingResource

    val firebaseRegistrationService: FirebaseRegistrationService

    val authInteractor: AuthInteractor

}