package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.common.extensions.addBlankIfEmpty
import com.sohohouse.seven.network.core.common.extensions.formatWithCommas
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.Meta
import com.sohohouse.seven.network.utils.LocalDateTimeUtil
import com.sohohouse.seven.network.utils.getApiFormattedDate
import com.squareup.moshi.Moshi
import moe.banana.jsonapi2.JsonBuffer
import org.threeten.bp.LocalDateTime
import retrofit2.Call

enum class EventTypeFilter(val filter: Array<String>) {
    EVENTS(arrayOf("MEMBER_EVENT")),
    CINEMA(arrayOf("SCREENING")),
    FITNESS(arrayOf("GYM_CLASS", "HOUSE_RIDE")),
    HOUSE_VISIT(arrayOf("HOUSE_VISIT"))
}

class GetEventsRequest(
    private val filterLocationID: Array<String>? = null,
    private val recommend: String? = null,
    private val filterParentLocationID: Array<String>? = null,
    private val filterType: Array<String>? = null,
    private val filterCategories: Array<String>? = null,
    private val isFeatured: Boolean? = null,
    private val filterState: Array<String>? = null,
    private val minTicketsAvailable: Int? = null,
    private val isPaid: Boolean? = null,
    private val venueDateFrom: String? = null,
    private val venueDateTo: String? = null,
    private val venueEndDateFrom: String? = null,
    private val venueEndDateTo: String? = null,
    private val endsAtFrom: String? = null,
    private val startsAtFrom: String? = null,
    private val startsAtTo: String? = null,
    override var page: Int? = null,
    override var perPage: Int? = null,
    private val includeResources: Array<String>? = null,
    private val filterDigitalEvents: Boolean? = null,
    private val sort: String? = null,
) : CoreAPIRequestPagable<List<Event>> {
    override fun createCall(api: CoreApi): Call<out List<Event>> {
        return api.getEvents(
            filterLocationID?.formatWithCommas(),
            recommend,
            filterParentLocationID?.formatWithCommas(),
            filterType?.formatWithCommas(),
            filterCategories?.formatWithCommas(),
            isFeatured,
            filterState?.formatWithCommas(),
            minTicketsAvailable,
            isPaid,
            venueDateFrom,
            venueDateTo,
            venueEndDateFrom,
            venueEndDateTo,
            endsAtFrom,
            startsAtFrom,
            startsAtTo,
            page,
            perPage,
            includeResources?.formatWithCommas(),
            filterDigitalEvents,
            sort)
    }

    companion object {
        private const val MAX_NUM_FEATURED_OR_HISTORY_EVENTS = 3
        private const val MAX_NUM_HAPPENING_NOW = 11
        const val MAX_OTHER_EVENTS_PER_CALL = 10
        private const val HISTORY_RECOMMENDED_FILTER = "HISTORY"
        private const val EVENT_INDUCTION_TYPE = "INDUCTION"
        private const val BOOKING_INCLUDE_TYPE = "booking"
        private const val EVENT_STATE_PUBLISHED = "PUBLISHED"
        private const val EVENT_STATE_OPEN = "OPEN_FOR_BOOKING"
        private const val RESOURCE_INCLUDE_TYPE = "resource"
        private const val RESOURCE_META_INCLUDE_TYPE = "resource.resource_meta"
        private const val FILM_INCLUDE_TYPE = "film"
        private const val INCLUDE_TYPE_VENUE = "venue"
        private const val SORT_START_AT_DESC = "-starts_at"

        /**
         * @param dynamicVenue the id of the dynamic house or null if the house is not open
         * @return All of today's events in the dynamicVenue if the house is open, or no events if the house is closed
         */
        fun getDynamicHouseEvents(dynamicVenue: String?, date: String) = GetEventsRequest(
            filterParentLocationID = if (dynamicVenue != null) arrayOf(dynamicVenue) else arrayOf(""),
            perPage = MAX_NUM_HAPPENING_NOW,
            includeResources = arrayOf(BOOKING_INCLUDE_TYPE,
                RESOURCE_INCLUDE_TYPE,
                RESOURCE_META_INCLUDE_TYPE,
                FILM_INCLUDE_TYPE),
            filterType = EventTypeFilter.EVENTS.filter + EventTypeFilter.CINEMA.filter + EventTypeFilter.FITNESS.filter + EventTypeFilter.HOUSE_VISIT.filter,
            venueDateFrom = date,
            venueDateTo = date,
            venueEndDateFrom = date,
            filterState = arrayOf(EVENT_STATE_PUBLISHED, EVENT_STATE_OPEN)
        )

        /**
         * @param locationFilters a list of all the user's favourite houses (can include the dynamic venue)
         * @param dynamicVenue the id of the dynamic house or null if the house is not open
         * @return All of today's events in all houses except in the dynamicVenue if the venue is open (not null),
         * or all events in all houses if the dynamicVenue is closed (null)
         */
        fun getHappeningNowEvents(
            locationFilters: List<String>,
            date: String,
            dynamicVenue: String?,
        ) = GetEventsRequest(
            filterParentLocationID = locationFilters.filter { it != dynamicVenue }.toTypedArray()
                .addBlankIfEmpty(),
            perPage = MAX_NUM_HAPPENING_NOW,
            includeResources = arrayOf(BOOKING_INCLUDE_TYPE,
                RESOURCE_INCLUDE_TYPE,
                RESOURCE_META_INCLUDE_TYPE,
                FILM_INCLUDE_TYPE),
            filterType = EventTypeFilter.EVENTS.filter + EventTypeFilter.CINEMA.filter + EventTypeFilter.FITNESS.filter,
            venueDateFrom = date,
            venueDateTo = date,
            venueEndDateFrom = date,
            filterState = arrayOf(EVENT_STATE_PUBLISHED, EVENT_STATE_OPEN)
        )

        fun getFeaturedEvents(
            eventTypeFilter: EventTypeFilter,
            locationFilters: Array<String>,
            startDate: String,
            endsAtFrom: String?,
        ): GetEventsRequest {
            return GetEventsRequest(isFeatured = true,
                filterParentLocationID = locationFilters,
                filterType = eventTypeFilter.filter,
                perPage = MAX_NUM_FEATURED_OR_HISTORY_EVENTS,
                venueDateFrom = startDate,
                includeResources = arrayOf(BOOKING_INCLUDE_TYPE,
                    RESOURCE_INCLUDE_TYPE,
                    RESOURCE_META_INCLUDE_TYPE,
                    FILM_INCLUDE_TYPE),
                endsAtFrom = endsAtFrom)
        }

        fun getHistoryEvents(
            locationFilters: Array<String>,
            startDate: String,
            endsAtFrom: String?,
        ): GetEventsRequest {
            return GetEventsRequest(recommend = HISTORY_RECOMMENDED_FILTER,
                filterParentLocationID = locationFilters,
                filterType = EventTypeFilter.EVENTS.filter,
                perPage = MAX_NUM_FEATURED_OR_HISTORY_EVENTS,
                venueDateFrom = startDate,
                includeResources = arrayOf(BOOKING_INCLUDE_TYPE,
                    RESOURCE_INCLUDE_TYPE,
                    RESOURCE_META_INCLUDE_TYPE,
                    FILM_INCLUDE_TYPE),
                endsAtFrom = endsAtFrom)
        }

        fun getAllEvents(
            eventTypeFilter: EventTypeFilter,
            locationFilters: Array<String>? = null,
            filterCategories: Array<String>? = null,
            startDate: String,
            endDate: String? = null,
            endsAtFrom: String?,
            page: Int? = null,
            perPage: Int? = null,
        ): GetEventsRequest {
            return GetEventsRequest(filterType = eventTypeFilter.filter,
                filterParentLocationID = locationFilters,
                filterCategories = filterCategories,
                venueDateFrom = startDate,
                venueDateTo = endDate,
                includeResources = arrayOf(BOOKING_INCLUDE_TYPE,
                    RESOURCE_INCLUDE_TYPE,
                    RESOURCE_META_INCLUDE_TYPE,
                    FILM_INCLUDE_TYPE),
                endsAtFrom = endsAtFrom,
                page = page,
                perPage = perPage)
        }

        fun getInductionEvents(
            locationFilters: Array<String>? = null,
            startsAtFrom: String,
            startsAtTo: String,
            endsAtFrom: String?,
        ): GetEventsRequest {
            return GetEventsRequest(filterLocationID = locationFilters,
                filterType = arrayOf(EVENT_INDUCTION_TYPE),
                filterState = arrayOf(EVENT_STATE_OPEN),
                minTicketsAvailable = 1,
                venueDateFrom = startsAtFrom,
                venueDateTo = startsAtTo,
                endsAtFrom = endsAtFrom)
        }

        fun getPastDigitalEvents(page: Int, perPage: Int = 10): GetEventsRequest {
            return GetEventsRequest(
                includeResources = arrayOf(BOOKING_INCLUDE_TYPE,
                    FILM_INCLUDE_TYPE,
                    RESOURCE_INCLUDE_TYPE,
                    RESOURCE_META_INCLUDE_TYPE,
                    INCLUDE_TYPE_VENUE),
                startsAtFrom = LocalDateTimeUtil.since1970().getApiFormattedDate(),
                startsAtTo = LocalDateTime.now().getApiFormattedDate(),
                filterDigitalEvents = true,
                sort = SORT_START_AT_DESC,
                page = page,
                perPage = perPage
            )
        }
    }

    override fun getMeta(response: List<Event>): Meta? {
        val adapter = Moshi.Builder().build().adapter(Meta::class.java)
        if (response.isNotEmpty() && response[0].document.meta != null) {
            @Suppress("UNCHECKED_CAST")
            return (response[0].document.meta as JsonBuffer<Meta>).get(adapter)
        }
        return null
    }
}
