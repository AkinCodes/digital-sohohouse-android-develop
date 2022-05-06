package com.sohohouse.seven.common.views

import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import com.sohohouse.seven.R
import com.sohohouse.seven.network.core.request.EventTypeFilter

enum class EventType(
    val typeFilter: EventTypeFilter,
    @AttrRes val color: Int,
    @StringRes val label: Int
) {
    HOUSE_VISIT(
        EventTypeFilter.HOUSE_VISIT,
        R.attr.colorEventTypeHouseVisit,
        R.string.book_a_visit
    ),
    MEMBER_EVENT(EventTypeFilter.EVENTS, R.attr.colorEventTypeMember, R.string.event_type_event),
    CINEMA_EVENT(
        EventTypeFilter.CINEMA,
        R.attr.colorEventTypeCinema,
        R.string.event_type_screening
    ),
    FITNESS_EVENT(EventTypeFilter.FITNESS, R.attr.colorEventTypeFitness, R.string.event_type_class);

    fun isMemberEvent() = this == MEMBER_EVENT
    fun isCinemaEvent() = this == CINEMA_EVENT
    fun isFitnessEvent() = this == FITNESS_EVENT
    fun isHouseVisitEvent() = this == HOUSE_VISIT

    companion object {
        fun get(name: String) =
            values().find { it.typeFilter.filter.contains(name) } ?: MEMBER_EVENT
    }
}