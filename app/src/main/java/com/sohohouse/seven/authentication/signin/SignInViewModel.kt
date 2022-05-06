package com.sohohouse.seven.authentication.signin

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.os.bundleOf
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.branding.AppIconService
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.remoteconfig.RemoteConfigManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.isSuccessful
import com.uxcam.UXCam
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignInViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val accountInteractor: AccountInteractor,
    private val userManager: UserManager,
    private val ioDispatcher: CoroutineDispatcher,
    private val sohoApiService: SohoApiService,
    private val remoteConfigManager: RemoteConfigManager
) : BaseViewModel(analyticsManager), Loadable.ViewModel by Loadable.ViewModelImpl(),
    ErrorDialogViewModel by ErrorDialogViewModelImpl() {

    private val _loginSuccessEvent = LiveEvent<Any>()
    private val _loginFailEvent = LiveEvent<Any>()

    val loginSuccessEvent: LiveEvent<Any> get() = _loginSuccessEvent
    val loginFailEvent: LiveEvent<Any> get() = _loginFailEvent

    @SuppressLint("CheckResult")
    fun login(email: String, password: String, clientSecret: String, clientID: String) {
        analyticsManager.logEventAction(AnalyticsManager.Action.SignInMember)
        setLoadingState(LoadingState.Loading)
        accountInteractor.signIn(email, password, clientSecret, clientID)
            .observeOn(AndroidSchedulers.mainThread())
            .compose(errorDialogTransformer())
            .subscribe(Consumer {
                setLoadingState(LoadingState.Idle)
                when (it) {
                    is Either.Error -> {
                        analyticsManager.logEventAction(
                            action = AnalyticsManager.Action.SignInMemberFail,
                            params = bundleOf(
                                "error" to "${it.error.javaClass.simpleName}: ${it.error}"
                            )
                        )
                        accountInteractor.logout()
                        _loginFailEvent.postEvent()
                    }
                    is Either.Value -> {
                        analyticsManager.logEventAction(AnalyticsManager.Action.AuthLoginSuccess)
                        fetchAccountOnboarding()
                        // Set UXCam user identity as user's first and last name
                        if (remoteConfigManager.remoteConfig.getBoolean(FeatureFlags.UxCam.UXCAM_IDENTITY_TRACKING_ENABLED)) {
                            it.value.profile?.let { profile ->
                                UXCam.setUserIdentity(profile.firstName + profile.lastName)
                            }
                        }
                    }
                    is Either.Empty -> {
                        analyticsManager.logEventAction(AnalyticsManager.Action.AuthLoginEmpty)
                        throw IllegalArgumentException("Did not expect an empty body.")
                    }
                }
            })
    }

    private fun fetchAccountOnboarding() {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            sohoApiService.getAccountOnboardingStatus().let {
                if (it.isSuccessful()) {
                    userManager.isAppOnboardingNeeded =
                        it.response.houseMemberOnboardingAt == null && it.response.friendsMemberOnboardedAt == null
                    _loginSuccessEvent.postEvent()
                } else {
                    userManager.isAppOnboardingNeeded = false
                    _loginSuccessEvent.postEvent()
                }
            }
        }
    }

    fun logForgotPasswordClick() {
        analyticsManager.logEventAction(AnalyticsManager.Action.SignInForgotPassword)
    }

    fun isAppIconUpdated(context: Context): Boolean {
        return AppIconService.isComponentEnabled(
            context,
            userManager.subscriptionType,
            userManager.isStaff
        )
    }

}