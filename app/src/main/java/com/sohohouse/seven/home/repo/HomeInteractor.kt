package com.sohohouse.seven.home.repo

import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.book.adapter.model.EventCarouselBuilder
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.prefs.PrefsManager
import com.sohohouse.seven.common.prefs.VenueAttendanceProvider
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.venue.VenueList
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.toolbar.Banner
import com.sohohouse.seven.connect.trafficlights.VenueMembers
import com.sohohouse.seven.connect.trafficlights.controlpanel.TrafficLightsControlPanel
import com.sohohouse.seven.connect.trafficlights.repo.TrafficLightsRepo
import com.sohohouse.seven.discover.benefits.BenefitsRepo
import com.sohohouse.seven.discover.housenotes.HouseNotesRepo
import com.sohohouse.seven.home.adapter.viewholders.BannerShortcut
import com.sohohouse.seven.home.completeyourprofile.SetUpAppPromptItemFactory
import com.sohohouse.seven.home.happeningnow.HappeningNowListFactory
import com.sohohouse.seven.home.housenotes.HouseNotesListFactory
import com.sohohouse.seven.home.suggested_people.SuggestedAdapterItem
import com.sohohouse.seven.home.suggested_people.SuggestedPeopleAdapterItem
import com.sohohouse.seven.home.suggested_people.getSuggestedPeopleAdapterItem
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.models.Perk
import com.sohohouse.seven.network.core.models.RecommendationDto
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.network.core.request.GetHouseNotesSitecoreRequest
import com.sohohouse.seven.network.core.request.GetPerksRequest
import com.sohohouse.seven.profile.ProfileRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

interface HomeInteractor {
    suspend fun getHomeItems(refresh: Boolean): Flow<List<DiffItem>>
    fun errors(): Flow<ServerError>
    fun loadingState(): StateFlow<LoadingState>
    suspend fun refreshHousePayBanner(): Banner?
}

class HomeInteractorImpl(
    private val userManager: UserManager,
    private val eventsRepo: EventsRepo,
    private val benefitsRepo: BenefitsRepo,
    private val profileRepo: ProfileRepository,
    private val houseNotesRepo: HouseNotesRepo,
    private val happeningNowFactory: HappeningNowListFactory,
    private val trafficLightsRepo: TrafficLightsRepo,
    private val prefsManager: PrefsManager,
    private val venueRepo: VenueRepo,
    private val venueAttendanceProvider: VenueAttendanceProvider,
    private val housePayBannerDelegate: HousePayBannerDelegate,
    private val ioDispatcher: CoroutineDispatcher,
    private val apiService: SohoApiService,
) : HomeInteractor {

    private val attendingVenue: Venue? get() = venueAttendanceProvider.attendingVenue
    private val _errors = MutableSharedFlow<ServerError?>()

    private val loadingCount = MutableStateFlow(0)
    private val loadingState: StateFlow<LoadingState> = loadingCount.mapState {
        if (it > 0) LoadingState.Loading else LoadingState.Idle
    }

    override suspend fun getHomeItems(refresh: Boolean): Flow<List<DiffItem>> {
        return buildItems(refresh)
    }

    override fun errors(): Flow<ServerError> {
        return _errors.filterNotNull()
    }

    override suspend fun refreshHousePayBanner(): Banner? {
        return housePayBannerDelegate.getHousePayBanner()
    }

    override fun loadingState(): StateFlow<LoadingState> {
        return loadingState
    }

    private suspend fun buildItems(refresh: Boolean): Flow<List<DiffItem>> {
        val sections = getSectionsForSubscription(prefsManager.subscriptionType)
        val fetchedVenues = if (refresh) {
            loadingCount.increment()
            venueRepo.fetchVenues().fold(
                ifError = { serverError ->
                    _errors.emit(serverError)
                    venueRepo.venues()
                },
                ifValue = { it },
                ifEmpty = {
                    venueRepo.venues()
                }
            ).also {
                loadingCount.decrement()
            }
        } else venueRepo.venues()

        val flows = ArrayList<Flow<List<DiffItem>>>().apply {
            sections.forEach { section ->
                when (section) {
                    HomeSection.HOUSE_PAY_BANNER -> add(housePayBannerDelegate.housePayBannerFlow())
                    HomeSection.DYNAMIC_HOUSE -> add(getDynamicHouseEventItems())
                    HomeSection.ROUNDEL_SHORTCUTS -> add(getBannerCarouselItem())
                    HomeSection.HAPPENING_NOW -> add(getHappeningNowEventItems(fetchedVenues))
                    HomeSection.SET_UP_APP -> add(getSetUpAppItems())
                    HomeSection.HOUSE_NOTES -> add(getHouseNotes())
                    HomeSection.BENEFITS -> add(getBenefitsItems())
                    HomeSection.HOUSES -> add(getHousesItem(prefsManager.subscriptionType))
                    //HomeSection.EVENTS_ON_DEMAND -> add(getMemberEventsOnDemand(fetchedVenues))
                    HomeSection.TRAFFIC_LIGHTS_CONTROL_PANEL -> add(getTrafficLightInfo())
                    HomeSection.SUGGESTED_CAROUSEL -> add(getSuggestedPeople())
                }
            }
        }.onEach {
            it.catch {
                loadingCount.decrement()
            }
        }

        return combine(flows) {
            it.reduce { left, right ->
                left + right
            }
        }.distinctUntilChanged()
    }

    private suspend fun getTrafficLightInfo(): Flow<List<TrafficLightsControlPanel>> =
        flow(initialValue = emptyList()) {
            loadingCount.increment()
            val items = withContext(ioDispatcher) {
                trafficLightsRepo.getVenueMembers(perPage = 10, isInitialLoad = false).fold(
                    ifError = {
                        _errors.emit(it)
                        emptyList()
                    },
                    ifEmpty = { emptyList() },
                    ifValue = { listOf(createTrafficLightsControlPanel(it)) }
                )
            }
            emit(items)
            loadingCount.decrement()
        }


    private fun createTrafficLightsControlPanel(venueMembers: VenueMembers): TrafficLightsControlPanel {
        val venue = venueRepo.venues().findById(venueMembers.venueID)
        return TrafficLightsControlPanel(
            venueMembers = venueMembers,
            venueName = venue?.name ?: "",
            availableStatus = userManager.availableStatusFlow.mapState { it.availableStatus },
            userImageUrl = venue?.house?.get(venue.document)?.houseImageSet?.mediumPng ?: "",
            threshold = 5,
            estimatedTotal = venueMembers.estimatedTotal
        )
    }

    private fun getBannerCarouselItem(): Flow<List<BaseAdapterItem.BannerCarouselItem>> {
        return flow(initialValue = emptyList()) {
            val item = if (prefsManager.subscriptionType == SubscriptionType.FRIENDS) {
                BaseAdapterItem.BannerCarouselItem(BannerShortcut.SHORTCUTS_FOR_FRIENDS)
            } else {
                BaseAdapterItem.BannerCarouselItem(BannerShortcut.SHORTCUTS_FOR_MEMBERS)
            }
            emit(listOf(item))
        }
    }

    private suspend fun getBenefitsItems(): Flow<List<BaseAdapterItem.DiscoverPerks>> {
        return flow(initialValue = emptyList()) {
            loadingCount.increment()
            val items = withContext(ioDispatcher) {
                benefitsRepo.getPerks(perPage = GetPerksRequest.HOME_PERKS_PER_PAGE).fold(
                    ifError = {
                        _errors.emit(it)
                        emptyList()
                    },
                    ifEmpty = { emptyList() },
                    ifValue = {
                        val items = it.map { perks ->
                            createPerksItem(perks)
                        }
                        listOf(BaseAdapterItem.DiscoverPerks(items))
                    }
                )
            }
            emit(items)
            loadingCount.decrement()
        }
    }

    private suspend fun getHappeningNowEventItems(fetchedVenues: VenueList): Flow<List<BaseAdapterItem.HappeningNowItem.Container>> {
        return flow(initialValue = emptyList()) {
            loadingCount.increment()
            val locationList = userManager.favouriteHouses.filterNot { it == attendingVenue?.id }
            val items = withContext(ioDispatcher) {
                eventsRepo.getHappeningNowEvents(
                    attendingVenue = attendingVenue,
                    locationList = locationList
                ).fold(
                    ifError = {
                        _errors.emit(it)
                        emptyList()
                    },
                    ifEmpty = { emptyList() },
                    ifValue = {
                        val item = happeningNowFactory.getUpcomingEvents(it, fetchedVenues)
                            ?: return@fold emptyList()
                        listOf(item)
                    }
                )
            }
            emit(items)
            loadingCount.decrement()
        }
    }

    private suspend fun getDynamicHouseEventItems(): Flow<List<BaseAdapterItem.HappeningNowItem.Container>> {
        return flow(initialValue = emptyList()) {
            loadingCount.increment()
            val items = withContext(ioDispatcher) {
                eventsRepo.getDynamicHouseEvents(attendingVenue).fold(
                    ifError = {
                        _errors.emit(it)
                        emptyList()
                    },
                    ifEmpty = { emptyList() },
                    ifValue = { events ->
                        val item = happeningNowFactory.getDynamicHouseEvents(
                            events,
                            attendingVenue
                        ) ?: return@fold emptyList()
                        listOf(item)
                    }
                )
            }
            emit(items)
            loadingCount.decrement()
        }
    }

    private fun getHouseNotes(): Flow<List<BaseAdapterItem.HouseNoteItem>> {
        return flow(initialValue = emptyList()) {
            loadingCount.increment()
            val items = withContext(ioDispatcher) {
                houseNotesRepo.getAll().fold(
                    ifError = {
                        _errors.emit(it)
                        emptyList()
                    },
                    ifEmpty = { emptyList() },
                    ifValue = { templates ->
                        val items = HouseNotesListFactory.getHomeHouseNotesList(templates)
                            .take(GetHouseNotesSitecoreRequest.MAX_RECENT_HOUSE_NOTES_HOME)
                        items
                    }
                )
            }
            emit(items)
            loadingCount.decrement()
        }

    }

    private fun getSetUpAppItems(): Flow<List<BaseAdapterItem.SetUpAppPromptItem.Container>> {
        return flow(initialValue = emptyList()) {
            loadingCount.increment()
            val items = withContext(ioDispatcher) {
                profileRepo.getMyProfile().fold(
                    ifError = {
                        _errors.emit(it)
                        emptyList()
                    },
                    ifEmpty = { emptyList() },
                    ifValue = { profile ->
                        val items = SetUpAppPromptItemFactory(profile, prefsManager).createItems()
                        if (items.isNotEmpty()) {
                            listOf(BaseAdapterItem.SetUpAppPromptItem.Container(items))
                        } else {
                            emptyList()
                        }
                    }
                )
            }
            emit(items)
            loadingCount.decrement()
        }
    }

    private suspend fun getMemberEventsOnDemand(fetchedVenues: VenueList): Flow<List<DiffItem>> {
        return flow(initialValue = emptyList()) {
            loadingCount.increment()
            val items = withContext(ioDispatcher) {
                eventsRepo.getMemberEventsOnDemand().fold(
                    ifValue = { events ->
                        EventCarouselBuilder.buildOnDemandEventCarousel(events, fetchedVenues)
                            ?: emptyList()
                    },
                    ifEmpty = { emptyList() },
                    ifError = {
                        _errors.emit(it)
                        emptyList()
                    }
                )
            }
            emit(items)
            loadingCount.decrement()
        }
    }

    private suspend fun getSuggestedPeople(): Flow<List<DiffItem>> = flow(emptyList()) {
        val isOptedIn = userManager.connectRecommendationOptIn.isNotEmpty()

        val suggestedPeople = if (isOptedIn) {
            val suggestedPeopleRequest = apiService.getRecommendations()
            if (suggestedPeopleRequest is ApiResponse.Success<List<RecommendationDto>>) {
                suggestedPeopleRequest.response.map {
                    it.getSuggestedPeopleAdapterItem()
                }
            } else {
                emptyList()
            }
        } else {
            listOf(
                SuggestedPeopleAdapterItem.Placeholder(R.drawable.img_place_holder),
                SuggestedPeopleAdapterItem.Placeholder(R.drawable.img_come_back_soon_3),
                SuggestedPeopleAdapterItem.Placeholder(R.drawable.img_come_back_soon_5)
            )
        }

        emit(
            listOf(SuggestedAdapterItem(isOptedIn, suggestedPeople))
        )
    }

    private fun createPerksItem(perks: Perk): BaseAdapterItem.DiscoverPerks.PerksItem {
        return BaseAdapterItem.DiscoverPerks.PerksItem(perks)
    }
}

