package com.sohohouse.seven.common.prefs

import android.content.Context
import com.sohohouse.seven.BuildConfig
import com.sohohouse.seven.common.user.AnalyticsConsent
import com.sohohouse.seven.common.user.GymMembership
import com.sohohouse.seven.common.user.IconType
import com.sohohouse.seven.common.user.SubscriptionType
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefsManager @Inject constructor(val context: Context) {

    companion object {

        //SECURE
        private const val EMAIL_KEY: String = "email"
        private const val AUTH_TOKEN_KEY = "auth_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val LOCAL_HOUSES_ID = "local_houses"
        private const val MEMBERSHIP_STATUS = "membership_status"
        private const val MEMBERSHIP_TYPE = "membership_type"
        private const val MEMBERSHIP_CODE = "membership_code"
        private const val PROFILE_ID = "profile_id"
        private const val PROFILE_IMAGE_URL = "profile_image_url"
        private const val PROFILE_FIRST_NAME = "profile_first_name"
        private const val PROFILE_LAST_NAME = "profile_last_name"
        private const val PROFILE_OCCUPATION = "profile_occupation"
        private const val PROFILE_LOCATION = "profile_location"
        private const val CALENDAR_SUBSCRIPTION_URL = "calendar_subscription_url"
        private const val PAYMENT_UPDATE_URL = "payment_update_url"
        private const val FAVOURITE_HOUSES = "favourite_houses"
        private const val SUBSCRIPTION_TYPE = "subscription_type"
        private const val ICON_TYPE = "icon_type"
        private const val GYM_MEMBERSHIP = "gym_membership"

        //DEFAULT
        private const val CAN_ACCESS_APP = "can_access_app"
        private const val IS_INDUCTED = "is_indcuted"
        private const val DID_CONSENT_TERMS_CONDITIONS = "did_consent_terms_conditions"
        private const val DID_CONSENT_HOUSE_PAY_CONDITIONS = "did_consent_house_pay_conditions"
        private const val DID_CONSENT_ANALYTICS = "did_consent_analytics"
        private const val DID_DECIDE_ANALYTICS = "did_decide_analytics"
        private const val ANALYTICS_CONSENT = "analytics_consent"
        private const val IS_APP_ONBOARDING_COMPLETE = "is_app_onboarding_complete"
        private const val IS_NOTIFICATION_DIALOG_COMPLETE = "is_notification_dialog_complete_v2"
        private const val IS_NOTIFICATION_PREFERENCES_ALERT_EVENTS_ON =
            "is_notification_preferences_alert_events_on"
        private const val IS_NOTIFICATION_PREFERENCES_ALERT_SCREENINGS_ON =
            "is_notification_preferences_alert_screenings_on"
        private const val VENUE_ATTENDED_ID = "venue_attended_id"
        private const val IS_EMAIL_VERIFIED = "is_email_verified"
        private const val ARE_NOTIFICATIONS_CUSTOMISED = "ARE_NOTIFICATIONS_CUSTOMISED"
        private const val HAS_SEEN_WELCOME_SCREEN = "has_seen_welcome_screen"
        private const val PERKS_FILTER_CITIES = "PERKS_FILTER_CITIES"
        private const val NOTICEBOARD_FILTER_VENUES = "NOTICEBOARD_FILTER_VENUES"
        private const val HAS_SEEN_ONBOARDING_BENEFITS_SCREEN =
            "has_seen_onboarding_benefits_screen"
        private const val IS_STAFF = "is_staff"
        private const val HAS_SEEN_ONBOARDING_WELCOME_SCREEN = "has_seen_onboarding_welcome_screen"
        private const val CONFIRM_PREPOPULATED_PROFILE_DATA = "confirm_prepopulated_profile_data"
        private const val AVAILABILITY_STATUS = "availability_status"
        private const val IS_CHECKED_IN = "is_checked_in"
        private const val LANDING_OPT_IN_RECOMMENDATIONS = "landing_opt_in_recommendations"
        private const val CONNECT_RECOMMENDATION = "connectRecommendationOptIn"
        private const val DISMISSED_CHECK_CLOSED_IDS = "DISMISSED_CHECK_CLOSED_IDS"
    }

    private val securePrefsStore = SecurePrefsStore(context)
    private val defaultPrefsStore = DefaultPrefsStore(context)
    private val stablePrefsStore = StablePrefsStore(context)

    init {
        migrateIfNeeded()
    }

    var token: String
        get() = securePrefsStore.getString(AUTH_TOKEN_KEY)
        set(value) = securePrefsStore.putString(AUTH_TOKEN_KEY, value)

    var refreshToken: String
        get() = securePrefsStore.getString(REFRESH_TOKEN_KEY)
        set(value) = securePrefsStore.putString(REFRESH_TOKEN_KEY, value)

    var availableStatus: String
        get() = defaultPrefsStore.getString(AVAILABILITY_STATUS)
        set(value) = defaultPrefsStore.putString(AVAILABILITY_STATUS, value)

    var localHouseId: String
        get() = securePrefsStore.getString(LOCAL_HOUSES_ID)
        set(value) = securePrefsStore.putString(LOCAL_HOUSES_ID, value)

    var canAccessApp: Boolean
        get() = defaultPrefsStore.getBoolean(CAN_ACCESS_APP)
        set(value) = defaultPrefsStore.putBoolean(CAN_ACCESS_APP, value)

    var isEmailVerified: Boolean
        get() = defaultPrefsStore.getBoolean(IS_EMAIL_VERIFIED)
        set(value) = defaultPrefsStore.putBoolean(IS_EMAIL_VERIFIED, value)

    var membershipType: String
        get() = securePrefsStore.getString(MEMBERSHIP_TYPE)
        set(value) = securePrefsStore.putString(MEMBERSHIP_TYPE, value)

    var membershipStatus: String
        get() = securePrefsStore.getString(MEMBERSHIP_STATUS)
        set(value) = securePrefsStore.putString(MEMBERSHIP_STATUS, value)

    var isInducted: Boolean
        get() = defaultPrefsStore.getBoolean(IS_INDUCTED)
        set(value) = defaultPrefsStore.putBoolean(IS_INDUCTED, value)

    var isStaff: Boolean
        get() = defaultPrefsStore.getBoolean(IS_STAFF)
        set(value) = defaultPrefsStore.putBoolean(IS_STAFF, value)

    fun landingOptInRecommendationsStateForUser(userID: String): Boolean {
        return stablePrefsStore.getBoolean("$LANDING_OPT_IN_RECOMMENDATIONS+$userID", true)
    }

    fun landingOptInRecommendationsIsShownForUser(userID: String) {
        stablePrefsStore.putBoolean("$LANDING_OPT_IN_RECOMMENDATIONS+$userID", false)
    }

    // kinda persistent data to keep consistency with iOS
    var isAppOnboardingComplete: Boolean?
        get() {
            if (!defaultPrefsStore.contains(IS_APP_ONBOARDING_COMPLETE)) return null
            return defaultPrefsStore.getBoolean(IS_APP_ONBOARDING_COMPLETE)
        }
        set(value) {
            defaultPrefsStore.putBoolean(IS_APP_ONBOARDING_COMPLETE, value ?: return)
        }

    var hasSeenOnboardingBenefitsScreen: Boolean
        get() = defaultPrefsStore.getBoolean(HAS_SEEN_ONBOARDING_BENEFITS_SCREEN)
        set(value) {
            defaultPrefsStore.putBoolean(HAS_SEEN_ONBOARDING_BENEFITS_SCREEN, value ?: return)
        }

    var hasSeenOnboardingWelcomeScreen: Boolean
        get() = defaultPrefsStore.getBoolean(HAS_SEEN_ONBOARDING_WELCOME_SCREEN)
        set(value) = defaultPrefsStore.putBoolean(HAS_SEEN_ONBOARDING_WELCOME_SCREEN, value)

    var didConsentTermsConditions: Boolean
        get() = defaultPrefsStore.getBoolean(DID_CONSENT_TERMS_CONDITIONS)
        set(value) = defaultPrefsStore.putBoolean(DID_CONSENT_TERMS_CONDITIONS, value)

    var didConsentHousePayTermsConditions: Boolean
        get() = defaultPrefsStore.getBoolean(DID_CONSENT_HOUSE_PAY_CONDITIONS)
        set(value) = defaultPrefsStore.putBoolean(DID_CONSENT_HOUSE_PAY_CONDITIONS, value)

    var didConsentAnalytics: Boolean
        get() = defaultPrefsStore.getBoolean(DID_CONSENT_ANALYTICS)
        set(value) = defaultPrefsStore.putBoolean(DID_CONSENT_ANALYTICS, value)

    var didDecideAnalytics: Boolean
        get() = defaultPrefsStore.getBoolean(DID_DECIDE_ANALYTICS)
        set(value) = defaultPrefsStore.putBoolean(DID_DECIDE_ANALYTICS, value)

    var analyticsConsent: AnalyticsConsent
        get() = defaultPrefsStore.getInt(ANALYTICS_CONSENT, AnalyticsConsent.NONE.ordinal)
            .let { ordinal ->
                AnalyticsConsent.fromOrdinal(ordinal)
            }
        set(value) = defaultPrefsStore.putInt(ANALYTICS_CONSENT, value.ordinal)

    var calendarSubscriptionUrl: String
        get() = securePrefsStore.getString(CALENDAR_SUBSCRIPTION_URL)
        set(value) = securePrefsStore.putString(CALENDAR_SUBSCRIPTION_URL, value)

    var favouriteHouses: List<String>?
        get() = securePrefsStore.getStringList(FAVOURITE_HOUSES)
        set(value) = securePrefsStore.putStringList(FAVOURITE_HOUSES, value)

    var profileID: String
        get() = securePrefsStore.getString(PROFILE_ID)
        set(value) = securePrefsStore.putString(PROFILE_ID, value)

    var accountId: String
        get() = securePrefsStore.getString(MEMBERSHIP_CODE)
        set(value) = securePrefsStore.putString(MEMBERSHIP_CODE, value)

    var profileImageURL: String
        get() = securePrefsStore.getString(PROFILE_IMAGE_URL)
        set(value) = securePrefsStore.putString(PROFILE_IMAGE_URL, value)

    var firstName: String
        get() = securePrefsStore.getString(PROFILE_FIRST_NAME)
        set(value) = securePrefsStore.putString(PROFILE_FIRST_NAME, value)

    var lastName: String
        get() = securePrefsStore.getString(PROFILE_LAST_NAME)
        set(value) = securePrefsStore.putString(PROFILE_LAST_NAME, value)

    var occupation: String
        get() = securePrefsStore.getString(PROFILE_OCCUPATION)
        set(value) = securePrefsStore.putString(PROFILE_OCCUPATION, value)

    var location: String
        get() = securePrefsStore.getString(PROFILE_LOCATION)
        set(value) = securePrefsStore.putString(PROFILE_LOCATION, value)

    var paymentUpdateUrl: String
        get() = securePrefsStore.getString(PAYMENT_UPDATE_URL)
        set(value) = securePrefsStore.putString(PAYMENT_UPDATE_URL, value)

    var email: String
        get() = securePrefsStore.getString(EMAIL_KEY)
        set(value) = securePrefsStore.putString(EMAIL_KEY, value)

    var isNotificationDialogComplete: Boolean
        get() = defaultPrefsStore.getBoolean(IS_NOTIFICATION_DIALOG_COMPLETE)
        set(value) = defaultPrefsStore.putBoolean(IS_NOTIFICATION_DIALOG_COMPLETE, value)

    var attendedVenueId: String
        get() = defaultPrefsStore.getString(VENUE_ATTENDED_ID)
        set(value) = defaultPrefsStore.putString(VENUE_ATTENDED_ID, value)

    var perksFilterCities: List<String>
        get() = defaultPrefsStore.getStringList(PERKS_FILTER_CITIES) ?: emptyList()
        set(value) = defaultPrefsStore.putStringList(PERKS_FILTER_CITIES, value)

    var noticeboardFilterVenues: List<String>?
        get() = defaultPrefsStore.getStringList(NOTICEBOARD_FILTER_VENUES)
        set(value) = defaultPrefsStore.putStringList(NOTICEBOARD_FILTER_VENUES, value)

    var subscriptionType: SubscriptionType
        get() = try {
            SubscriptionType.valueOf(securePrefsStore.getString(SUBSCRIPTION_TYPE))
        } catch (e: Exception) {
            SubscriptionType.NONE
        }
        set(value) = securePrefsStore.putString(SUBSCRIPTION_TYPE, value.name)

    var iconType: IconType
        get() = try {
            IconType.valueOf(defaultPrefsStore.getString(ICON_TYPE))
        } catch (e: Exception) {
            IconType.NONE
        }
        set(value) = defaultPrefsStore.putString(ICON_TYPE, value.name)

    var gymMembership: GymMembership
        get() = GymMembership.get(securePrefsStore.getString(GYM_MEMBERSHIP))
        set(value) = securePrefsStore.putString(GYM_MEMBERSHIP, value.name)

    var notificationsCustomised: Boolean
        get() = defaultPrefsStore.getBoolean(ARE_NOTIFICATIONS_CUSTOMISED, false)
        set(value) {
            defaultPrefsStore.putBoolean(ARE_NOTIFICATIONS_CUSTOMISED, value)
        }

    var connectRecommendationOptIn: String
        get() = defaultPrefsStore.getString(CONNECT_RECOMMENDATION)
        set(value) {
            defaultPrefsStore.putString(CONNECT_RECOMMENDATION, value)
        }

    var isCheckedIn: Boolean
        get() = defaultPrefsStore.getBoolean(IS_CHECKED_IN, false)
        set(value) = defaultPrefsStore.putBoolean(IS_CHECKED_IN, value)

    var confirmProfile: Boolean
        get() = defaultPrefsStore.getBoolean(CONFIRM_PREPOPULATED_PROFILE_DATA)
        set(value) = defaultPrefsStore.putBoolean(CONFIRM_PREPOPULATED_PROFILE_DATA, value)

    val dismissedCheckClosedIds: List<String>
        get() = defaultPrefsStore.getStringList(DISMISSED_CHECK_CLOSED_IDS) ?: emptyList()

    fun addDismissedCheckClosedId(checkID: String) {
        defaultPrefsStore.putStringList(
            DISMISSED_CHECK_CLOSED_IDS,
            dismissedCheckClosedIds.toMutableList().apply {
                if (contains(checkID).not()) add(checkID)
            }
        )
    }

    private fun migrateIfNeeded() {
        if (defaultPrefsStore.isEmpty) {
            migrate()
        }

        if (defaultPrefsStore.contains(ANALYTICS_CONSENT).not()) {
            migrateAnalyticsConsent()
        }
    }

    private fun migrateAnalyticsConsent() {
        try {
            val didConsent = defaultPrefsStore.getBooleanIfContains(DID_CONSENT_ANALYTICS)
            val didDecide = defaultPrefsStore.getBooleanIfContains(DID_DECIDE_ANALYTICS)
            analyticsConsent = when {
                didConsent == null || didDecide == null -> if (BuildConfig.DEBUG) AnalyticsConsent.ACCEPTED else AnalyticsConsent.NONE
                didConsent || didDecide -> AnalyticsConsent.ACCEPTED
                else -> AnalyticsConsent.REJECTED
            }

            defaultPrefsStore.remove(DID_CONSENT_ANALYTICS)
            defaultPrefsStore.remove(DID_DECIDE_ANALYTICS)
        } catch (e: Exception) {
        }
    }

    private fun migrate() {
        Timber.d("Migrating...")

        defaultPrefsStore.putBoolean(CAN_ACCESS_APP, securePrefsStore.getBoolean(CAN_ACCESS_APP))
        securePrefsStore.remove(CAN_ACCESS_APP)

        defaultPrefsStore.putBoolean(IS_INDUCTED, securePrefsStore.getBoolean(IS_INDUCTED))
        securePrefsStore.remove(IS_INDUCTED)

        defaultPrefsStore.putBoolean(
            DID_CONSENT_TERMS_CONDITIONS,
            securePrefsStore.getBoolean(DID_CONSENT_TERMS_CONDITIONS)
        )
        securePrefsStore.remove(DID_CONSENT_TERMS_CONDITIONS)

        defaultPrefsStore.putBoolean(
            IS_APP_ONBOARDING_COMPLETE,
            securePrefsStore.getBoolean(IS_APP_ONBOARDING_COMPLETE)
        )
        securePrefsStore.remove(IS_APP_ONBOARDING_COMPLETE)

        defaultPrefsStore.putBoolean(
            IS_NOTIFICATION_DIALOG_COMPLETE,
            securePrefsStore.getBoolean(IS_NOTIFICATION_DIALOG_COMPLETE)
        )
        securePrefsStore.remove(IS_NOTIFICATION_DIALOG_COMPLETE)

        defaultPrefsStore.putBoolean(
            IS_NOTIFICATION_PREFERENCES_ALERT_EVENTS_ON,
            securePrefsStore.getBoolean(IS_NOTIFICATION_PREFERENCES_ALERT_EVENTS_ON)
        )
        securePrefsStore.remove(IS_NOTIFICATION_PREFERENCES_ALERT_EVENTS_ON)

        defaultPrefsStore.putBoolean(
            IS_NOTIFICATION_PREFERENCES_ALERT_SCREENINGS_ON,
            securePrefsStore.getBoolean(IS_NOTIFICATION_PREFERENCES_ALERT_SCREENINGS_ON)
        )
        securePrefsStore.remove(IS_NOTIFICATION_PREFERENCES_ALERT_SCREENINGS_ON)

        migrateAnalyticsConsent()
    }

    fun clearData() {
        // We're showing the welcome screen and onboarding after clean installing the app
        // then after logging in back, we don't show the welcome screen
        // but check whether the use is inducted or not to show onboarding.
        // So, here we restore 'hasSeenWelcomeScreen' and 'isAppOnboardingComplete' after clearing the shared preferences.
        val hasSeenOnboardingWelcomeScreen = hasSeenOnboardingWelcomeScreen
        val hasSeenOnboardingBenefitsScreen = hasSeenOnboardingBenefitsScreen
        val isAppOnboardingComplete = isAppOnboardingComplete
        securePrefsStore.clear()
        defaultPrefsStore.clear()
        this.hasSeenOnboardingWelcomeScreen = hasSeenOnboardingWelcomeScreen
        this.hasSeenOnboardingBenefitsScreen = hasSeenOnboardingBenefitsScreen
        this.isAppOnboardingComplete = isAppOnboardingComplete
    }
}