package com.sohohouse.seven.splash

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.BuildConfig
import com.sohohouse.seven.authentication.AuthenticationFlowManager
import com.sohohouse.seven.authentication.NotificationType
import com.sohohouse.seven.authentication.NotificationType.ERROR
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.LiveEvent
import com.sohohouse.seven.base.mvvm.postEvent
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder.PATH_CHAT
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder.PATH_MESSAGE
import com.sohohouse.seven.common.deeplink.DeeplinkViewModel
import com.sohohouse.seven.common.extensions.addTo
import com.sohohouse.seven.common.extensions.print
import com.sohohouse.seven.common.extensions.removeBuildSuffix
import com.sohohouse.seven.common.extensions.toMap
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.user.UserSessionManager
import com.sohohouse.seven.common.uxcam.UXCamVendor
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.isSuccessful
import com.sohohouse.seven.network.forceupdate.ForceUpdateError
import com.sohohouse.seven.network.forceupdate.ForceUpdateRequest
import com.sohohouse.seven.network.forceupdate.ForceUpdateRequestFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val flowManager: AuthenticationFlowManager,
    private val forceUpdateRequestFactory: ForceUpdateRequestFactory,
    private val accountInteractor: AccountInteractor,
    private val userSessionManager: UserSessionManager,
    private val ioDispatcher: CoroutineDispatcher,
    private val userManager: UserManager,
    private val sohoApiService: SohoApiService,
    val uxCamVendor: UXCamVendor,
    deeplinkViewModel: DeeplinkViewModel,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager),
    DeeplinkViewModel by deeplinkViewModel {

    private val _navigation: MutableLiveData<Intent> = MutableLiveData()
    val navigation: LiveData<Intent>
        get() = _navigation

    private val _networkError: LiveEvent<Any> = LiveEvent()
    val networkError: LiveEvent<Any>
        get() = _networkError

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.Splash.name)
    }

    fun loadRequest(context: Context, intent: Intent?) {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            forceUpdateRequestFactory.create(ForceUpdateRequest(BuildConfig.VERSION_NAME.removeBuildSuffix()))
                .fold(
                    ifError = {
                        analyticsManager.logEventAction(
                            AnalyticsManager.Action.ForceUpdateRequest,
                            bundleOf("error_type" to it.javaClass.simpleName)
                        )
                        when (it) {
                            ServerError.TIMEOUT,
                            ServerError.NO_INTERNET,
                            ServerError.INVALID_RESPONSE -> showNetworkErrorDialog()
                            ForceUpdateError.UPDATE_REQUIRED -> navigateToForceUpdate(context)
                            ForceUpdateError.SERVER_MAINTENANCE -> navigateToServerMaintenance(
                                context
                            )
                            else -> showGenericErrorDialog()
                        }
                    },
                    ifValue = {
                        fetchUserAccount(context, intent)
                    },
                    ifEmpty = {
                        analyticsManager.logEventAction(
                            AnalyticsManager.Action.ForceUpdateRequest,
                            bundleOf("returns" to "empty")
                        )
                        showGenericErrorDialog()
                    }
                )
        }
    }

    private fun fetchUserAccount(context: Context, intent: Intent?) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.PassPoint, bundleOf(
                "point" to "fetchUserAccount",
                "is_logged" to userSessionManager.isLoggedIn,
                "intent_content" to "data: ${intent?.data} | extras: ${intent?.extras?.print()}"
            )
        )
        if (userSessionManager.isLoggedIn) {
            fetchAccountOnboarding()

            val completeAccount =
                accountInteractor.getCompleteAccountV2(
                    refreshAccount = true,
                    includeLastAttendance = true
                )
            val log = when (completeAccount) {
                is Either.Empty -> "empty"
                is Either.Value -> "success"
                is Either.Error -> completeAccount.error
            }
            analyticsManager.logEventAction(
                AnalyticsManager.Action.CompleteAccount, bundleOf(
                    "request state" to log
                )
            )
        }
        transitionToNextScreen(context, intent)
    }

    private fun fetchAccountOnboarding() {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            sohoApiService.getAccountOnboardingStatus().let {
                if (it.isSuccessful()) {
                    userManager.isAppOnboardingNeeded =
                        it.response.houseMemberOnboardingAt == null && it.response.friendsMemberOnboardedAt == null
                } else {
                    userManager.isAppOnboardingNeeded = false
                }
            }
        }
    }

    private fun transitionToNextScreen(context: Context, intent: Intent?) {
        if (intent == null || (intent.extras == null && intent.data == null)) {
            navigateToNextScreen(context)
            return
        }

        val uri = intent.data
            ?: DeeplinkBuilder.buildUri(intent.extras?.toMap())

        if (uri == null) {
            navigateToNextScreen(context)
            return
        }

        // TODO: Improvement needed to handle different deeplink authorities
        when (uri.authority) {
            DeeplinkBuilder.AUTHORITY_CLICK -> {
                redirectDeeplink(context, uri)
                return
            }
            DeeplinkBuilder.AUTHORITY -> {
                setDeeplink(
                    uri.buildUpon()
                        .scheme(DeeplinkBuilder.APPS_SCHEME)
                        .authority(DeeplinkBuilder.APP_AUTHORITY)
                        .clearQuery().build()
                )
                navigateToNextScreen(context)
                return
            }
            DeeplinkBuilder.AUTHORITY_PROFILE -> {
                setDeeplink(
                    uri.buildUpon()
                        .scheme(DeeplinkBuilder.HTTPS_SCHEME)
                        .authority(DeeplinkBuilder.AUTHORITY_PROFILE)
                        .clearQuery().build()
                )
                navigateToNextScreen(context, true, uri.toString())
                return
            }
        }

        val screen = uri.getQueryParameter("screen")
        val type = when (uri.pathSegments.first()) {
            PATH_MESSAGE -> NotificationType.MESSAGE
            PATH_CHAT -> NotificationType.NEW_MESSAGE_REQUEST
            else -> NotificationType.fromId(screen) ?: ERROR
        }
        val id = uri.getQueryParameter("id").orEmpty()
        setDeeplink(uri)
        navigateFromNotification(context, id, type)
    }

    private fun redirectDeeplink(context: Context, uri: Uri) {
        redirectDeeplink(uri.toString())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ navigateToNextScreen(context) }, { navigateToNextScreen(context) })
            .addTo(compositeDisposable)
    }

    private fun navigateToNextScreen(
        context: Context,
        reviewProfile: Boolean = false,
        payload: String = ""
    ) {
        _navigation.postValue(
            flowManager.navigateFrom(
                context,
                reviewProfile = reviewProfile,
                payload = payload
            )
        )
    }

    private fun navigateToForceUpdate(context: Context) {
        _navigation.postValue(flowManager.navigateToForceUpdate(context))
    }

    private fun showNetworkErrorDialog() {
        _networkError.postEvent()
    }

    private fun showGenericErrorDialog() {
        _networkError.postEvent()
    }

    private fun navigateToServerMaintenance(context: Context) {
        _navigation.postValue(flowManager.navigateToServerMaintenance(context))
    }

    private fun navigateFromNotification(
        context: Context,
        eventId: String,
        type: NotificationType
    ) {
        _navigation.postValue(
            flowManager.navigateFrom(
                context,
                payload = eventId,
                notificationType = type
            )
        )
    }
}
