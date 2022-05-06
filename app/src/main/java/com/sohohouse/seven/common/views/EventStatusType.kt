package com.sohohouse.seven.common.views

import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import com.sohohouse.seven.R

enum class EventStatusType(
    @StringRes var resString: Int,
    @AttrRes var attrColor: Int,
    @AttrRes var attrSecondColor: Int = 0,
    val showDot: Boolean = true
) {
    OPEN_FOR_BOOKING(
        R.string.explore_events_event_booking_open_label,
        R.attr.colorEventOpenBooking
    ),
    WAITING_LIST(R.string.explore_events_event_waiting_label, R.attr.colorEventWaitingList),
    FULLY_BOOKED(R.string.explore_events_event_booked_label, R.attr.colorEventFull),
    EVENT_CLOSED(R.string.explore_events_event_closed_label, R.attr.colorEventClosed),
    OPEN_SOON(R.string.explore_events_event_opens_label, R.attr.colorEventOpeningSoon),
    EVENT_PAST(R.string.explore_events_event_past_label, R.attr.colorEventPast),
    HOUSE_MEMBER_ONLY(
        R.string.explore_events_event_members_only_label,
        R.attr.colorEventMembersOnly
    ),
    EVENT_POSTPONED(R.string.explore_events_event_postponed_label, R.attr.colorEventPostponed),
    ACTIVE_MEMBERS_ONLY(
        R.string.active_members_only_label,
        R.attr.colorEventActiveMembersOnly,
        showDot = false
    ),
    LIVE_NOW(R.string.event_live_now,R.attr.colorEventLiveNowBkg, R.attr.colorEventLiveNow)
}