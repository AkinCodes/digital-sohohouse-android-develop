package com.sohohouse.seven.more.notifications

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState.Idle
import com.sohohouse.seven.base.mvvm.LoadingState.Loading
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.tag
import com.sohohouse.seven.common.extensions.updateAllTags
import com.sohohouse.seven.common.prefs.PrefsManager
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserSessionManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.more.notifications.recyclerview.*
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.models.CommunicationPreference
import com.sohohouse.seven.network.core.models.DeviceNotificationPreferences
import com.sohohouse.seven.network.core.request.GetCommunicationPreferenceRequest
import com.sohohouse.seven.network.core.request.GetDeviceNotificationPreferencesRequest
import com.sohohouse.seven.network.core.request.PatchCommunicationPreferenceRequest
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MoreNotificationsViewModel @Inject constructor(
    private val zipRequestsUtil: ZipRequestsUtil,
    val userSessionManager: UserSessionManager,
    private val firebaseEventTracking: AnalyticsManager,
    private val prefsManager: PrefsManager,
    private val apiService: SohoApiService,
    private val stringProvider: StringProvider,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    companion object {
        private const val DIGITAL_HOUSE = "digital_house"
        private const val PUSH_MARKETING = "marketing"

        private const val MEMBERSHIP = "membership"
        private const val PUSH_LOCAL_UPDATE = "local_update"

        private const val SOHO_HOUSE = "soho_house"
        private const val PUSH_AFFILIATE = "affiliate"

        private const val ALL_OPT_OUT = "all_opt_out"
    }

    private val _items: MutableLiveData<List<MoreNotificationsAdapterItem>> = MutableLiveData()
    val items: LiveData<List<MoreNotificationsAdapterItem>>
        get() = _items

    private lateinit var prefs: List<DeviceNotificationPreferences>
    private lateinit var emailMap: MutableMap<String, Boolean>

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.NotificationPreferences.name)
    }

    @SuppressLint("CheckResult")
    fun fetchCommunicationPrefs(notificationsEnabled: Boolean) {
        var digitalHouse = false
        var membership = false
        var sohoHouse = false
        zipRequestsUtil.issueApiCall(GetCommunicationPreferenceRequest())
            .flatMap { either ->
                when (either) {
                    is Either.Error -> {
                        analyticsManager.track(AnalyticsEvent.Notification.Update.Failure(either.error.toString()))
                        Timber.d(either.error.toString())
                        Single.just(either)
                    }
                    is Either.Empty -> Single.just(either)
                    is Either.Value -> {
                        val communicationPreference = either.value
                        analyticsManager.track(
                            AnalyticsEvent.Notification.Update.Success(
                                communicationPreference.houseSevenOptIn,
                                communicationPreference.membersUpdateOptIn,
                                communicationPreference.membersAffiliatesOptIn
                            )
                        )
                        digitalHouse = communicationPreference.houseSevenOptIn
                        membership = communicationPreference.membersUpdateOptIn
                        sohoHouse = communicationPreference.membersAffiliatesOptIn
                        zipRequestsUtil.issueApiCall(GetDeviceNotificationPreferencesRequest())
                    }
                }
            }
            .flatMap { either ->
                when (either) {
                    is Either.Error -> {
                        analyticsManager.track(AnalyticsEvent.Notification.Update.Failure(either.error.toString()))
                        Timber.d(either.error.toString())
                        return@flatMap Single.just(either)
                    }
                    is Either.Empty -> return@flatMap Single.just(either)
                    is Either.Value -> {
                        return@flatMap Single.just(either)
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { setLoadingState(Loading) }
            .doOnSuccess { setLoadingState(Idle) }
            .doOnError { setLoadingState(Idle); showError() }
            .subscribe { either ->
                when (either) {
                    is Either.Error -> {
                        analyticsManager.track(AnalyticsEvent.Notification.Update.Failure(either.error.toString()))
                        Timber.d(either.error.toString())
                    }
                    is Either.Value -> {
                        prefs = either.value

                        val emailMap = mutableMapOf(
                            DIGITAL_HOUSE to digitalHouse,
                            MEMBERSHIP to membership,
                            SOHO_HOUSE to sohoHouse
                        )
                        MarketingCloudSdk.requestSdk { it.updateAllTags(prefs) }
                        val itemList = createAdapterItemList(prefs, emailMap, notificationsEnabled)
                        this.emailMap = emailMap.toMutableMap()
                        _items.postValue(itemList)
                    }
                }
            }
    }

    private fun updateTitlesForFriends(items: List<DeviceNotificationPreferences>) {
        items.takeIf { prefsManager.subscriptionType == SubscriptionType.FRIENDS }?.iterator()
            ?.forEach {
                when (it.id) {
                    PUSH_MARKETING -> {
                        it.title =
                            stringProvider.getString(R.string.notifications_settings_friends_marketing)
                        it.description =
                            stringProvider.getString(R.string.notifications_settings_friends_marketing_description)
                    }
                    PUSH_LOCAL_UPDATE -> {
                        it.title =
                            stringProvider.getString(R.string.notifications_settings_friends_local_updates)
                        it.description =
                            stringProvider.getString(R.string.notifications_settings_friends_local_updates_description)
                    }
                    PUSH_AFFILIATE -> {
                        it.description =
                            stringProvider.getString(R.string.notifications_settings_friends_affiliate_description)
                    }

                }
            }
    }

    private fun createAdapterItemList(
        prefs: List<DeviceNotificationPreferences>,
        emailMap: Map<String, Boolean>,
        notificationsEnabled: Boolean
    ): List<MoreNotificationsAdapterItem> {
        val itemList = mutableListOf<MoreNotificationsAdapterItem>()

        val categories: HashMap<String, ArrayList<DeviceNotificationPreferences>> = HashMap()

        updateTitlesForFriends(prefs)

        prefs.iterator().forEach {
            if (!categories.containsKey(it.category)) categories[it.category] = ArrayList()
            categories[it.category]?.add(it)
        }

        itemList.add(MoreNotificationsTopSupportingAdapterItem())

//        val isNotificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()

        if (!notificationsEnabled) {
            itemList.add(MoreNotificationsPlatformSettingsAdapterItem())
        }

        for (category in categories.keys) {
            itemList.add(MoreNotificationsHeaderAdapterItem(category))

            if (userSessionManager.shouldShowAlertMap.isEmpty()) {
                categories[category]?.iterator()?.forEach {
                    userSessionManager.shouldShowAlertMap[it.id] = true
                }
            }

            for (pref in categories[category]!!)

                when (pref.id) {
                    PUSH_MARKETING -> itemList.add(
                        MoreNotificationsNotificationOptionAdapterItem(
                            pref.id, pref.title, pref.description,
                            if (notificationsEnabled) pref.enabled
                            else false,
                            pref.enabled,
                            true,
                            DIGITAL_HOUSE, emailMap[DIGITAL_HOUSE] ?: true
                        )
                    )

                    PUSH_LOCAL_UPDATE -> itemList.add(
                        MoreNotificationsNotificationOptionAdapterItem(
                            pref.id, pref.title, pref.description,
                            if (notificationsEnabled) pref.enabled
                            else false,
                            pref.enabled,
                            true,
                            MEMBERSHIP, emailMap[MEMBERSHIP] ?: true
                        )
                    )

                    PUSH_AFFILIATE -> itemList.add(
                        MoreNotificationsNotificationOptionAdapterItem(
                            pref.id, pref.title, pref.description,
                            if (notificationsEnabled) pref.enabled
                            else false,
                            pref.enabled,
                            true,
                            SOHO_HOUSE, emailMap[SOHO_HOUSE] ?: true
                        )
                    )

                    else -> itemList.add(
                        MoreNotificationsNotificationOptionAdapterItem(
                            pref.id, pref.title, pref.description,
                            if (notificationsEnabled) pref.enabled
                            else false,
                            defaultState = pref.enabled
                        )
                    )
                }
        }

        return itemList
    }

    fun onNotificationSettingsClicked() {
        firebaseEventTracking.logEventAction(AnalyticsManager.Action.NotificationsOptionsSettings)
    }

    fun notificationOffAlertRequired(key: String, currentValue: Boolean): Boolean {
        return currentValue && userSessionManager.shouldShowAlertMap[key] == true
    }

    fun onTogglePushNotification(
        key: String,
        listener: NotificationEventsToggleListener,
        currentValue: Boolean
    ) {
        listener.toggle(true)
        prefs.find { it.id == key }?.let { updateDeviceNotification(it, !currentValue) }
    }

    @SuppressLint("CheckResult")
    fun onToggleEmail(key: String, currentValue: Boolean) {
        emailMap[key] = !currentValue

        val mutableValueMap = emailMap.toMutableMap()
        val digitalHouse =
            emailMap[DIGITAL_HOUSE]?.also { mutableValueMap.remove(DIGITAL_HOUSE) } ?: true
        val sohoHouse = emailMap[SOHO_HOUSE]?.also { mutableValueMap.remove(SOHO_HOUSE) } ?: true
        val membership = emailMap[MEMBERSHIP]?.also { mutableValueMap.remove(MEMBERSHIP) } ?: true
        val allOptOut = emailMap[ALL_OPT_OUT]?.also { mutableValueMap.remove(ALL_OPT_OUT) } ?: false

        val communicationPreference =
            CommunicationPreference(digitalHouse, sohoHouse, membership, allOptOut)
        zipRequestsUtil.issueApiCall(PatchCommunicationPreferenceRequest(communicationPreference))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { setLoadingState(Loading) }
            .doOnSuccess { setLoadingState(Idle) }
            .doOnError { setLoadingState(Idle); showError() }
            .subscribe(Consumer {
                if (it is Either.Error) {
                    Timber.d(it.error.toString())
                }
            })
    }

    fun turnOffNotification(key: String) {
        prefs.find { it.id == key }?.let {
            updateDeviceNotification(it, false)
        }
    }

    private fun updateDeviceNotification(pref: DeviceNotificationPreferences, isEnable: Boolean) {
        viewModelScope.launch(viewModelContext) {
            pref.enabled = isEnable

            MarketingCloudSdk.requestSdk {
                it.tag(
                    pref.marketingCloud.tag,
                    pref.marketingCloud.tag_indicates_disabled,
                    pref.enabled
                )
            }

            when (val result = apiService.patchDeviceNotificationPrefs(pref)) {
                is ApiResponse.Success -> Timber.tag("FPS").i(result.response.description)
                is ApiResponse.Error -> Timber.tag("FPS").i(result.message ?: "")
            }
        }
    }

    fun onDestroy() {
        firebaseEventTracking.logEventAction(AnalyticsManager.Action.NotificationsOptionsDone)
    }

    fun logNotificationOptionsView() {
        firebaseEventTracking.logEventAction(AnalyticsManager.Action.NotificationsOptions)
    }

    fun flagNotificationsCustomised() {
        prefsManager.notificationsCustomised = true
    }
}
