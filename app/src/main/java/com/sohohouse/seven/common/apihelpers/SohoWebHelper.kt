package com.sohohouse.seven.common.apihelpers

import android.net.Uri
import com.sohohouse.seven.App
import com.sohohouse.seven.common.apihelpers.SohoWebHelper.KickoutType.*
import com.sohohouse.seven.common.extensions.replaceBraces

/**
 * When kicking out to soho web, use this class to fetch the necessary URL.
 *
 * Intentionally not making this a singleton, because no need to.
 */
object SohoWebHelper {
    enum class KickoutType(val path: String) {
        // New urls from DotCom
        RESTAURANT("restaurants"),
        CONTACT_SUPPORT("contact"),
        MEMBERSHIP("membership"),
        PLANNER("my-planner"),
        BOOK_HOTEL("bedrooms"),
        ROOM_BOOKING("bedrooms/bookings/{id}"),
        HOUSES("houses/{id}"),
        FAQS("faqs"),
        FAQS_FRIENDS("faq/soho-friends-membership"),
        HOUSE_PAY_FAQS("faqs/house-pay"),
        NEW_PAYMENT_METHOD("settings/payment/add-method"),
        TERMS_AND_POLICIES("terms-and-policies"),
        TERMS_CONDITIONS("terms-and-policies/terms-and-conditions"),
        PRIVACY_POLICY("terms-and-policies/privacy-policy"),
        HOUSE_RULES("terms-and-policies/house-rules"),
        HOUSE_PAY_TERMS("terms-and-policies/house-pay"),
        BOOK_A_VISIT("whats-on/visits"),

        // Old urls
        ANALYTICS("policy/analytics"),
        HOUSE_TOUR("house/{id}/tours"),

        ELECTRIC_CINEMA("electriccinema"),

        STUDIO_SPACES("studio-spaces"),

        // temporary type for all other urls, if type is this we use the url that's passed in
        OTHERS("")
    }

    private val secureUrls = arrayOf(
        ANALYTICS,
        HOUSE_TOUR,
        BOOK_HOTEL,
        BOOK_A_VISIT,
        HOUSES,
        ELECTRIC_CINEMA,
        ROOM_BOOKING,
        FAQS,
        FAQS_FRIENDS,
        CONTACT_SUPPORT,
        TERMS_AND_POLICIES,
        TERMS_CONDITIONS
    )

    private const val SOHOWEB_QUERY_WEBVIEW = "webview"
    private const val DOTCOM_URL_SCHEME = "https"
    private val DOTCOM_URL_AUTHORITY =
        if (App.buildConfigManager.isStaging) {
            "dih-master.staging.sohohousedigital.com"
        } else {
            "www.sohohouse.com"
        }
    private val ELECTRIC_CINEMA_WEB_URL = "https://www.electriccinema.co.uk/?webview"

    private const val QUERY_LAYOUT = "layout"
    private const val LAYOUT_HEADLESS = "headless"
    private const val QUERY_THEME = "theme"
    const val THEME_DARK = "dark"
    const val THEME_LIGHT = "light"

    fun getWebViewFormatted(
        type: KickoutType,
        id: String? = null,
        url: String? = "",
        theme: String = THEME_DARK
    ): Uri {
        if (type == OTHERS && url != null) {
            return buildUri(url, theme)
        }

        val kickoutUrl = if (id != null) type.path.replaceBraces(id) else type.path
        return when (type) {
            ELECTRIC_CINEMA -> buildUri(ELECTRIC_CINEMA_WEB_URL, theme)
            ANALYTICS,
            HOUSE_TOUR -> buildUri(App.buildConfigManager.webHostName, kickoutUrl, null, theme)
            else -> buildUri(DOTCOM_URL_SCHEME, kickoutUrl, DOTCOM_URL_AUTHORITY, theme)
        }
    }

    private fun buildUri(
        scheme: String,
        kickoutUrl: String,
        authority: String? = null,
        theme: String
    ): Uri {
        return Uri.Builder().scheme(scheme)
            .authority(authority)
            .appendEncodedPath(kickoutUrl)
            .query(SOHOWEB_QUERY_WEBVIEW)
            .also { appendExtraQueries(it, theme) }
            .build()
    }

    private fun buildUri(url: String, theme: String): Uri {
        return Uri.parse(url).buildUpon()
            .also { appendExtraQueries(it, theme) }
            .build()
    }

    private fun appendExtraQueries(builder: Uri.Builder, theme: String) {
        builder.appendQueryParameter(QUERY_LAYOUT, LAYOUT_HEADLESS)
            .appendQueryParameter(QUERY_THEME, theme)
    }

    // temporary for the old url
    fun isSecureUrl(type: KickoutType?): Boolean {
        return secureUrls.contains(type)
    }
}