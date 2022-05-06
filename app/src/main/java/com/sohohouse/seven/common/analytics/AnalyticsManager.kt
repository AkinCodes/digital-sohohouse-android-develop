package com.sohohouse.seven.common.analytics

import android.app.Activity
import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.book.table.model.BookedTable
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.print
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.network.core.models.EventBooking
import com.sohohouse.seven.network.core.models.Reaction
import com.sohohouse.seven.network.core.models.RoomBooking
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.profile.SocialMediaItem
import com.sohohouse.seven.profile.SocialMediaItem.Type.*

interface AnalyticsManager {

    fun logEventAction(action: Action, params: Bundle? = null)

    fun setScreenName(activityName: String, screen: String)

    fun setAnalyticsEnabled(enabled: Boolean)

    @Deprecated("Deprecate: Use logEventAction instead")
    fun track(@Suppress("DEPRECATION") eventAnalytics: AnalyticsEvent)

    @Suppress("unused")
    enum class Action(val value: String) {
        /*
            NAIVATION - TABS
         */
        TabSelected("tab_selected"),
        MembershipCardShake("membership_card"),

        /*
            HOME
         */
        HomeHappeningAtSeeAll("home_happening_at_see_all"), //not implemented
        HomeHappeningNowSeeAll("home_happening_now_see_all"),//not implemented
        HomeLiveNowEvent("home_live_now_event"),//not implemented
        HomePastDigitalSeeAll("home_past_digital_see_all"),//not implemented
        PerksCopyCode("perks_copy_code"),
        PerksVisitSite("perks_visit_site"),
        PerksOpenFromHomeCarousel("perks_open_from_home_carousel"),
        PerksOpenFromHomeFooter("perks_open_from_home_footer"), //not applicable
        HomeHouseNotesLatest("home_house_notes_latest"),    //not applicable
        HomeHouseNotesTapCarousel("home_house_notes_tap_carousel"),
        HomeHouseNotesSeeAll("home_house_notes_see_all"),
        HomeHouseNotesDiscoverNotes("home_house_notes_discover_notes"),
        ViewAnotherNoticeboard("view_another_noticeboard"),
        HomeNoticeboardPost("home_noticeboard_post"),
        NoticeboardPost("noticeboard_post"),
        NoticeboadDeletePost("noticeboad_delete_post"),
        HomeHousesSeeAll("home_houses_see_all"),
        HomeHousesClickImage("home_houses_click_image"),
        HouseBoardBannerTapped("house_board_banner_tapped"),
        HomeOpenPerks("perks_open_from_home_carousel"),
        HomeQuickAccess("home_quick_access_tap"),

        /*
            BOOK
         */
        EventsTab("events_tab"),
        EventsLatest("events_latest"),
        EventsFeatured("events_featured"),
        EventsAddToBookings("events_add_to_bookings"),
        EventsBook("events_book"),
        EventsMoreTickets("events_more_tickets"),
        EventsLessTickets("events_less_tickets"),
        EventsBuyTickets("events_buy_tickets"),
        EventsConfirmPayment("events_confirm_payment"),
        EventsCancelTicketless("events_cancel_ticketless"),
        EventsFilter("events_filter"),
        EventsFilterLocation("events_filter_location"),
        EventsFilterDate("events_filter_date"),
        EventsFilterCategory("events_filter_category"),
        EventsFilterLocationConfirm("events_filter_location_confirm"),
        EventsFilterDateConfirm("events_filter_date_confirm"),
        EventsFilterCategoryConfirm("events_filter_category_confirm"),
        EventsPastDigitalSeeAll("events_past_digital_see_all"),//not implemented
        EventsDigitalAlert("events_digital_alert"),//not implemented
        EventsDigitalJoinNow("events_digital_join_now"),//not implemented
        EventsDigitalLiveSoon("events_digital_live_soon"),//not implemented
        ScreeningsTab("screenings_tab"),
        ScreeningsLatest("screenings_latest"),
        ScreeningsFeatured("screenings_featured"),
        ScreeningsMoreTickets("screenings_more_tickets"),
        ScreeningsLessTickets("screenings_less_tickets"),
        ScreeningsBookAndPay("screenings_book_and_pay"),
        ScreeningsConfirmDeposit("screenings_confirm_deposit"),
        ScreeningsJoinLottery("screenings_join_lottery"),
        ScreeningsJoinWaitingList("screenings_join_waiting_list"),
        ScreeningsAlertMe("screenings_alert_me"),
        ScreeningsFilter("screenings_filter"),
        ScreeningsFilterLocation("screenings_filter_location"),
        ScreeningsFilterDate("screenings_filter_date"),
        ScreeningsFilterCategory("screenings_filter_category"),
        ScreeningsFilterLocationConfirm("screenings_filter_location_confirm"),
        ScreeningsFilterDateConfirm("screenings_filter_date_confirm"),
        ScreeningsFilterCategoryConfirm("screenings_filter_category_confirm"),
        ScreeningsPastDigitalSeeAll("screenings_past_digital_see_all"),//not implemented
        ScreeningsDigitalAlert("screenings_digital_alert"),//not implemented
        ScreeningsDigitalJoinNow("screenings_digital_join_now"),//not implemented
        ScreeningsDigitalLiveSoon("screenings_digital_live_soon"),//not implemented
        GymTab("gym_tab"),
        GymLatest("gym_latest"),
        GymFeatured("gym_featured"),
        GymBookAndPay("gym_book_and_pay"),
        GymBuyTicket("gym_buy_ticket"),
        GymFilter("gym_filter"),
        GymFilterLocation("gym_filter_location"),
        GymFilterDate("gym_filter_date"),
        GymFilterCategory("gym_filter_category"),
        GymFilterLocationConfirm("gym_filter_location_confirm"),
        GymFilterDateConfirm("gym_filter_date_confirm"),
        GymFilterCategoryConfirm("gym_filter_category_confirm"),
        GymPastDigitalSeeAll("gym_past_digital_see_all"),//not implemented
        GymPastDigitalAlert("gym_past_digital_alert"),//not implemented
        GymPastDigitalJoinNow("gym_past_digital_join_now"),//not implemented
        GymDigitalLiveSoon("gym_digital_live_soon"),//not implemented
        HouseVisit("house_visit"),
        HouseVisitLatest("house_visit_latest"),
        HouseVisitFeatured("house_visit_featured"),
        HouseVisitFilter("house_visit_filter"),
        HouseVisitFilterLocation("gym_filter_location"),
        HouseVisitFilterDate("gym_filter_date"),
        HouseVisitFilterCategory("gym_filter_category"),
        BedroomsTab("bedrooms_tab"),
        BedroomsBackToApp("Bedrooms_back_to_app"),
        TableBookingCheckAvailability("table_booking_landing_check_availability"),

        /*
            TABLE BOOKINGS
         */
        TableAvailabilityConfirmSlot("table_availability_confirm_slot"),
        TableBookingAdditionalComment("table_booking_additional_comments_filled"),
        TableBookingSummaryCreate("table_booking_summary_create_booking"),
        TableBookingSummaryEdit("table_booking_summary_edit_booking"),
        TableBookingDone("table_booking_done"),
        TableBookingCancel("table_booking_detail_cancel_booking"),
        TableBookingCancelFromNotification("table_booking_cancel_from_notification"),
        TableBookingCancelRestricted("table_booking_detail_cancel_restricted"),
        TableBookingEdit("table_booking_detail_edit_booking"),
        TableBookingEditFromNotification("table_booking_edit_from_notification"),
        TableBookingEditRestricted("table_booking_detail_edit_restricted"),
        TableBookingModifyException("table_booking_modify_exception"),

        BookingId("booking_id"),
        HouseId("house_id"),
        TableBookingAlternateSuggestionDisplay("alternative_suggestions_displayed"),
        TableBookingAlternateSuggestionChangedClick("alternative_search_changed"),
        TableBookingAlternateSuggestionItemSelected("alternative_suggestion_selected"),

        /*
            DISCOVER
         */
        DiscoverHouseNotes("discover_house_notes"),
        DiscoverLatestHouseNote("discover_latest_house_note"), // not applicable
        DiscoverFeaturedHouseNote("discover_featured_house_note"),// not applicable
        DiscoverHouses("discover_houses"),
        DiscoverPerks("discover_perks"),
        DiscoverOpenPerks("perks_open_from_discover"),

        /*
            ACCOUNT
         */
        AccountViewProfile("account_view_profile"),
        AccountLandingViewProfile("account_landing_view_profile"),
        EditProfileBio("edit_profile_bio"),
        EditProfileCity("edit_profile_city"),
        EditProfileHome("edit_profile_home"),
        EditProfileImageHome("edit_profile_image_home"),
        EditProfileImageSaveTap("edit_profile_image_save_tap"),
        EditProfileImageSettings("edit_profile_image_settings"),
        EditProfileInterests("edit_profile_interests"),
        EditProfileLetsChat("edit_profile_lets_chat"),
        EditProfileNatureOfBusiness("edit_profile_nature_of_business"),
        EditProfileOccupation("edit_profile_occupation"),
        EditProfilePhoneNumber("edit_profile_phone_number"),
        EditProfileSettings("edit_profile_settings"),
        EditProfileSocialAccounts("edit_profile_social_accounts"),
        SaveProfileDidTap("save_profile_did_tap"),
        SaveProfileFail("save_profile_fail"),
        SaveProfileSuccess("save_profile_success"),
        ViewPersonalProfile("view_personal_profile"),
        ViewPublicProfile("view_public_profile"),
        ViewPublicProfileHome("view_public_profile_home"),
        ViewPublicProfileNoticeBoard("view_public_profile_notice_board"),
        Upload("upload"),
        ProfileAddProfilePhotoModule("profile_add_profile_photo_module"),   //not implemented
        ProfileUpdateBioModule("profile_update_bio_module"),    //not implemented
        ViewPublicSocialInstagram("view_public_social_instagram"),
        ViewPublicSocialLinkedIn("view_public_social_linked_in"),
        ViewPublicSocialSpotify("view_public_social_spotify"),
        ViewPublicSocialTwitter("view_public_social_twitter"),
        ViewPublicSocialWebsite("view_public_social_website"),
        ViewPublicSocialYoutube("view_public_social_youtube"),
        AccountMembership("account_membership"),
        MembershipCardAddToWallet("membership_card_add_to_wallet"),//not implemented
        MembershipCardViewInWallet("membership_card_view_in_wallet"),//not implemented
        AccountBookings("account_bookings"),
        PastBookingWatchAgain("past_booking_watch_again"),//not implemented
        BookingHistoryTapEvent("booking_history_tap_event"),
        BookingHistoryTapRoom("booking_history_tap_room"),
        BookingTabHistory("booking_tab_history"),
        BookingTabUpcoming("booking_tab_upcoming"),
        BookingUpcomingTapEvent("booking_upcoming_tap_event"),
        BookingUpcomingTapRoom("booking_upcoming_tap_room"),
        AccountPaymentMethods("account_payment_methods"),
        AddPaymentMethod("add_payment_method"),
        AccountReceipts("account_receipts"),//not implemented
        OpenCheckHouseBoard("open_check_house_board"),//not implemented
        OpenCheckNotification("open_check_notification"),//not implemented
        ClosedCheckHouseBoard("closed_check_house_board"),//not implemented
        ChangePaymentCheck("change_payment_check"),//not implemented
        CustomTipHousePay("custom_tip_house_pay"),//not implemented
        MultiplePaymentsOverlayCheck("multiple_payments_overlay_check"),//not implemented
        PullToRefreshCheck("pull_to_refresh_check"),//not implemented
        AccountFavouriteHouses("account_favourite_houses"),
        ResetFavouriteHouses("reset_favourite_houses"),
        SaveFavouriteHouses("save_favourite_houses"),
        FavouriteHousesExpandEurope("favourite_houses_expand_europe"),
        FavouriteHousesExpandAsia("favourite_houses_expand_asia"),
        FavouriteHousesExpandNorthAmerica("favourite_houses_expand_north_america"),
        FavouriteHousesExpandUk("favourite_houses_expand_uk"),
        FavouriteHousesExpandCwh("favourite_houses_expand_cwh"),
        AccountTermsAndPolicies("account_terms_and_policies"),
        AccountContactUs("account_contact_us"),
        AccountNotificationPreferences("account_notification_preferences"),
        NotificationsOptions("notifications_options"),
        NotificationsOptionsClearall("notifications_options_clearall"), // not applicable
        NotificationsOptionsDone("notifications_options_done"),
        NotificationsOptionsSettings("notifications_options_settings"),
        NotificationsRemoveOutOfBounds("notifications_remove_out_of_bounds"), // not applicable
        AccountSyncCalendar("account_sync_calendar"),//not implemented
        AccountChangePassword("account_change_password"),
        AccountSignOut("account_sign_out"),
        PrepopulateProfileChangesConfirm("prepopulated_changes_confirm"),
        PrepopulateProfileFieldEdit("prepopulated_field_edit"),
        PrepopulatedReviewProfile("prepopulated_review_profile"),
        PrepopulatedClose("prepopulated_close"),
        ShareSocialConnectionsToggle("share_social_connection_toggle"),
        EditProfileGenderPronouns("edit_profile_gender_pronouns"),
        AccountLandingShareProfile("account_landing_share_profile"),
        ProfileShareProfile("profile_share_profile"),
        ConnectShareProfileCopyLink("connect_share_profile_copy_link"),
        ConnectShareProfileMoreOptions("connect_share_profile_more_options"),

        /*
            HOUSE BOARD
         */
        ExpandNotificationsStack("expand_notifications_stack"),
        NotificationsShowless("notifications_showless"),
        NotificationsSwipeview("notifications_swipeview"),
        NotificationsTapclear("notifications_tapclear"),
        NotificationsTapview("notifications_tapview"),
        HouseBoardTapHouse("house_board_tap_house"),
        HouseBoardMembershipCard("house_board_membership_card"),

        HouseBoardBookingTapEvent("house_board_booking_tap_event"),
        HouseBoardBookingSeeAll("house_board_booking_see_all"),

        //FIXME these are the same as above 2 (same trigger)
        UpcomingBookingsCarouselTapEvent("booking_carousel_tap_event"),
        UpcomingBookingsSeeAll("booking_carousel_see_all"),

        /*
            SIGN IN
         */
        SignIn("sign_in"),
        CreateAccount("create_account"),
        SignInMember("sign_in_member"),
        SignInMemberFail("sign_in_member_fail"),
        SignInForgotPassword("sign_in_forgot_password"),
        AuthLoginSuccess("auth_login_success"),
        AuthLoginEmpty("auth_login_empty"),

        /**
         * LOGOUT
         * */
        LogoutConfirm("logout_confirm"),

        /*
            OTHER
         */
        OfflineSnackBarAppeared("offline_snack_bar_appeared"),
        BackOnlineSnackBarAppeared("back_online_snack_bar_appeared"),
        DidTapTryAgainCTAOffline("did_tap_try_again_CTA_offline"),
        ActiveSubscribe("active_subscribe"),
        ActiveInfoSubscribe("active_info_subscribe"),
        ActiveInfoSubscribePlus("active_info_subscribe_plus"),
        AppForegrounded("app_bring_to_foreground"),
        AppBackgrounded("app_send_to_background"),

        ForceUpdateRequest("force_update_request"),
        PassPoint("pass_point"),
        CompleteAccount("getCompleteAccountV2"),

        /**
         * House Guest
         */
        HouseBoardGuestsNewListStart("house_board_guests_new_list_start"),
        AccountGuestLandingOpen("account_guests_landing_open"),
        GuestsLandingNewListStart("guests_landing_new_list_start"),
        GuestsDetailNewListCreate("guests_detail_new_list_create"),
        GuestsDetailNewListCancel("guests_detail_new_list_cancel"),
        GuestsDetailShare("guests_detail_share"),
        GuestsDetailGuestAdd("guests_detail_guest_add"),
        GuestsDetailListDelete("guests_detail_list_delete"),

        /**
         *
         */
        OnboardingWelcomeNext("onboarding_welcome_next"),
        OnboardingPrivacyNext("onboarding_privacy_next"),
        OnboardingDressCodeNext("onboarding_dress_code_next"),
        OnboardingMembershipCardNext("onboarding_membership_card_next"),
        OnboardingMobileFreeNext("onboarding_mobile_free_next"),
        OnboardingHouseVisitNext("onboarding_house_visit_next"),
        OnboardingGuestInviteNext("onboarding_guest_invite_next"),
        OnboardingChildrenNext("onboarding_children_next"),
        OnboardingBillingNext("onboarding_billing_next"),
        OnboardingStayWithUsNext("onboarding_stay_with_us_next"),
        OnboardingSFSpacesNext("onboarding_SF_spaces_next"),
        OnboardingMemberBenefitsNext("onboarding_member_benefits_next"),

        /**
         * Connect - Noticeboard
         */
        NoticeboardFilter("noticeboard_filter"),
        NoticeboardFilterClose("noticeboard_filter_close"),
        NoticeboardFilterConfirm("noticeboard_filter_confirm"),
        NoticeboardFilterDeselect("noticeboard_filter_deselect"),
        NoticeboardFilterPostTag("noticeboard_filter_post_tag"),
        NoticeboardFilterSelect("noticeboard_filter_select"),
        NoticeboardFilterUnselect("noticeboard_filter_unselect"),

        NoticeboardPostCancel("noticeboard_cancel"),
        NoticeboardPostCityAdd("noticeboard_city_add"),
        NoticeboardPostDelete("noticeboard_post_delete"),
        NoticeboardPostDeleteBack("noticeboard_post_delete_back"),
        NoticeboardPostDeleteConfirm("noticeboard_post_delete_confirm"),
        NoticeboardPostDetailBack("noticeboard_post_detail_back"),
        NoticeboardPostDetailReplySubmit("noticeboard_post_detail_reply_submit"),
        NoticeboardPostHouseAdd("noticeboard_house_add"),
        NoticeboardPostReplyTo("noticeboard_post_reply_to"),
        NoticeboardPostSubmit("noticeboard_post_submit"),
        NoticeboardPostTap("noticeboard_post_tap"),
        NoticeboardPostTopicAdd("noticeboard_topic_add"),
        NoticeboardPostWrite("noticeboard_post_write"),

        NoticeboardReactionCount("noticeboard_reaction_count"),
        NoticeboardReactionLongPress("noticeboard_reaction_long_press"),
        NoticeboardReactionTap("noticeboard_reaction_tap"),
        NoticeboardReactionTapProfile("noticeboard_reactions_tap_profile"),
        NoticeboardReactionUntap("noticeboard_reaction_untap"),
        NoticeboardTapProfile("noticeboard_tap_profile"),

        MessagingMute("chat_conversation_mute_swipe"),
        MessagingUnMute("Chat_conversation_unmute_swipe"),
        MessagingMessagesMoreTap("chat_messages_more_tap"),
        MessagingUserBlockConfirm("chat_messages_user_block_confirm"),
        MessagingUserBlock("chat_messages_user_block"),
        MessagingUserBlockCancel("chat_messages_user_block_cancel"),
        MessagingUserReport("chat_messages_user_report"),
        MessagingUserReportSubmit("chat_messages_user_report_submit"),
        MessagingUserReportBack("chat_messages_user_report_back"),
        MessagingChatNewMessage("chat_new_message"),
        MessagingChatConversationOpenTap("chat_conversation_open_tap"),
        MessagingCannotCreateTempFile("cannot_create_temp_file"),
        ConnectConnectionsMenuBlockedOpen("connect_connections_menu_blocked"),

        /**
         *  Traffic Lights
         * */
        TrafficLightsLeaveVenue("connect_checkin_leave_venue"),
        TrafficLightsLeaveVenueConfirm("connect_checkin_leave_venue_confirm"),
        TrafficLightsLeaveVenueCancel("connect_checkin_leave_venue_cancel"),
        TrafficLightsCheckingAvailable("connect_checkin_available"),
        TrafficLightsCheckingConnectionsOnly("connect_checkin_only_connected"),
        TrafficLightsCheckingUnavailable("connect_checkin_unavailable"),
        TrafficLightsCheckingConfirm("connect_checkin_confirm"),
        TrafficLightsCheckingDismiss("connect_checkin_dismiss"),


        ProfileConnectWriteMessage("profile_connect_write_message"),
        ProfileConnectMessageSent("profile_connect_message_sent"),
        ProfileConnectMessageCancel("profile_connect_message_cancel"),

        ConnectConnectionOnBoardingNext("connect_connection_onboarding_next"),
        ConnectRecommendationOptInToggle("connect_recommendations_optin_toggle"),
        ConnectRecommendationOptInConfirm("connect_recommendations_opt_in_confirm"),
        ConnectRecommendationOptInCancel("connect_recommendations_opt_in_cancel"),
        ConnectRecommendationOnBoardingEditProf("connect_connection_onboarding_edit_prof"),

        //When selecting Discover members
        ConnectDiscoverMember("connect_discover_member"),

        //When selecting to filter from the discover members page
        RecommendationsFilter("recommendations_filter"),

        //When deselecting recommendations
        //recommendations_filter_deselect (params: global_id; recommendation_industry, recommendation_location, recommendation_interest)
        RecommendationsFilterDeselect("recommendations_filter_deselect"),

        //When selecting to filter by selecting a tablet
        RecommendationsFilterSelect("recommendations_filter_select"),

        //When unselecting recommendations (dismissing the tablet)
        RecommendationsFilterUnselect("recommendations_filter_unselect"),

        //When selecting to confirm and refine the recommendations by the values selected
        RecommendationsFilterConfirm("recommendations_filter_confirm"),

        //When closing the refine recommendations page
        // (global_id, recommendation_industry, recommendation_location, recommendation_interest)
        RecommendationsFilterClose("recommendations_filter_close"),

        ProfileMessageTap("profile_message_tap"),
        ChatMessagesOpened("chat_messages_opened"),
        ChatMessagesSendTap("chat_messages_send_tap"),

        None(""),
    }


    enum class Parameters(val value: String) {
        PerkId("perks_id"),
        PerkName("perk_name"),
        PerkCode("code_copied"),
        PerkUrl("site_url"),
        HouseName("house_name"),
        EventId("event_id"),
        EventTitle("event_title"),
        EventType("event_type"),
        RoomBookingId("room_booking_id"),
        RoomBookingName("room_name"),
        UserGlobalId("user_global_id"),
        MembershipType("user_membership_type"),
        MembershipSubscriptionType("user_membership_subscription_type"),
        TimeSpent("time_spent"),
        TotalTimeSpent("total_time_spent"),
        ShareSocialConnectionsToggleEnabledState("enabled"),
        MessagingMessagesAreEmpty("messages_are_empty"),
        MessagingRecipientGlobalID("recipient_global_id"),
        GlobalId("global_id"),
        RecipientGlobalId("recipient_global_id"),
        BookedTableId("booked_table_id"),
        BookedTableName("booked_table_name"),
        BookedTableVenue("booked_table_venue"),
        BookedTableDate("booked_table_date"),
        MessagingFileCreateError("messaging_file_create_error"),
    }

    object NoticeboardReactions {
        fun getParams(postID: String, screenID: String, reaction: Reaction?): Bundle {
            val bundle = bundleOf(
                "noticeboard_post_id" to postID,
                "screen_id" to screenID,
            )
            if (reaction != null) {
                bundle.putString("reaction_id", reaction.name)
            }

            return bundle
        }
    }

    object Bookings {
        fun getEventBookingParams(eventBooking: EventBooking): Bundle {
            return Bundle().apply {
                putString(Parameters.EventId.value, eventBooking.event?.id ?: "")
                putString(Parameters.EventTitle.value, eventBooking.event?.name ?: "")
                putString(Parameters.EventType.value, eventBooking.event?.eventType ?: "")
            }
        }

        fun getRoomBookingParams(roomBooking: RoomBooking): Bundle {
            return Bundle().apply {
                putString(Parameters.RoomBookingId.value, roomBooking.id)
                putString(
                    Parameters.RoomBookingName.value,
                    roomBooking.room?.get(roomBooking.document)?.name ?: ""
                )
            }
        }

        fun getBookedTableParams(bookedTable: BookedTable): Bundle {
            return Bundle().apply {
                putString(Parameters.BookedTableId.value, bookedTable.id)
                putString(Parameters.BookedTableName.value, bookedTable.name)
                putString(Parameters.BookedTableVenue.value, bookedTable.venueDetails.name)
                putString(
                    Parameters.BookedTableDate.value,
                    bookedTable.bookedTableDate.date.toString()
                )
            }
        }
    }

    object Perks {
        fun getParams(
            id: String? = null,
            title: String? = null,
            promoCode: String? = null,
            membershipType: String? = null,
            subscriptionType: String? = null
        ): Bundle {
            return Bundle().apply {
                putString(Parameters.PerkId.value, id)
                putString(Parameters.PerkName.value, title)
                putString(Parameters.PerkCode.value, promoCode)
                putString(Parameters.MembershipType.value, membershipType)
                putString(Parameters.MembershipSubscriptionType.value, subscriptionType)
            }
        }

        fun getPromoCodeParams(id: String, name: String?, promoCode: String?): Bundle {
            return Bundle().apply {
                putString(Parameters.PerkId.value, id)
                putString(Parameters.PerkName.value, name)
                putString(Parameters.PerkCode.value, promoCode)
            }
        }

        fun getVisitEventParams(id: String, name: String, url: String): Bundle {
            return Bundle().apply {
                putString(Parameters.PerkId.value, id)
                putString(Parameters.PerkName.value, name)
                putString(Parameters.PerkUrl.value, url)
            }
        }
    }

    object SubscribeActive {
        private const val EVENT_ID = "event_id"
        private const val EVENT_TITLE = "event_title"
        private const val TYPE = "type"

        fun buildParams(eventId: String?, eventTitle: String?, type: String?): Bundle {
            return Bundle().apply {
                putString(EVENT_ID, eventId)
                putString(EVENT_TITLE, eventTitle)
                putString(TYPE, type)
            }
        }
    }

    object SocialMedia {
        fun mapToAction(account: SocialMediaItem): Action {
            return when (account.type) {
                SPOTIFY -> (Action.ViewPublicSocialSpotify)
                YOUTUBE -> (Action.ViewPublicSocialYoutube)
                LINKEDIN -> (Action.ViewPublicSocialLinkedIn)
                TWITTER -> (Action.ViewPublicSocialTwitter)
                INSTAGRAM -> (Action.ViewPublicSocialInstagram)
                WEBSITE -> (Action.ViewPublicSocialWebsite)
            }
        }
    }

    object ProfileFields {
        const val OCCUPTATION = "OCCUPATION"
        const val INDUSTRY = "INDUSTRY"
        const val CITY = "CITY"
    }

    object HouseNameParamHelper {
        fun buildParams(venue: Venue?): Bundle {
            return Bundle().apply {
                putString(Parameters.HouseName.value, venue?.slug)
            }
        }
    }

    object HouseGuest {
        private const val MEMBERSHIP_TYPE = "user_membership_type"
        private const val HOUSE_ID = "house_id"
        private const val GUEST_COUNT = "guest_count"
        private const val INVITE_ID = "invite_id"

        fun buildParams(
            membershipType: String? = null,
            houseId: String? = null,
            guestCount: Int? = null,
            inviteId: String? = null
        ): Bundle? {
            if (membershipType.isNullOrEmpty() && houseId.isNullOrEmpty() && guestCount == null) return null

            return Bundle().apply {
                membershipType?.let { putString(MEMBERSHIP_TYPE, it) }
                houseId?.let { putString(HOUSE_ID, it) }
                guestCount?.let { putInt(GUEST_COUNT, it) }
                inviteId?.let { putString(INVITE_ID, it) }
            }
        }
    }

    /**
     * A-Z order
     */
    enum class Screens {
        Account,
        AnalyticsPolicy,
        AppIcon,
        Booking,
        BookingConfirmation,
        CalendarSync,
        CategoryPreferences,
        Connections,
        ChangePassword,
        Chat,
        CityGuide,
        ContactUs,
        Connect,
        Discover,
        EditCity,
        EditInterests,
        EditOccupation,
        EditProfile,
        EventCategories,
        EventDetails,
        Events,
        FAQ,
        Filter,
        Fitness,
        FitnessDetails,
        FitnessFiltered,
        Home,
        House,
        HouseBoard,
        HouseBoardPost,
        HouseContact,
        HouseNotes,
        HouseNotesDetail,
        HouseNotesFiltered,
        HouseNotesSingleContent,
        HousePreferences,
        HouseRules,
        Houses,
        HouseTour,
        HouseVisit,
        InductionBooking,
        Intro,
        Login,
        MembershipDetails,
        Noticeboard,
        NotificationSetting,
        FilterBottomSheet,
        NotificationPreferences,
        OnboardCategoryPreferences,
        OnboardDataCollection,
        OnboardFinish,
        OnboardHouseBoardPost,
        OnboardHousePreferences,
        OnboardNoticeboard,
        OnboardTermsConditions,
        HousePayTermsConditions,
        OnboardWelcome,
        PastBookings,
        PastBookingsDetail,
        PaymentConfirmation,
        PaymentMethods,
        Perks,
        PerksDetails,
        PerksLanding,
        Privacy,
        PrivacyPolicies,
        RoomBook,
        RoomBookings,
        RoomCancel,
        RoomCancelled,
        RoomDetails,
        RoomLanding,
        RoomPromotion,
        Settings,
        ScreeningDetails,
        Screenings,
        ScreeningsFiltered,
        Splash,
        TermsConditions,
        Under27Discount,
        UpcomingBookings,
        ViewProfile,
        Welcome
    }
}

class FirebaseAnalyticsManager(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val userManager: UserManager
) : AnalyticsManager {

    override fun logEventAction(action: AnalyticsManager.Action, params: Bundle?) {
        firebaseAnalytics.logEvent(action.value, (params ?: Bundle()).apply {
            putString(AnalyticsManager.Parameters.UserGlobalId.value, userManager.accountId)
            putString(BundleKeys.SUBSCRIPTION_TYPE, userManager.subscriptionType.name)
        })
        FirebaseCrashlytics.getInstance().log("${action.value}: ${params?.print()}")
    }

    override fun setScreenName(activityName: String, screen: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screen)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, activityName)
        }
        FirebaseCrashlytics.getInstance().log("Viewed $screen")
    }

    override fun setAnalyticsEnabled(enabled: Boolean) {
        firebaseAnalytics.setAnalyticsCollectionEnabled(enabled)
    }

    override fun track(eventAnalytics: AnalyticsEvent) {
        firebaseAnalytics.logEvent("${eventAnalytics.category}: ${eventAnalytics.action} ${eventAnalytics.label}") {
            param(AnalyticsManager.Parameters.UserGlobalId.value, userManager.accountId)
            param(BundleKeys.SUBSCRIPTION_TYPE, userManager.subscriptionType.name)
        }
        FirebaseCrashlytics.getInstance()
            .log("${eventAnalytics.category}: ${eventAnalytics.action} ${eventAnalytics.label}")
    }

}