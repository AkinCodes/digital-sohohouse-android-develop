package com.sohohouse.seven.common.dagger.module

import com.sohohouse.seven.authentication.signin.SignInFragment
import com.sohohouse.seven.base.filter.types.categories.FilterCategoriesFragment
import com.sohohouse.seven.base.filter.types.date.FilterDateFragment
import com.sohohouse.seven.base.filter.types.location.FilterLocationFragment
import com.sohohouse.seven.book.BookFragment
import com.sohohouse.seven.book.cinema.CinemaFragment
import com.sohohouse.seven.book.digital.DigitalEventsFragment
import com.sohohouse.seven.book.events.EventsFragment
import com.sohohouse.seven.book.fitness.FitnessFragment
import com.sohohouse.seven.book.table.BookATableFragment
import com.sohohouse.seven.branding.AppIconChooserFragment
import com.sohohouse.seven.branding.AppIconFragment
import com.sohohouse.seven.common.dagger.scope.OpenCheckScope
import com.sohohouse.seven.common.views.amountinput.AmountInputFragment
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.connect.ConnectTabFragment
import com.sohohouse.seven.connect.filter.city.CityFilterFragment
import com.sohohouse.seven.connect.filter.house.HouseFilterFragment
import com.sohohouse.seven.connect.filter.industry.IndustryFilterFragment
import com.sohohouse.seven.connect.filter.topic.TopicFilterFragment
import com.sohohouse.seven.connect.match.RecommendationListFilterBottomSheet
import com.sohohouse.seven.connect.match.RecommendationListFragment
import com.sohohouse.seven.connect.match.RecommendationsOptInBottomSheet
import com.sohohouse.seven.connect.message.chat.content.ChatContentFragment
import com.sohohouse.seven.connect.message.chat.content.menu.MenuBottomSheet
import com.sohohouse.seven.connect.message.chat.content.menu.accept.AcceptRequestBottomSheet
import com.sohohouse.seven.connect.message.chat.content.menu.block.BlockUserBottomSheet
import com.sohohouse.seven.connect.message.chat.content.menu.report.ReportUserBottomSheet
import com.sohohouse.seven.connect.message.chat.fullscreen.MediaFullScreenFragment
import com.sohohouse.seven.connect.message.list.MessagesListFragment
import com.sohohouse.seven.connect.mynetwork.blockedprofiles.BlockedProfilesBottomSheet
import com.sohohouse.seven.connect.mynetwork.connections.ConnectionsFragment
import com.sohohouse.seven.connect.mynetwork.requests.ConnectionRequestsFragment
import com.sohohouse.seven.connect.noticeboard.NoticeboardFilterBottomSheet
import com.sohohouse.seven.connect.noticeboard.NoticeboardLandingFragment
import com.sohohouse.seven.connect.noticeboard.create_post.NoticeboardCreatePostFragment
import com.sohohouse.seven.connect.noticeboard.user_reactions.UserReactionsBottomSheet
import com.sohohouse.seven.connect.trafficlights.firstvisit.TrafficLightFirstVisitBottomSheet
import com.sohohouse.seven.connect.trafficlights.leave.LeaveVenueBottomSheet
import com.sohohouse.seven.connect.trafficlights.update.UpdateAvailabilityStatusBottomSheet
import com.sohohouse.seven.discover.DiscoverFragment
import com.sohohouse.seven.discover.benefits.BenefitsFragment
import com.sohohouse.seven.discover.housenotes.HouseNotesFragment
import com.sohohouse.seven.discover.houses.HousesFragment
import com.sohohouse.seven.guests.LocationPickerFragment
import com.sohohouse.seven.home.HomeFragment
import com.sohohouse.seven.home.houseboard.HouseBoardFragment
import com.sohohouse.seven.housepay.FragmentHousepayCheck
import com.sohohouse.seven.housepay.FragmentHousepayOnboarding
import com.sohohouse.seven.housepay.checkdetail.closed.CheckReceiptFragment
import com.sohohouse.seven.housepay.checkdetail.open.AcceptHousePayTermsFragment
import com.sohohouse.seven.housepay.checkdetail.open.OpenCheckFragment
import com.sohohouse.seven.housepay.home.HousePayReceiptsFragment
import com.sohohouse.seven.housepay.payment.ChoosePaymentDialogFragment
import com.sohohouse.seven.housevisit.HouseVisitFragment
import com.sohohouse.seven.intro.profile.PrepopulateProfileFormFragment
import com.sohohouse.seven.more.AccountFragment
import com.sohohouse.seven.more.bookings.MyBookingsFragment
import com.sohohouse.seven.more.bookings.PastBookingsFragment
import com.sohohouse.seven.more.bookings.UpcomingBookingsFragment
import com.sohohouse.seven.more.notifications.NotificationSettingsFragment
import com.sohohouse.seven.more.privacy.PrivacySettingsFragment
import com.sohohouse.seven.more.profile.crop.edit.CropEditFragment
import com.sohohouse.seven.more.profile.crop.preview.CropPreviewFragment
import com.sohohouse.seven.onboarding.benefits.OnboardingBenefitsFragment
import com.sohohouse.seven.payment.housepay.AddHousePayFragment
import com.sohohouse.seven.perks.details.MembershipCardDialogFragment
import com.sohohouse.seven.perks.filter.BenefitsFilterCityFragment
import com.sohohouse.seven.profile.edit.EditCityBottomSheet
import com.sohohouse.seven.profile.edit.EditOccupationBottomSheet
import com.sohohouse.seven.profile.edit.interests.EditInterestsBottomSheet
import com.sohohouse.seven.profile.edit.pronouns.EditPronounsFragment
import com.sohohouse.seven.profile.edit.socialmedia.EditSocialMediaBottomSheet
import com.sohohouse.seven.profile.share.ShareProfileBottomSheet
import com.sohohouse.seven.profile.view.ProfileFieldsFragment
import com.sohohouse.seven.profile.view.ProfileViewerFragment
import com.sohohouse.seven.profile.view.connect.ComposeMessageBottomSheet
import com.sohohouse.seven.profile.view.connect.ConnectRequestBottomSheet
import com.sohohouse.seven.profile.view.more.MoreOptionsBottomSheet
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeMainMenuFragment(): HouseBoardFragment

    @ContributesAndroidInjector
    abstract fun contributeEditOccupationBottomSheet(): EditOccupationBottomSheet

    @ContributesAndroidInjector
    abstract fun contributesPastBookingsFragment(): PastBookingsFragment

    @ContributesAndroidInjector
    abstract fun contributesUpcmoingBookingsFragment(): UpcomingBookingsFragment

    @ContributesAndroidInjector
    abstract fun contributeEditCityBottomSheet(): EditCityBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeEditInterestsBottomSheet(): EditInterestsBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeBookingsFragment(): MyBookingsFragment

    @ContributesAndroidInjector
    abstract fun contributeAddHousePayFragment(): AddHousePayFragment

    @ContributesAndroidInjector
    abstract fun contributeHousePayOnboardingFragment(): FragmentHousepayOnboarding

    @ContributesAndroidInjector
    abstract fun contributeHousePayCheckFragment(): FragmentHousepayCheck

    @ContributesAndroidInjector
    abstract fun contributeDiscoverFragment(): DiscoverFragment

    @ContributesAndroidInjector
    abstract fun contributeHouseNotesFragment(): HouseNotesFragment

    @ContributesAndroidInjector
    abstract fun contributePerksFragment(): BenefitsFragment

    @ContributesAndroidInjector
    abstract fun contributeHousesFragment(): HousesFragment

    @ContributesAndroidInjector
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector
    abstract fun contributeLocationPickerFragment(): LocationPickerFragment

    @ContributesAndroidInjector
    abstract fun contributeBenefitsFilterFragment(): BenefitsFilterCityFragment

    @ContributesAndroidInjector
    abstract fun contributeNotificationSettingsFragment(): NotificationSettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeAppIconChooserFragment(): AppIconChooserFragment

    @ContributesAndroidInjector
    abstract fun contributeAppIconFragment(): AppIconFragment

    @ContributesAndroidInjector
    abstract fun contributeMembershipCardDialogFragment(): MembershipCardDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeBenefitsFragment(): OnboardingBenefitsFragment

    @ContributesAndroidInjector
    abstract fun contributeWebViewBottomSheetFragment(): WebViewBottomSheetFragment

    @ContributesAndroidInjector
    abstract fun contributeTableBookingFragment(): BookATableFragment

    @ContributesAndroidInjector
    abstract fun contributeBookFragment(): BookFragment

    @ContributesAndroidInjector
    abstract fun contributeFitnessFragment(): FitnessFragment

    @ContributesAndroidInjector
    abstract fun contributeSigninFragment(): SignInFragment

    @ContributesAndroidInjector
    abstract fun contributeHouseVisitFragment(): HouseVisitFragment

    @ContributesAndroidInjector
    abstract fun contributeCinemaFragment(): CinemaFragment

    @ContributesAndroidInjector
    abstract fun contributeEventsFragment(): EventsFragment

    @ContributesAndroidInjector
    abstract fun contributeNoticeboardLAndingFragment(): NoticeboardLandingFragment

    @ContributesAndroidInjector
    abstract fun contributeHouseFilterFragment(): HouseFilterFragment

    @ContributesAndroidInjector
    abstract fun contributeCityFilterFragment(): CityFilterFragment

    @ContributesAndroidInjector
    abstract fun contributeTopicFilterFragment(): TopicFilterFragment

    @ContributesAndroidInjector
    abstract fun contributeIndustryFilterFragment(): IndustryFilterFragment

    @ContributesAndroidInjector
    abstract fun contributeCreatePostFragment(): NoticeboardCreatePostFragment

    @ContributesAndroidInjector
    abstract fun contributeRecommendationListFilterBottomSheet(): RecommendationListFilterBottomSheet

    @ContributesAndroidInjector
    abstract fun contributePrepopulateProfileFormFragment(): PrepopulateProfileFormFragment

    @ContributesAndroidInjector
    abstract fun contributeEditSocialMediaBottomSheet(): EditSocialMediaBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeDigitalEventsFragment(): DigitalEventsFragment

    @ContributesAndroidInjector
    abstract fun contributeTrafficLightFirstVisitBottomSheet(): TrafficLightFirstVisitBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeUpdateAvailabilityStatusBottomSheet(): UpdateAvailabilityStatusBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeLeaveVenueBottomSheet(): LeaveVenueBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeProfileDetailsFragment(): ProfileFieldsFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileViewerFragment(): ProfileViewerFragment

    @ContributesAndroidInjector
    abstract fun contributeConnectionRequestBottomSheet(): ConnectRequestBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeComposeMessageBottomSheet(): ComposeMessageBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeConnectionsFragment(): ConnectionsFragment

    @ContributesAndroidInjector
    abstract fun contributeConnectionRequestsFragment(): ConnectionRequestsFragment

    @ContributesAndroidInjector
    abstract fun contributeMoreOptionsBottomSheet(): MoreOptionsBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeEditPronounsFragment(): EditPronounsFragment

    @OpenCheckScope
    @ContributesAndroidInjector(modules = [OpenCheckModule::class])
    abstract fun contributeOpenCheckFragment(): OpenCheckFragment

    @ContributesAndroidInjector
    abstract fun contributeCheckReceiptFragment(): CheckReceiptFragment

    @OpenCheckScope
    @ContributesAndroidInjector(modules = [OpenCheckModule::class])
    abstract fun contributChoosePaymentDialogFragment(): ChoosePaymentDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeAmountInputFragment(): AmountInputFragment

    @ContributesAndroidInjector
    abstract fun contributeMessagesListFragment(): MessagesListFragment

    @ContributesAndroidInjector
    abstract fun contributeChatContentFragment(): ChatContentFragment

    @ContributesAndroidInjector
    abstract fun contributeConnectTabFragment(): ConnectTabFragment

    @ContributesAndroidInjector
    abstract fun contributeBlockUserFragment(): BlockUserBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeMenuBottomSheet(): MenuBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeReportUserBottomSheet(): ReportUserBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeAcceptRequestBottomSheet(): AcceptRequestBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeUserReactionsBottomSheet(): UserReactionsBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeBlockedProfilesBottomSheet(): BlockedProfilesBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeShareProfileBottomSheet(): ShareProfileBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeDecideOptInBottomSheet(): RecommendationsOptInBottomSheet

    @ContributesAndroidInjector
    abstract fun contributePrivacySettingsFragment(): PrivacySettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeAllRecommendationFragment(): RecommendationListFragment

    @ContributesAndroidInjector
    abstract fun contributeNoticeboardFilterFragment(): NoticeboardFilterBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeMediaFullScreenFragment(): MediaFullScreenFragment

    @ContributesAndroidInjector
    abstract fun contributeCropEditFragment(): CropEditFragment

    @ContributesAndroidInjector
    abstract fun contributeCropPreviewFragment(): CropPreviewFragment

    @ContributesAndroidInjector
    abstract fun contributeHousePayReceiptsFragment(): HousePayReceiptsFragment

    @ContributesAndroidInjector
    abstract fun contributeFilterDateFragment(): FilterDateFragment

    @ContributesAndroidInjector
    abstract fun contributeFilterLocationFragment(): FilterLocationFragment

    @ContributesAndroidInjector
    abstract fun contributeFilterCategoriesFragment(): FilterCategoriesFragment

}
