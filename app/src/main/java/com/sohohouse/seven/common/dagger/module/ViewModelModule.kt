package com.sohohouse.seven.common.dagger.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sohohouse.seven.App
import com.sohohouse.seven.accountstatus.AccountStatusViewModel
import com.sohohouse.seven.apponboarding.AppOnboardingViewModel
import com.sohohouse.seven.apponboarding.data.OnboardingDataViewModel
import com.sohohouse.seven.apponboarding.optinrecommendations.LandingOptInRecommendationsViewModel
import com.sohohouse.seven.apponboarding.terms.OnboardingTermsViewModel
import com.sohohouse.seven.authentication.VerifyAccountViewModel
import com.sohohouse.seven.authentication.signin.SignInViewModel
import com.sohohouse.seven.base.filter.types.categories.FilterCategoriesViewModel
import com.sohohouse.seven.base.filter.types.date.FilterDateViewModel
import com.sohohouse.seven.base.filter.types.location.FilterLocationViewModel
import com.sohohouse.seven.book.BookViewModel
import com.sohohouse.seven.book.cinema.CinemaViewModel
import com.sohohouse.seven.book.digital.DigitalEventsViewModel
import com.sohohouse.seven.book.eventdetails.payment.psd2.Psd2PaymentConfirmationViewModel
import com.sohohouse.seven.book.events.EventsViewModel
import com.sohohouse.seven.book.filter.BookFilterViewModel
import com.sohohouse.seven.book.fitness.FitnessViewModel
import com.sohohouse.seven.book.table.BookATableViewModel
import com.sohohouse.seven.book.table.completebooking.TableCompleteBookingViewModel
import com.sohohouse.seven.book.table.tableconfirmed.TableConfirmedViewModel
import com.sohohouse.seven.book.table.timeslots.TableTimeSlotsViewModel
import com.sohohouse.seven.book.table.update.UpdateTableBookingViewModel
import com.sohohouse.seven.branding.AppIconViewModel
import com.sohohouse.seven.common.dagger.SHViewModelFactory
import com.sohohouse.seven.common.views.webview.SohoWebViewModel
import com.sohohouse.seven.connect.ConnectTabViewModel
import com.sohohouse.seven.connect.filter.city.CityFilterViewModel
import com.sohohouse.seven.connect.filter.house.HouseFilterViewModel
import com.sohohouse.seven.connect.filter.industry.IndustryFilterViewModel
import com.sohohouse.seven.connect.filter.topic.TopicFilterViewModel
import com.sohohouse.seven.connect.match.RecommendationListFilterViewModel
import com.sohohouse.seven.connect.match.RecommendationListViewModel
import com.sohohouse.seven.connect.match.RecommendationsOptInViewModel
import com.sohohouse.seven.connect.message.chat.ChatViewModel
import com.sohohouse.seven.connect.message.chat.content.menu.accept.AcceptRequestBottomSheetViewModel
import com.sohohouse.seven.connect.message.list.MessagesListViewModel
import com.sohohouse.seven.connect.mynetwork.MyConnectionsViewModel
import com.sohohouse.seven.connect.mynetwork.blockedprofiles.BlockedProfileBottomSheetViewModel
import com.sohohouse.seven.connect.mynetwork.blockedprofiles.BlockedProfilesViewModel
import com.sohohouse.seven.connect.mynetwork.connections.ConnectionsViewModel
import com.sohohouse.seven.connect.mynetwork.requests.ConnectionRequestsViewModel
import com.sohohouse.seven.connect.noticeboard.NoticeboardFilterFragmentViewModel
import com.sohohouse.seven.connect.noticeboard.create_post.NoticeboardCreatePostViewModel
import com.sohohouse.seven.connect.noticeboard.post_details.NoticeboardPostDetailsViewModel
import com.sohohouse.seven.connect.noticeboard.user_reactions.UserReactionsViewModel
import com.sohohouse.seven.connect.trafficlights.firstvisit.TrafficLightFirstVisitViewModel
import com.sohohouse.seven.connect.trafficlights.leave.LeaveVenueViewModel
import com.sohohouse.seven.connect.trafficlights.members.MembersInTheVenueViewModel
import com.sohohouse.seven.connect.trafficlights.update.UpdateAvailabilityStatusViewModel
import com.sohohouse.seven.discover.DiscoverViewModel
import com.sohohouse.seven.discover.benefits.BenefitsViewModel
import com.sohohouse.seven.discover.housenotes.HouseNotesViewModel
import com.sohohouse.seven.discover.houses.HousesViewModel
import com.sohohouse.seven.guests.GuestListDetailsViewModel
import com.sohohouse.seven.guests.LocationPickerViewModel
import com.sohohouse.seven.guests.NewGuestListViewModel
import com.sohohouse.seven.guests.list.GuestListIndexViewModel
import com.sohohouse.seven.home.HomeViewModel
import com.sohohouse.seven.home.houseboard.viewmodels.HouseBoardViewModel
import com.sohohouse.seven.houseboard.post.HouseBoardPostViewModel
import com.sohohouse.seven.housenotes.detail.sitecore.HouseNoteDetailsViewModel
import com.sohohouse.seven.housepay.home.HousePayReceiptsViewModel
import com.sohohouse.seven.housepay.terms.HousePayTermsViewModel
import com.sohohouse.seven.housevisit.HouseVisitViewModel
import com.sohohouse.seven.intro.IntroViewModel
import com.sohohouse.seven.intro.profile.PrepopulateProfileViewModel
import com.sohohouse.seven.main.MainViewModel
import com.sohohouse.seven.membership.MembershipInfoViewModel
import com.sohohouse.seven.more.AccountViewModel
import com.sohohouse.seven.more.SettingsActivityViewModel
import com.sohohouse.seven.more.bookings.MyBookingsViewModel
import com.sohohouse.seven.more.bookings.PastBookingDetailViewModel
import com.sohohouse.seven.more.bookings.PastBookingsViewModel
import com.sohohouse.seven.more.bookings.UpcomingBookingsViewModel
import com.sohohouse.seven.more.membershipdetails.MoreMembershipDetailsViewModel
import com.sohohouse.seven.more.notifications.MoreNotificationsViewModel
import com.sohohouse.seven.more.privacy.PrivacySettingsViewModel
import com.sohohouse.seven.more.profile.crop.preview.CropPreviewFragmentViewModel
import com.sohohouse.seven.onboarding.benefits.OnboardingBenefitsViewModel
import com.sohohouse.seven.perks.details.MembershipCardViewModel
import com.sohohouse.seven.perks.details.PerksDetailViewModel
import com.sohohouse.seven.perks.filter.BenefitsFilterCityViewModel
import com.sohohouse.seven.profile.edit.EditCityViewModel
import com.sohohouse.seven.profile.edit.EditOccupationViewModel
import com.sohohouse.seven.profile.edit.EditProfileViewModel
import com.sohohouse.seven.profile.edit.interests.EditInterestsViewModel
import com.sohohouse.seven.profile.share.ShareProfileViewModel
import com.sohohouse.seven.splash.SplashViewModel
import com.sohohouse.seven.welcome.WelcomeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule constructor(val app: App) {

    @Binds
    abstract fun bindViewModelFactory(factory: SHViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSplashViewModel(viewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditProfileViewModel::class)
    abstract fun bindEditProfileViewModel(viewModel: EditProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditOccupationViewModel::class)
    abstract fun bindEditOccupationViewModel(viewModel: EditOccupationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditCityViewModel::class)
    abstract fun bindEditCityViewModel(viewModel: EditCityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditInterestsViewModel::class)
    abstract fun bindEditInterestsViewModel(viewModel: EditInterestsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MembershipInfoViewModel::class)
    abstract fun bindMembershipInfoViewModel(viewModel: MembershipInfoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PastBookingsViewModel::class)
    abstract fun bindPastBookingsViewModel(viewModel: PastBookingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UpcomingBookingsViewModel::class)
    abstract fun bindUpcomingBookingsViewModel(viewModel: UpcomingBookingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyBookingsViewModel::class)
    abstract fun bindMyBookingsViewModel(viewModel: MyBookingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PastBookingDetailViewModel::class)
    abstract fun bindPastBookingDetailViewModel(viewModel: PastBookingDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HouseBoardViewModel::class)
    abstract fun bindHouseBoardViewModel(viewModel: HouseBoardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HouseNoteDetailsViewModel::class)
    abstract fun bindHouseNoteDetailsViewModel(viewModel: HouseNoteDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HouseNotesViewModel::class)
    abstract fun bindHouseNotesViewModel(viewModel: HouseNotesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SohoWebViewModel::class)
    abstract fun bindSohoWebViewModel(viewModel: SohoWebViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WelcomeViewModel::class)
    abstract fun bindWelcomeViewModel(viewModel: WelcomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DiscoverViewModel::class)
    abstract fun bindDiscoverViewModel(viewModel: DiscoverViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BenefitsViewModel::class)
    abstract fun bindPerksViewModel(viewModel: BenefitsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HousesViewModel::class)
    abstract fun bindHousesViewModel(viewModel: HousesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VerifyAccountViewModel::class)
    abstract fun bindVerifyAccountViewModel(viewModel: VerifyAccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GuestListIndexViewModel::class)
    abstract fun bindGuestInvitationsViewModel(viewModel: GuestListIndexViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewGuestListViewModel::class)
    abstract fun bindCreateGuestInvitationsViewModel(viewModel: NewGuestListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(viewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LocationPickerViewModel::class)
    abstract fun bindLocationPickerViewModel(viewModel: LocationPickerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GuestListDetailsViewModel::class)
    abstract fun bindGuestListDetailsViewModel(viewModel: GuestListDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BenefitsFilterCityViewModel::class)
    abstract fun bindVerifyBenefitsFilterViewModel(viewModel: BenefitsFilterCityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MoreNotificationsViewModel::class)
    abstract fun bindMoreNotificationsViewModel(viewModel: MoreNotificationsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AppIconViewModel::class)
    abstract fun bindAppIconViewModel(viewModel: AppIconViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PerksDetailViewModel::class)
    abstract fun bindPerksDetailViewModel(viewModel: PerksDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MembershipCardViewModel::class)
    abstract fun bindMembershipCardViewModel(viewModel: MembershipCardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OnboardingDataViewModel::class)
    abstract fun bindOnboardingDataViewModel(viewModel: OnboardingDataViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OnboardingTermsViewModel::class)
    abstract fun bindOnboardingTermsViewModel(viewModel: OnboardingTermsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IntroViewModel::class)
    abstract fun bindIntroViewModel(viewModel: IntroViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OnboardingBenefitsViewModel::class)
    abstract fun bindVerifyBenefitsViewModel(viewModel: OnboardingBenefitsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MoreMembershipDetailsViewModel::class)
    abstract fun bindMoreMembershipDetailsViewModel(viewModel: MoreMembershipDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BookATableViewModel::class)
    abstract fun bindTableBookingViewModel(viewModel: BookATableViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TableTimeSlotsViewModel::class)
    abstract fun bindTableBookingDetailsViewModel(viewModel: TableTimeSlotsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TableCompleteBookingViewModel::class)
    abstract fun bindTableConfirmationViewModel(viewModel: TableCompleteBookingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TableConfirmedViewModel::class)
    abstract fun bindTableSummaryViewModel(viewModel: TableConfirmedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UpdateTableBookingViewModel::class)
    abstract fun bindUpdateTableBookingViewModel(viewModel: UpdateTableBookingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HouseBoardPostViewModel::class)
    abstract fun bindHouseBoardPostViewModel(viewModel: HouseBoardPostViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AccountStatusViewModel::class)
    abstract fun bindAccountStatusViewModel(viewModel: AccountStatusViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignInViewModel::class)
    abstract fun bindSignInViewModel(viewModel: SignInViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BookViewModel::class)
    abstract fun bindBookViewModel(viewModel: BookViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EventsViewModel::class)
    abstract fun bindEventsViewModel(viewModel: EventsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CinemaViewModel::class)
    abstract fun bindCinemaViewModel(viewModel: CinemaViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FitnessViewModel::class)
    abstract fun bindFitnessViewModel(viewModel: FitnessViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HouseVisitViewModel::class)
    abstract fun bindHouseVisitViewModel(viewModel: HouseVisitViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NoticeboardPostDetailsViewModel::class)
    abstract fun bindPostDetailsViewModel(viewModel: NoticeboardPostDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserReactionsViewModel::class)
    abstract fun userReactionsViewModel(viewModel: UserReactionsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HouseFilterViewModel::class)
    abstract fun bindHouseFilterViewModel(viewModel: HouseFilterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CityFilterViewModel::class)
    abstract fun bindCityFilterViewModel(viewModel: CityFilterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TopicFilterViewModel::class)
    abstract fun bindCityTopicFilterViewModel(viewModel: TopicFilterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IndustryFilterViewModel::class)
    abstract fun bindIndustryFilterViewModel(viewModel: IndustryFilterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NoticeboardCreatePostViewModel::class)
    abstract fun bindCreatePostViewModel(viewModel: NoticeboardCreatePostViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RecommendationListFilterViewModel::class)
    abstract fun bindNoticeboardFilterViewModel(bottomSheetViewModel: RecommendationListFilterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NoticeboardFilterFragmentViewModel::class)
    abstract fun bindNoticeboardFilterFragmentViewModel(bottomSheetViewModel: NoticeboardFilterFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PrepopulateProfileViewModel::class)
    abstract fun bindPrepopulateProfileViewModel(viewmodel: PrepopulateProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DigitalEventsViewModel::class)
    abstract fun bindDigitalEventsViewModel(viewModel: DigitalEventsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TrafficLightFirstVisitViewModel::class)
    abstract fun bindTrafficLightFirstVisitViewModel(viewModel: TrafficLightFirstVisitViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MembersInTheVenueViewModel::class)
    abstract fun bindMembersInTheVenueViewModel(viewModel: MembersInTheVenueViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UpdateAvailabilityStatusViewModel::class)
    abstract fun bindUpdateAvailabilityStatusViewModel(viewModel: UpdateAvailabilityStatusViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LeaveVenueViewModel::class)
    abstract fun bindLeaveVenueViewModel(viewModel: LeaveVenueViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(ConnectionsViewModel::class)
    abstract fun bindConnectionsViewModel(viewModel: ConnectionsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BlockedProfilesViewModel::class)
    abstract fun bindBlockedProfilesViewModel(viewModel: BlockedProfilesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ShareProfileViewModel::class)
    abstract fun bindShareProfileViewModel(viewModel: ShareProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BlockedProfileBottomSheetViewModel::class)
    abstract fun bindBlockedProfileBottomSheetViewModel(viewModel: BlockedProfileBottomSheetViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConnectionRequestsViewModel::class)
    abstract fun bindConnectionRequestsViewModel(viewModel: ConnectionRequestsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyConnectionsViewModel::class)
    abstract fun bindMyConnectionsViewModel(viewModel: MyConnectionsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MessagesListViewModel::class)
    abstract fun bindMessagesListViewModel(viewModel: MessagesListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChatViewModel::class)
    abstract fun bindChatViewModel(viewModel: ChatViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConnectTabViewModel::class)
    abstract fun bindConnectTabViewModel(viewModel: ConnectTabViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AcceptRequestBottomSheetViewModel::class)
    abstract fun bindAcceptRequestBottomSheetViewModel(viewModel: AcceptRequestBottomSheetViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LandingOptInRecommendationsViewModel::class)
    abstract fun bindLandingOptInRecommendationsViewModel(viewModel: LandingOptInRecommendationsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RecommendationsOptInViewModel::class)
    abstract fun bindDecideOptInBottomSheetViewModel(viewModel: RecommendationsOptInViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PrivacySettingsViewModel::class)
    abstract fun bindPrivacySettingsViewModel(viewModel: PrivacySettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RecommendationListViewModel::class)
    abstract fun bindAllRecommendationViewModel(viewModel: RecommendationListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsActivityViewModel::class)
    abstract fun bindSettingsActivityViewModel(viewModel: SettingsActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CropPreviewFragmentViewModel::class)
    abstract fun bindCropPreviewFragmentViewModel(cropPreviewFragmentViewModel: CropPreviewFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AppOnboardingViewModel::class)
    abstract fun bindAppOnboardingViewModel(viewModel: AppOnboardingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HousePayReceiptsViewModel::class)
    abstract fun bindHousePayReceiptsViewModel(viewModel: HousePayReceiptsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BookFilterViewModel::class)
    abstract fun bindBookFilterViewModel(viewModel: BookFilterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FilterDateViewModel::class)
    abstract fun bindFilterDateViewModel(viewModel: FilterDateViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FilterLocationViewModel::class)
    abstract fun bindFilterLocationViewModel(viewModel: FilterLocationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FilterCategoriesViewModel::class)
    abstract fun bindFilterCategoriesViewModel(viewModel: FilterCategoriesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HousePayTermsViewModel::class)
    abstract fun bindHousePayTermsViewModel(viewModel: HousePayTermsViewModel): ViewModel

}
