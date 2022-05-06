package com.sohohouse.seven.home

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.error.DisplayableError
import com.sohohouse.seven.base.error.ErrorHelper
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.book.BookTab
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.analytics.AnalyticsEvent.Home
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder
import com.sohohouse.seven.common.deeplink.DeeplinkRepo
import com.sohohouse.seven.common.navigation.NavigationScreen
import com.sohohouse.seven.common.prefs.PrefsManager
import com.sohohouse.seven.common.prefs.VenueAttendanceProvider
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import com.sohohouse.seven.common.views.toolbar.Banner
import com.sohohouse.seven.home.repo.HomeInteractor
import com.sohohouse.seven.home.repo.HousePayBannerDelegateImpl
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.profile.MutualConnectionStatus
import com.sohohouse.seven.profile.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val interactor: HomeInteractor,
    private val userManager: UserManager,
    private val deeplinkRepo: DeeplinkRepo,
    private val venueAttendanceProvider: VenueAttendanceProvider,
    private val profileRepository: ProfileRepository,
    private val connectionRepository: ConnectionRepository,
    private val ioDispatcher: CoroutineDispatcher,
    val stringProvider: StringProvider,
    private val prefsManager: PrefsManager,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    ErrorViewStateViewModel by ErrorViewStateViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    private val isOptedIn: Boolean
        get() = userManager.connectRecommendationOptIn.isEmpty()

    val screeningTab: BookTab
        get() = if (userManager.subscriptionType == SubscriptionType.FRIENDS) BookTab.ELECTRIC_CINEMA else BookTab.SCREENING

    val items: LiveData<List<DiffItem>>
        get() = _items
    private val _items = MutableLiveData<List<DiffItem>>()

    val profileUrl: LiveData<String>
        get() = _profileUrl
    private val _profileUrl = MutableLiveData<String>()

    val events = LiveEvent<Event>()
    val openProfile = LiveEvent<ProfileItem>()

    val listLoading = interactor.loadingState().asLiveData()

    private var lastOptInState: Boolean? = null

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            getHomeItems()
        }
        viewModelScope.launch(viewModelContext) {
            interactor.errors().map {
                ErrorHelper.getErrorMessage(
                    errorCodes = (it as? ServerError.ApiError?)?.errorCodes ?: emptyArray(),
                    stringProvider = stringProvider
                )
            }.collect {
                events.postValue(Event.ShowErrorSnackbar(it))
            }
        }
    }

    fun refreshHomeItems() {
        getHomeItems(refresh = true)
    }

    fun getHomeItems(refresh: Boolean = false) {
        viewModelScope.launch(coroutineExceptionHandler) {
            ensureActive()
            interactor.getHomeItems(refresh).collect {
                onListItemsFetched(it)
            }
        }
    }

    private fun onListItemsFetched(either: List<DiffItem>) {
        _items.postValue(either)
        _profileUrl.postValue(userManager.profileImageURL)
        showWelcomeModal()
    }

    private fun showWelcomeModal() {
        if (venueAttendanceProvider.isAttending
            && userManager.attendedVenueId != venueAttendanceProvider.venueAttendanceId
            && venueAttendanceProvider.isFirstVenueVisit
        ) {
            userManager.attendedVenueId = venueAttendanceProvider.venueAttendanceId
        }
    }

    fun onHouseNotesClicked() {
        analyticsManager.logEventAction(AnalyticsManager.Action.HomeHouseNotesSeeAll)
        navigateToHouseNotes()
    }

    private fun navigateToHouseNotes() {
        deeplink(NavigationScreen.DISCOVER_HOUSE_NOTES)
    }

    fun onTableBooking() {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.HomeQuickAccess,
            Bundle().apply { putString("item", "book_table") })
    }

    fun onHouseNoteClicked(id: String) {
        analyticsManager.track(Home.HouseNotes(id))
    }

    fun onPerksClicked() {
        sendAnalytics(Home.FooterPerksSeeAll())
        deeplink(NavigationScreen.DISCOVER_PERKS)
    }

    private fun sendAnalytics(analytics: AnalyticsEvent) {
        analyticsManager.track(analytics)
    }

    private fun deeplink(screen: NavigationScreen) {
        val uri = DeeplinkBuilder.buildUri(screen)
        deeplinkRepo.put(uri)
        events.postValue(Event.LaunchIntent(Intent(Intent.ACTION_VIEW, uri)))
    }

    fun onSeeAllHousesClick() {
        analyticsManager.logEventAction(AnalyticsManager.Action.HomeHousesSeeAll)
        deeplink(NavigationScreen.DISCOVER_HOUSES)
    }

    fun onHousesImageClick() {
        analyticsManager.logEventAction(AnalyticsManager.Action.HomeHousesClickImage)
        deeplink(NavigationScreen.DISCOVER_HOUSES)
    }

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.Home.name)
    }

    fun onCompleteYourProfileItemClick() {
        analyticsManager.logEventAction(AnalyticsManager.Action.EditProfileHome)
    }

    fun onSeeAllPerkButtonClicked() {
        deeplink(NavigationScreen.DISCOVER_PERKS)
    }

    fun trackEventPerksItem(id: String, title: String?, promoCode: String?) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.HomeOpenPerks,
            AnalyticsManager.Perks.getParams(
                id = id,
                title = title,
                promoCode = promoCode,
                membershipType = userManager.membershipType,
                subscriptionType = userManager.subscriptionType.name
            )
        )
    }

    override fun reloadDataAfterError() {
        getHomeItems(refresh = true)
    }

    fun openProfileById(userId: String) {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            setLoadingState(LoadingState.Loading)
            profileRepository.getProfile(userId)
                .ifValue {
                    val profile = ProfileItem(
                        it, MutualConnectionStatus.from(
                            it,
                            userManager.profileID,
                            connectionRepository.blockedMembers.value
                        ) // TODO , showMessageButton = false
                    )
                    openProfile.postValue(profile)
                }.ifError {
                    showError(stringProvider.getString(R.string.internet_error_header))
                }
            setLoadingState(LoadingState.Idle)
        }
    }

    fun fixOptInState() {
        lastOptInState = isOptedIn
    }

    fun checkForOptInStateChanges() {
        if (lastOptInState != null && lastOptInState != isOptedIn)
            refreshHomeItems()
    }

    fun onBannerClick(banner: Banner) {
        when (banner.id) {
            HousePayBannerDelegateImpl.TYPE_OPEN_CHECK -> {
                events.postValue(Event.ViewOpenCheck(banner.checkId ?: ""))
            }
            HousePayBannerDelegateImpl.TYPE_RECENTLY_COSED_CHECK -> {
                events.postValue(Event.ViewCheckReceipt(banner.checkId ?: ""))
            }
        }
    }

    fun refreshHousePayBanner() {
        if (loadingState.value == LoadingState.Loading) {
            return  // refreshing the house pay banner is redundant if all items are loading
        }

        viewModelScope.launch(coroutineExceptionHandler) {
            interactor.refreshHousePayBanner()?.let { banner ->
                val newItems = _items.value?.toMutableList()?.apply {
                    if (firstOrNull() is Banner) {
                        set(0, banner)
                    } else {
                        add(0, banner)
                    }
                }
                _items.postValue(
                    newItems
                )
            }
        }
    }

    fun onBannerDismiss(banner: Banner) {
        _items.value = _items.value?.toMutableList()?.apply {
            remove(banner)
        }
        if (banner.id == HousePayBannerDelegateImpl.TYPE_RECENTLY_COSED_CHECK) {
            banner.checkId?.let { checkId ->
                prefsManager.addDismissedCheckClosedId(checkId)
            }
        }
    }

    sealed class Event {
        data class LaunchIntent(val intent: Intent) : Event()
        data class ViewCheckReceipt(val checkId: String) : Event()
        data class ShowErrorSnackbar(val error: DisplayableError) : Event()
        data class ViewOpenCheck(val checkId: String) : Event()
    }

}