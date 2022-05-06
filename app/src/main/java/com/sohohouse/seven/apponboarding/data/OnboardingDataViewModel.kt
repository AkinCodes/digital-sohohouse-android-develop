package com.sohohouse.seven.apponboarding.data

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.authentication.AuthenticationFlowManager
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.AnalyticsConsent
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.ZipRequestsUtil
import com.sohohouse.seven.network.core.common.StaticPagesTypes
import com.sohohouse.seven.network.core.models.AccountUpdate
import com.sohohouse.seven.network.core.request.GetPrivacyPolicyRequest
import com.sohohouse.seven.network.core.request.PatchAccountAttributesRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class OnboardingDataViewModel @Inject constructor(
    private val zipRequestsUtil: ZipRequestsUtil,
    private val userManager: UserManager,
    private val authFlowManager: AuthenticationFlowManager,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher),
    Loadable.ViewModel by Loadable.ViewModelImpl() {

    private val _privacyPolicy: MutableLiveData<String?> = MutableLiveData()
    val privacyPolicy: LiveData<String?>
        get() = _privacyPolicy

    private val _intent: MutableLiveData<Intent> = MutableLiveData()
    val intent: LiveData<Intent>
        get() = _intent

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.OnboardDataCollection.name)
    }

    fun getPrivacyPolicy() {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            zipRequestsUtil.issueApiCall(GetPrivacyPolicyRequest()).fold(
                ifError = { FirebaseCrashlytics.getInstance().log(it.toString()) },
                ifValue = { pages ->
                    pages.firstOrNull { it.id == StaticPagesTypes.PRIVACY_POLICY.name && it.body.isNotEmpty() }
                        .let { _privacyPolicy.postValue(it?.body) }
                },
                ifEmpty = { _privacyPolicy.postValue(null) }
            )
            setLoadingState(LoadingState.Idle)
        }
    }

    fun didDecideAnalytics(context: Context, didConsent: Boolean) {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)

            zipRequestsUtil.issueApiCall(
                PatchAccountAttributesRequest(
                    AccountUpdate(
                        didConsent,
                        null
                    )
                )
            )
                .fold(
                    ifError = { Timber.d(it.toString()) },
                    ifValue = {
                        if (didConsent) {
                            userManager.analyticsConsent = AnalyticsConsent.ACCEPTED
                            analyticsManager.setAnalyticsEnabled(true)
                            analyticsManager.track(AnalyticsEvent.AppOnBoarding.DataCollection)
                        } else {
                            userManager.analyticsConsent = AnalyticsConsent.REJECTED
                            analyticsManager.setAnalyticsEnabled(false)
                        }
                        _intent.postValue(authFlowManager.navigateFrom(context))
                    },
                    ifEmpty = {}
                )
            setLoadingState(LoadingState.Idle)
        }
    }

}