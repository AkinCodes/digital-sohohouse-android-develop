package com.sohohouse.seven.common.dagger.module

import com.sohohouse.seven.accountstatus.AccountStatusActivity
import com.sohohouse.seven.apponboarding.AppOnboardingActivity
import com.sohohouse.seven.apponboarding.data.OnboardingDataActivity
import com.sohohouse.seven.apponboarding.optinrecommendations.LandingOptInRecommendationsActivity
import com.sohohouse.seven.apponboarding.terms.OnboardingTermsActivity
import com.sohohouse.seven.authentication.AuthenticationActivity
import com.sohohouse.seven.authentication.VerificationEmailSentActivity
import com.sohohouse.seven.authentication.VerifyAccountActivity
import com.sohohouse.seven.book.digital.DigitalEventsActivity
import com.sohohouse.seven.book.eventdetails.EventDetailsActivity
import com.sohohouse.seven.book.filter.BookFilterActivity
import com.sohohouse.seven.book.eventdetails.payment.psd2.Psd2PaymentConfirmationActivity
import com.sohohouse.seven.book.table.booked.BookedTableDetailsActivity
import com.sohohouse.seven.book.table.completebooking.TableCompleteBookingActivity
import com.sohohouse.seven.book.table.tableconfirmed.TableConfirmedActivity
import com.sohohouse.seven.book.table.timeslots.TableTimeSlotsActivity
import com.sohohouse.seven.book.table.update.UpdateTableBookingActivity
import com.sohohouse.seven.branding.AppIconActivity
import com.sohohouse.seven.connect.match.RecommendationListActivity
import com.sohohouse.seven.connect.message.chat.ChatActivity
import com.sohohouse.seven.connect.mynetwork.MyConnectionsActivity
import com.sohohouse.seven.connect.mynetwork.blockedprofiles.BlockedProfilesActivity
import com.sohohouse.seven.connect.noticeboard.post_details.NoticeboardPostDetailsActivity
import com.sohohouse.seven.connect.trafficlights.members.MembersInTheVenueActivity
import com.sohohouse.seven.guests.GuestListDetailsActivity
import com.sohohouse.seven.guests.NewGuestListActivity
import com.sohohouse.seven.guests.list.GuestListIndexActivity
import com.sohohouse.seven.houseboard.post.HouseBoardPostActivity
import com.sohohouse.seven.housenotes.detail.sitecore.HouseNoteDetailsActivity
import com.sohohouse.seven.housepay.HousepayActivity
import com.sohohouse.seven.housepay.terms.HousePayTermActivity
import com.sohohouse.seven.intro.IntroActivity
import com.sohohouse.seven.intro.profile.PrepopulateProfileActivity
import com.sohohouse.seven.main.MainActivity
import com.sohohouse.seven.membership.ActiveMembershipInfoActivity
import com.sohohouse.seven.more.SettingsActivity
import com.sohohouse.seven.more.bookings.MyBookingsActivity
import com.sohohouse.seven.more.bookings.PastEventBookingDetailActivity
import com.sohohouse.seven.more.contact.MoreContactActivity
import com.sohohouse.seven.more.housepreferences.MoreHousePreferencesActivity
import com.sohohouse.seven.more.membershipdetails.MoreMembershipDetailsActivity
import com.sohohouse.seven.more.payment.MorePaymentActivity
import com.sohohouse.seven.more.profile.crop.ProfileImageCropActivity
import com.sohohouse.seven.onboarding.benefits.OnboardingBenefitsActivity
import com.sohohouse.seven.perks.details.PerksDetailActivity
import com.sohohouse.seven.perks.filter.BenefitsFilterCityActivity
import com.sohohouse.seven.perks.landing.PerksLandingActivity
import com.sohohouse.seven.profile.edit.EditProfileActivity
import com.sohohouse.seven.splash.SplashActivity
import com.sohohouse.seven.welcome.WelcomeActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributesSplashActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributesMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributesEditProfileActivity(): EditProfileActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributesActiveMembershipInfoActivity(): ActiveMembershipInfoActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributesMyBookingsActivity(): MyBookingsActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributesHousePayLandingActivity(): HousepayActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributesPastEventBookingDetailActivity(): PastEventBookingDetailActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributesHouseNoteDetailsActivity(): HouseNoteDetailsActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributesWelcomeActivity(): WelcomeActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeVerifyAccountActivity(): VerifyAccountActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeVerificationEmailSentAccountActivity(): VerificationEmailSentActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeGuestInvitationsActivity(): GuestListIndexActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeCreateInviteActivity(): NewGuestListActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributesGuestListDetailsActivity(): GuestListDetailsActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeSettingsActivity(): SettingsActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeAppIconActivity(): AppIconActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeBenefitsFilterCityActivity(): BenefitsFilterCityActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributePerksDetailActivity(): PerksDetailActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeOnboardingDataActivity(): OnboardingDataActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeOnboardingTermsActivity(): OnboardingTermsActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeIntroActivity(): IntroActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeMoreMembershipDetailsActivity(): MoreMembershipDetailsActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeMoreHousePreferencesActivity(): MoreHousePreferencesActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeMoreBenefitsActivity(): OnboardingBenefitsActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeMoreMorePaymentActivity(): MorePaymentActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeMoreMoreContactActivity(): MoreContactActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributePerksLandingActivity(): PerksLandingActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeAuthenticationActivity(): AuthenticationActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeProfileImageCropActivity(): ProfileImageCropActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeHouseBoardPostActivity(): HouseBoardPostActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeAccountStatusActivity(): AccountStatusActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeTableBookingDetailsActivity(): TableTimeSlotsActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeBookedTableDetails(): BookedTableDetailsActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeTableConfirmationActivity(): TableCompleteBookingActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeTableSummaryActivity(): TableConfirmedActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeUpdateTableBookingActivity(): UpdateTableBookingActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeEventDetailsActivity(): EventDetailsActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeNoticeboardPostDetailsActivity(): NoticeboardPostDetailsActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributePrepopulateProfileActivity(): PrepopulateProfileActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeDigitalEventsActivity(): DigitalEventsActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeMembersInTheVenueActivity(): MembersInTheVenueActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeMyConnectionsActivity(): MyConnectionsActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeBlockedProfilesActivity(): BlockedProfilesActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeLandingOptInRecommendationsActivity(): LandingOptInRecommendationsActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeAllRecommendationActivity(): RecommendationListActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeChatActivity(): ChatActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributePsd2PaymentConfirmationActivity(): Psd2PaymentConfirmationActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeAppOnboardingActivity(): AppOnboardingActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeBookFilterActivity(): BookFilterActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeHousePayTermActivity(): HousePayTermActivity
}

