package com.sohohouse.seven.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.sohohouse.seven.R
import com.sohohouse.seven.base.error.ErrorHelper
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.LiveEvent
import com.sohohouse.seven.base.mvvm.postEvent
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.analytics.NavigationParams
import com.sohohouse.seven.common.deeplink.DeeplinkViewModel
import com.sohohouse.seven.common.prefs.LocalVenueProvider
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.DateUtils
import com.sohohouse.seven.common.utils.LocaleProvider
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.utils.UrlUtils
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.CustomGreetings
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.common.views.Period
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.home.houseboard.viewmodels.NotificationViewModel
import com.sohohouse.seven.network.chat.ChatChannelsRepo
import com.sohohouse.seven.network.chat.ChatConnectionRepo
import com.sohohouse.seven.network.chat.ChatUsersRepo
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.profile.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.net.URLConnection
import java.util.*
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection

class MainViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val venueRepo: VenueRepo,
    private val userManager: UserManager,
    private val localeProvider: LocaleProvider,
    private val localVenueProvider: LocalVenueProvider,
    private val chatUsersRepo: ChatUsersRepo,
    private val connectionRepository: ChatConnectionRepo,
    private val channelRepository: ChatChannelsRepo,
    private val profileRepository: ProfileRepository,
    private val dispatcher: CoroutineDispatcher,
    analyticsManager: AnalyticsManager,
    notificationViewModel: NotificationViewModel,
    deeplinkViewModel: DeeplinkViewModel
) : BaseViewModel(analyticsManager),
    DeeplinkViewModel by deeplinkViewModel,
    NotificationViewModel by notificationViewModel {

    // backing fields
    private val _profileImage = MutableLiveData<String>()

    private val _notificationDialog = LiveEvent<Any>()

    private val _headerData = MutableLiveData<HeaderData>()

    private val _dayOfMonth = LiveEvent<Int>()

    private val _unReadMessages = LiveEvent<Boolean>()

    private val dateChangeReceiver = DateChangeReceiver(::onDateChanged)

    // getters
    val profileImage: LiveData<String>
        get() = _profileImage

    val notificationDialog: LiveEvent<Any>
        get() = _notificationDialog

    val headerData: LiveData<HeaderData>
        get() = _headerData

    val dayOfMonth: LiveEvent<Int>
        get() = _dayOfMonth

    val unReadMessages: LiveEvent<Boolean>
        get() = _unReadMessages

    val bottomNavMenu: Int
        get() {
            return when (userManager.subscriptionType) {
                SubscriptionType.FRIENDS -> R.menu.bottom_nav_bar_friends
                else -> R.menu.bottom_nav_bar
            }
        }

    private val _sharedProfile = MutableSharedFlow<ProfileItem?>()
    val sharedProfile = _sharedProfile.asSharedFlow()

    private val _sharedProfileState = MutableSharedFlow<String>()
    val sharedProfileState = _sharedProfileState.asSharedFlow()

    val profileItem: ProfileItem
        get() = ProfileItem(
            id = userManager.profileID,
            firstName = userManager.profileFirstName,
            lastName = userManager.profileLastName,
            occupation = userManager.profileOccupation,
            location = userManager.profileLocation,
            imageUrl = userManager.profileImageURL,
            isMyself = true
        )

    init {
        localVenueProvider.localVenue.observeForever(::onLocalVenueFetched)
        if (userManager.subscriptionType != SubscriptionType.FRIENDS) {
            registerTokenOnSendBird()
            checkUnreadMessages()
        }
    }

    fun registerTokenOnSendBird() {
        viewModelScope.launch(viewModelContext) {
            connectionRepository.connect(userManager.getMiniProfileForSB())
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                connectionRepository.registerPushTokenForCurrentUser(token)
            }
            channelRepository.hasUnreadMessages()
                .collect {
                    withContext(Dispatchers.Main) {
                        _unReadMessages.value = it
                    }
                }
        }
    }

    private fun checkUnreadMessages() {
        viewModelScope.launch(viewModelContext) {
            connectionRepository.connect(userManager.getMiniProfileForSB())
            channelRepository.channels()
                .collect {
                    val unreadChannelsCount = it.count { channel ->
                        channel.isUnread
                    }
                    withContext(Dispatchers.Main) {
                        _unReadMessages.value = unreadChannelsCount > 0
                    }
                }
        }
    }

    fun fetch() {
        getProfileImage()
    }

    fun trackFilterEvent(eventType: EventType) {
        when (eventType) {
            EventType.MEMBER_EVENT -> analyticsManager.track(AnalyticsEvent.Explore.Events.FilterButtonClick)
            EventType.CINEMA_EVENT -> analyticsManager.track(AnalyticsEvent.Explore.Cinema.FilterButtonClick)
            EventType.FITNESS_EVENT -> analyticsManager.track(AnalyticsEvent.Explore.Fitness.FilterButtonClick)
            EventType.HOUSE_VISIT -> analyticsManager.track(AnalyticsEvent.Explore.HouseVisit.FilterButtonClick)
        }
    }

    fun trackTabSelected(tab: NavigationParams.Tab) {
        analyticsManager.logEventAction(
            action = AnalyticsManager.Action.TabSelected,
            params = NavigationParams.withTabType(tab)
        )
    }

    fun registerDateChangeReceiver(context: Context) {
        dateChangeReceiver.register(context)
    }

    fun unregisterDateChangeReceiver(context: Context) {
        dateChangeReceiver.unregister(context)
    }

    private fun onDateChanged(dayOfMonth: Int) {
        _dayOfMonth.value = dayOfMonth
    }

    private fun onLocalVenueFetched(venue: Venue) {
        _headerData.postValue(createHeaderData(venue, getGreetings(venue)))
    }

    private fun getProfileImage() {
        _profileImage.postValue(userManager.profileImageURL)
    }

    private fun tryNotificationDialog() {
        if (!userManager.isNotificationDialogComplete) {
            userManager.isNotificationDialogComplete = true
            _notificationDialog.postEvent()
        }
    }

    private fun getGreetings(venue: Venue?): Period {
        if (venue != null) {
            return CustomGreetings.getCustomGreeting(
                venue.id,
                localeProvider.getLocale(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            )
        }

        return Period(
            DateUtils.getWelcomeHeaderRes(
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            ), null
        )
    }

    private fun createHeaderData(venue: Venue, period: Period): HeaderData {
        return HeaderData(
            title = stringProvider.getString(period.title),
            subtitle = userManager.profileFirstName,
            imageUrl = venue.venueIcons.lightPng,
            emoji = stringProvider.getString(period.emoji)
        )
    }

    fun logBannerClick() {
        analyticsManager.logEventAction(AnalyticsManager.Action.HouseBoardBannerTapped)
    }

    fun onHouseBoardViewed() {
        markFirstNotifAsRead()
    }

    fun loadProfile(shortProfileUrl: String) {
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            val longUrl = loadLongUrl(shortProfileUrl)
            val lastSlashIndex = longUrl.lastIndexOf("/") + 1
            val profileId = longUrl.substring(lastSlashIndex)
            profileRepository.getProfile(profileId).ifValue {
                _sharedProfile.emit(ProfileItem(it))
            }.ifError {
                _sharedProfileState.emit(ErrorHelper.ERROR_LOADING_SHARED_PROFILE)
            }.ifEmpty {
                _sharedProfileState.emit(ErrorHelper.EMPTY_PROFILE)
            }
        }
    }

    private fun loadLongUrl(shortUrl: String): String {
        var result = ""
        val url = URL(shortUrl)
        val urlConn: URLConnection = url.openConnection()
        val httpsConn: HttpsURLConnection = urlConn as HttpsURLConnection
        httpsConn.instanceFollowRedirects = false
        httpsConn.requestMethod = UrlUtils.GET
        httpsConn.connect()
        if (httpsConn.responseCode == 301 || httpsConn.responseCode == 302) {
            result = httpsConn.getHeaderField(UrlUtils.LOCATION_HEADER_FIELD)
        }
        httpsConn.disconnect()
        return result
    }

}

data class HeaderData(
    val title: String,
    val subtitle: String,
    val imageUrl: String?,
    val emoji: String?
)