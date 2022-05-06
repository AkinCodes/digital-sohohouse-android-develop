package com.sohohouse.seven.common.views

import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.book.adapter.model.*
import com.sohohouse.seven.common.extensions.getLocationColor
import com.sohohouse.seven.common.extensions.isValidAndCwh
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.discover.benefits.adapter.PerksItem
import com.sohohouse.seven.discover.perks.BenefitContentPillar
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.EventCategory
import com.sohohouse.seven.network.core.models.Perk
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.perks.landing.adapter.PerksErrorItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExploreListFactory @Inject constructor(private val eventStatusHelper: EventStatusHelper) {

    fun createExploreCinemaItems(
        featuredList: List<Event> = emptyList(),
        screeningList: List<Event> = emptyList(),
        venues: VenueList,
        isFiltered: Boolean = false
    ): MutableList<DiffItem> {
        return createExploreItems(
            featuredList = featuredList,
            venues = venues,
            isFiltered = isFiltered,
            allList = screeningList
        )
    }

    fun createExploreFitnessItems(
        featuredList: List<Event> = emptyList(),
        allFitnessList: List<Event> = emptyList(),
        venues: VenueList,
        isFiltered: Boolean = false
    ): MutableList<DiffItem> {
        return createExploreItems(
            featuredList = featuredList,
            venues = venues,
            isFiltered = isFiltered,
            allList = allFitnessList
        )
    }

    fun createExploreEventsItems(
        featuredList: List<Event> = emptyList(),
        venues: VenueList,
        isFiltered: Boolean = false,
        allList: List<Event> = emptyList(),
        categories: List<EventCategory> = emptyList(),
        digitalEvents: List<Event> = emptyList()
    ): MutableList<DiffItem> {
        return createExploreItems(
            featuredList,
            venues,
            isFiltered,
            allList,
            categories,
            digitalEvents
        )
    }

    fun createHouseVisitItems(
        featuredList: List<Event> = emptyList(),
        screeningList: List<Event> = emptyList(),
        venues: VenueList,
        isFiltered: Boolean = false
    ): MutableList<DiffItem> {
        return createExploreItems(
            featuredList = featuredList,
            venues = venues,
            isFiltered = isFiltered,
            allList = screeningList
        )
    }

    fun createExplorePerksItems(
        perks: List<Perk>,
        venues: List<Venue>,
        isFiltered: Boolean = false
    ): MutableList<DiffItem> {
        return createPerkExploreItems(perks, venues, isFiltered = isFiltered)
    }

    fun createPerksItem(
        perks: List<Perk>,
        venues: List<Venue>
    ): MutableList<PerksItem> {
        val items = mutableListOf<PerksItem>()
        perks.iterator().forEach {
            it.isValidAndCwh(venues,
                onSuccess = { perk, venueName ->
                    items.add(
                        PerksItem(
                            id = perk.id,
                            title = perk.title,
                            imageUrl = perk.headerImageLarge,
                            city = perk.city,
                            description = perk.summary,
                            promoCode = perk.promotionCode,
                            contentPillar = BenefitContentPillar.forId(
                                perk.contentPillar ?: ""
                            )?.label,
                            expiry = perk.expiresOn
                        )
                    )
                },
                onNotCwh = { perk ->
                    items.add(
                        PerksItem(
                            id = perk.id,
                            title = perk.title,
                            description = perk.summary,
                            imageUrl = perk.headerImageLarge,
                            city = perk.city,
                            promoCode = perk.promotionCode,
                            contentPillar = BenefitContentPillar.forId(
                                perk.contentPillar ?: ""
                            )?.label,
                            expiry = perk.expiresOn
                        )
                    )
                }
            )
        }
        return items
    }


    private fun createPerkExploreItems(
        perksList: List<Perk>,
        venues: List<Venue>,
        isFiltered: Boolean
    ): MutableList<DiffItem> {

        val data: MutableList<DiffItem> = ArrayList()

        if (isFiltered) data.add(
            FilterStateHeaderAdapterItem(
                R.string.perks_filtered_header,
                isFiltered
            )
        )

        data.addAll(createPerkAdapterItems(perksList, venues))

        return data

    }

    private fun createExploreItems(
        featuredList: List<Event> = emptyList(),
        venues: VenueList,
        isFiltered: Boolean,
        allList: List<Event> = emptyList(),
        categories: List<EventCategory> = emptyList(),
        digitalEvents: List<Event> = emptyList()
    ): MutableList<DiffItem> {
        val data: MutableList<DiffItem> = ArrayList()

        if (isFiltered && (featuredList.isNotEmpty() || allList.isNotEmpty())) {
            data.add(
                FilterStateHeaderAdapterItem(
                    titleRes = R.string.events_filtered_results,
                    isFiltered = isFiltered
                )
            )
        }

        if (featuredList.isNotEmpty()) {
            //add 1 featured event if present
            data.add(createFeatureEventAdapterItem(featuredList.first(), venues, categories))
            //EventCarouselBuilder.buildOnDemandEventCarousel(digitalEvents, venues)?.let { data.addAll(it) }
            //add all list events
            data.addAll(allList.map { event ->
                createEventExploreAdapterItem(
                    event,
                    venues,
                    categories
                )
            })
        } else {
            //if no featured events add soonest open event as "featured event" (full bleed)
            var featuredEvent: Event? = null
            allList.firstOrNull { event -> isOpenForBooking(event, venues) }
                ?.let { soonestOpenEvent ->
                    data.add(createFeatureEventAdapterItem(soonestOpenEvent, venues, categories))
                    featuredEvent = soonestOpenEvent
                }
            //EventCarouselBuilder.buildOnDemandEventCarousel(digitalEvents, venues)?.let { data.addAll(it) }
            //add remaining list events
            if (allList.size > 1) {
                data.addAll(allList.filter { it != featuredEvent }
                    .map { event -> createEventExploreAdapterItem(event, venues, categories) })
            }
        }

        //if all three lists are empty
        if (featuredList.isEmpty() && allList.isEmpty()) {
            when {
                isFiltered -> data.add(
                    ZeroStateAdapterItem(
                        R.string.explore_events_filter_empty_header,
                        R.string.explore_events_filter_empty_supporting
                    )
                )
                else -> data.add(
                    ZeroStateAdapterItem(
                        R.string.explore_events_house_empty_header,
                        R.string.explore_events_house_empty_supporting
                    )
                )
            }
        }
        return data
    }

    private fun isOpenForBooking(it: Event, venues: VenueList) =
        eventStatusHelper.getRestrictedEventStatus(
            it,
            venues.findById(it.venue?.get()?.id)
        ) == EventStatusType.OPEN_FOR_BOOKING

    private fun createPerkAdapterItems(perksList: List<Perk>, venues: List<Venue>): List<DiffItem> {
        val adapterData = mutableListOf<DiffItem>()
        perksList.iterator().forEach {
            it.isValidAndCwh(venues,
                onSuccess = { relatedPerk, venueName ->
                    adapterData.add(
                        com.sohohouse.seven.perks.landing.adapter.PerksItem(
                            relatedPerk,
                            venueName
                        )
                    )
                },
                onNotCwh = { perk ->
                    adapterData.add(com.sohohouse.seven.perks.landing.adapter.PerksItem(perk))
                },
                onInvalid = {
                    adapterData.add(PerksErrorItem(it))
                }
            )
        }
        return adapterData
    }

    fun createErrorItems(): MutableList<DiffItem> {
        return arrayListOf(ErrorStateAdapterItem)
    }

    fun createEmptyState(isFiltered: Boolean): MutableList<DiffItem> {
        return arrayListOf(
            FilterStateHeaderAdapterItem(
                titleRes = R.string.events_filtered_results,
                isFiltered = isFiltered
            )
        )
    }

    private fun createEventExploreAdapterItem(
        event: Event,
        venueList: VenueList,
        categories: List<EventCategory>
    ): DiffItem {
        val venue = venueList.findById(event.venue?.get()?.id)
        val parentVenue = venueList.findById(venue?.parentId)
        val eventStatus = eventStatusHelper.getRestrictedEventStatus(event, venue)
        val category = getCategory(event, categories)
        return ListEvent(
            event = event,
            venueName = parentVenue?.name ?: venue?.name ?: "",
            venueColor = venue?.getLocationColor() ?: "",
            venueTimeZone = venue?.timeZone,
            eventStatus = eventStatus,
            categoryName = category?.name,
            categoryUrl = category?.icon?.png
        )
    }


    private fun createFeatureEventAdapterItem(
        event: Event,
        venueList: VenueList,
        categories: List<EventCategory>
    ): DiffItem {
        val venue = venueList.findById(event.venue?.get()?.id)
        val parentVenue = venueList.findById(venue?.parentId)
        val eventStatus = eventStatusHelper.getRestrictedEventStatus(event, venue)
        val category = getCategory(event, categories)
        return FeatureEvent(
            event = event,
            venueName = parentVenue?.name ?: venue?.name ?: "",
            venueColor = venue?.getLocationColor() ?: "",
            venueTimeZone = venue?.timeZone,
            eventStatus = eventStatus,
            categoryName = category?.name,
            categoryUrl = category?.icon?.png
        )
    }

    private fun getCategory(event: Event, categories: List<EventCategory>): EventCategory? {
        return categories.firstOrNull { it.id == event.category }
    }
}
