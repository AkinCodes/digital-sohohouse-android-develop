package com.sohohouse.seven.common.deeplink

import android.net.Uri
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.getAs
import com.sohohouse.seven.common.navigation.NavigationScreen

object DeeplinkBuilder {

    const val CORE_URL = "sohohouse.com"

    //schemes
    const val APPS_SCHEME = "apps"
    const val HTTPS_SCHEME = "https"
    const val HTTP_SCHEME = "http"

    //authorities
    const val AUTHORITY = "www.sohohouse.com"
    const val AUTHORITY_CLICK = "click.em.sohohouse.com"
    const val APP_AUTHORITY = "members.sohohouse.com"
    const val AUTHORITY_PROFILE = "sh.app"

    //paths
    const val PATH_NOTE = "notes"
    const val PATH_EVENT_DETAIL = "whats-on/details"
    const val PATH_HOME = "home"
    const val PATH_HOME_ATTENDANCE_STATUS_UPDATE = "attendance_status_update"
    const val PATH_EVENTS = "events"
    const val PATH_PLANNER = "planner"
    const val PATH_EVENT_BOOKING_DETAILS = "event/booking"
    const val PATH_EVENT_STATUS = "event/status"
    const val PATH_PROFILE = "profile"
    const val PATH_MY_PROFILE = "myprofile"
    const val PATH_NOTICEABORD_POST_DETAILS = "posts"
    const val PATH_CONNECTIONS = "connections"
    const val TABLE_BOOKING_DETAILS = "table_booking_details"
    const val PATH_DISCOVER = "discover"
    const val PATH_DISCOVER_NOTE = "discover/notes"
    const val PATH_DISCOVER_HOUSES = "discover/houses"
    const val PATH_DISCOVER_PERKS = "discover/member-benefits"
    const val PATH_MEMBER_BENEFITS = "member-benefits"
    const val PATH_MESSAGE = "message"
    const val PATH_CHAT = "chat"

    private val ALL_PATHS = arrayOf(
        PATH_EVENT_DETAIL,
        PATH_HOME,
        PATH_EVENTS,
        PATH_NOTE,
        PATH_PROFILE,
        PATH_MY_PROFILE,
        PATH_PLANNER,
        PATH_EVENT_BOOKING_DETAILS,
        PATH_EVENT_STATUS,
        PATH_DISCOVER,
        PATH_DISCOVER_NOTE,
        PATH_DISCOVER_HOUSES,
        PATH_DISCOVER_PERKS,
        PATH_NOTICEABORD_POST_DETAILS,
        PATH_CONNECTIONS,
        PATH_HOME_ATTENDANCE_STATUS_UPDATE,
        PATH_MESSAGE,
        PATH_CHAT
    )

    @JvmStatic
    fun buildUri(
        screen: NavigationScreen?,
        navigationResourceId: String? = null,
        navigationScreen: String? = null,
        navigationTrigger: String? = null
    ): Uri? {
        val builder = Uri.Builder().scheme(APPS_SCHEME).authority(APP_AUTHORITY)

        when (screen) {
            NavigationScreen.HOME -> builder.path(PATH_HOME)
            NavigationScreen.ATTENDANCE_STATUS_UPDATE -> builder.path(
                PATH_HOME_ATTENDANCE_STATUS_UPDATE
            )
            NavigationScreen.PLANNER -> builder.path(PATH_PLANNER)
            NavigationScreen.EVENTS -> builder.path(PATH_EVENTS)
            NavigationScreen.EVENT_DETAIL -> builder.path(PATH_EVENT_DETAIL)
            NavigationScreen.EVENT_BOOKING_DETAIL -> builder.path(PATH_EVENT_BOOKING_DETAILS)
            NavigationScreen.EVENT_STATUS -> builder.path(PATH_EVENT_STATUS)
            NavigationScreen.DISCOVER_HOUSE_NOTES -> builder.path(PATH_DISCOVER_NOTE)
            NavigationScreen.DISCOVER_HOUSES -> builder.path(PATH_DISCOVER_HOUSES)
            NavigationScreen.DISCOVER_PERKS -> builder.path(PATH_DISCOVER_PERKS)
            NavigationScreen.NOTICEBOARD_POST_DETAILS -> builder.path(PATH_NOTICEABORD_POST_DETAILS)
            NavigationScreen.CONNECTIONS_LIST -> builder.path(PATH_CONNECTIONS)
            NavigationScreen.TABLE_BOOKING_DETAILS -> builder.path(TABLE_BOOKING_DETAILS)
            NavigationScreen.MESSAGES -> builder.path(PATH_MESSAGE)
            NavigationScreen.NEW_MESSAGE_INVITE -> builder.path(PATH_CHAT)
            else -> return null
        }

        return builder
            .appendQueryParameter(BundleKeys.ID, navigationResourceId)
            .appendQueryParameter(BundleKeys.NAVIGATION_SCREEN, navigationScreen)
            .appendQueryParameter(BundleKeys.NAVIGATION_TRIGGER, navigationTrigger)
            .build()
    }

    fun makeDeepLinkable(url: String): String {
        var newUrl = url
        if (url.contains(CORE_URL) && ALL_PATHS.any { it in url }) {
            if ((url.startsWith(HTTPS_SCHEME))) {
                newUrl = url.replaceFirst(HTTPS_SCHEME, APPS_SCHEME)
            } else if (url.startsWith(HTTP_SCHEME)) {
                newUrl = url.replaceFirst(HTTP_SCHEME, APPS_SCHEME)
            }
        }
        return newUrl
    }

    fun buildUri(data: Map<String, Any?>?): Uri? {
        data ?: return null

        val trigger = data.getAs<String>(BundleKeys.NOTIFICATION_TRIGGER)
        val screenName = data.getAs<String>(BundleKeys.NOTIFICATION_SCREEN_NAME)
        val id = data.getAs<String>(BundleKeys.ID)

        if (trigger.isNullOrEmpty()
            && screenName.isNullOrEmpty()
        ) {
            return null
        }

        return buildUri(
            NavigationScreen.from(screenName),
            id,
            screenName,
            trigger
        )
    }

}