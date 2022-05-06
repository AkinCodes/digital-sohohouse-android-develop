package com.sohohouse.seven.common.navigation

@Suppress("unused")
enum class NavigationTrigger(val value: String) {
    PROMOTED_TO_EVENT_GUESTLIST("booking_promoted_to_guest_list"),
    WON_EVENT_LOTTERY("booking_won_lottery"),
    EVENT_OPEN_FOR_BOOKING("event_open_for_booking"),
    NOTICEBOARD_POST_REPLY_RECEIVED("checkin_reply_received"),
    NOTICEBOARD_POST_REACTIONS_RECEIVED("checkin_reactions_created"),
    NEW_MESSAGE_INVITE("new_message_invite"),
    FIRST_ATTENDANCE("first_attendance"),
    TABLE_BOOKING_DETAILS("table_booking_details"),
    ATTENDANCE_STATUS_UPDATE("attendance_status_update"),
    MUTUAL_CONNECTION_REQUEST_CREATED("mutual_connection_request_created"),
    MUTUAL_CONNECTION_REQUEST_UPDATED("mutual_connection_request_updated");

    companion object {
        @JvmStatic
        fun from(name: String?): NavigationTrigger? {
            return values().firstOrNull { it.value == name }
        }
    }

    val notYetSupported: Boolean
        get() = when (this) {
            NOTICEBOARD_POST_REACTIONS_RECEIVED -> true
            else -> false
        }

    val isEventTrigger: Boolean
        get() = when (this) {
            PROMOTED_TO_EVENT_GUESTLIST -> true
            WON_EVENT_LOTTERY -> true
            EVENT_OPEN_FOR_BOOKING -> true
            else -> false
        }
}