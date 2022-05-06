package com.sohohouse.seven.common.navigation

import android.annotation.SuppressLint

enum class NavigationScreen(val value: String) {
    EVENT_DETAIL("EventDetails"),
    HOME("Home"),
    EVENTS("Events"),
    PLANNER("Planner"),
    ATTENDANCE_STATUS_UPDATE("AttendanceStatusUpdate"),
    CHECK_DETAIL("CheckDetails"),
    EVENT_BOOKING_DETAIL("EventBookingDetails"),
    EVENT_STATUS("EventStatus"),
    CHECK_RECEIPT("CheckReceipt"),
    DISCOVER("DISCOVER"),
    DISCOVER_HOUSE_NOTES("HouseNotes"),
    DISCOVER_HOUSES("Houses"),
    DISCOVER_PERKS("MembersBenefitsListView"),
    NOTICEBOARD_POST_DETAILS("CheckinReply"),
    CONNECTIONS_LIST("ConnectionList"),
    TABLE_BOOKING_DETAILS("TableBookingDetails"),
    MESSAGES("Messages"),
    NEW_MESSAGE_INVITE("NewMessageInvite");

    companion object {
        @SuppressLint("DefaultLocale")
        @JvmStatic
        fun from(name: String?): NavigationScreen? {
            with(name?.toLowerCase()) {
                return values().firstOrNull { it.value.toLowerCase() == this }
            }
        }
    }

    val isEventsScreen: Boolean
        get() = when (this) {
            EVENT_DETAIL,
            EVENTS,
            EVENT_BOOKING_DETAIL,
            EVENT_STATUS -> true
            else -> false
        }
}
