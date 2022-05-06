package com.sohohouse.seven.more

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import com.sohohouse.seven.R
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.dagger.appComponent
import com.sohohouse.seven.debug.DebugActivity
import com.sohohouse.seven.guests.list.GuestListIndexActivity
import com.sohohouse.seven.housepay.HousepayActivity
import com.sohohouse.seven.more.AccountMenu.*
import com.sohohouse.seven.more.bookings.MyBookingsActivity
import com.sohohouse.seven.more.housepreferences.MoreHousePreferencesActivity
import com.sohohouse.seven.more.payment.MorePaymentActivity
import com.sohohouse.seven.more.synccalendar.SyncCalendarActivity
import com.sohohouse.seven.onboarding.benefits.OnboardingBenefitsActivity
import com.sohohouse.seven.perks.landing.PerksLandingActivity

enum class AccountMenu(val key: String, @StringRes val resId: Int? = null) {
    PROFILE("pref_account_profile"),
    LANDING_VIEW_PROFILE("pref_account_landing_view_profile", R.string.view_profile_cta),
    LANDING_SHARE_PROFILE("account_landing_share_profile"),
    MEMBERSHIP_DETAILS("pref_account_membership", R.string.more_membership_cta),
    MY_NETWORK("pref_account_my_network", R.string.account_my_connections),
    PERKS_LANDING("pref_account_perks", R.string.perks_all_cta),  // TBC
    PAYMENT_METHODS("pref_account_payment", R.string.more_payment_cta),
    BOOKINGS("pref_account_bookings", R.string.label_my_bookings),
    HOUSE_PREFERENCES("pref_account_favourite_houses", R.string.more_house_settings_cta),
    SETTINGS("pref_account_settings", R.string.settings_cta),
    CALENDAR_SYNC("pref_account_sync_calendar", R.string.more_calendar_cta),  // TBC
    FAQS("pref_account_faq", R.string.more_faq_cta),
    FAQS_FRIENDS("pref_account_faq_friends", R.string.more_faq_cta),
    CONTACT_US("pref_account_contact_us", R.string.more_contact_cta),
    TERMS_AND_POLICIES("pref_account_terms_and_policies", R.string.more_terms_and_policies_cta),
    GUEST_INVITATIONS("pref_account_guest_invitations", R.string.label_guest_invitations),
    LOGOUT("pref_account_logout", R.string.more_logout_cta),
    HOUSE_PAY("pref_house_pay", R.string.label_house_pay),
    APP_VERSION("pref_account_app_version", R.string.more_app_version_label)
}

class AccountFlowManager {
    fun transitionFrom(context: Context, key: String?): Intent? {
        val clz = when (key) {
            MEMBERSHIP_DETAILS.key -> OnboardingBenefitsActivity::class.java
            PERKS_LANDING.key -> PerksLandingActivity::class.java
            PAYMENT_METHODS.key -> MorePaymentActivity::class.java
            BOOKINGS.key -> MyBookingsActivity::class.java
            HOUSE_PREFERENCES.key -> MoreHousePreferencesActivity::class.java
            SETTINGS.key -> SettingsActivity::class.java
            CALENDAR_SYNC.key -> SyncCalendarActivity::class.java
            GUEST_INVITATIONS.key -> GuestListIndexActivity::class.java
            HOUSE_PAY.key -> HousepayActivity::class.java
            APP_VERSION.key -> DebugActivity::class.java
            else -> return null
        }
        return when (key) {
            CALENDAR_SYNC.key -> Intent(context, clz).apply {
                putExtra(SyncCalendarActivity.INTENT_EXTRA_KEY_SHOW_TOOLBAR, true)
                putExtra(SyncCalendarActivity.INTENT_EXTRA_KEY_SHOW_CONTINUE, false)
            }
            PROFILE.key, LANDING_VIEW_PROFILE.key -> Intent(context, clz).apply {
                putExtra(BundleKeys.PROFILE_ID_KEY, appComponent.userManager.profileID)
            }
            else -> Intent(context, clz)
        }
    }
}