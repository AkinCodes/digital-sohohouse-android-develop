package com.sohohouse.seven.apponboarding.terms

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.R
import com.sohohouse.seven.authentication.AuthenticationFlowManager
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.core.common.StaticPagesTypes.SOHO_FRIENDS_TERMS
import com.sohohouse.seven.network.core.common.StaticPagesTypes.TERMS_AND_CONDITIONS
import com.sohohouse.seven.network.core.models.AccountUpdate
import com.sohohouse.seven.network.core.request.GetFriendsTermsRequest
import com.sohohouse.seven.network.core.request.GetStaticPagesRequest
import com.sohohouse.seven.network.core.request.PatchAccountAttributesRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class OnboardingTermsViewModel @Inject constructor(
    private val zipRequestsUtil: com.sohohouse.seven.common.utils.ZipRequestsUtil,
    private val userManager: UserManager,
    private val authenticationFlowManager: AuthenticationFlowManager,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    val headerResId: Int
        get() = if (SubscriptionType.FRIENDS == userManager.subscriptionType)
            R.string.app_onboarding_terms_header_friends
        else
            R.string.app_onboarding_terms_header

    private val _termsAndConditions: MutableLiveData<String> = MutableLiveData()
    val termsAndConditions: LiveData<String>
        get() = _termsAndConditions

    private val _termsAccepted: LiveEvent<Any> = LiveEvent()
    val termsAccepted: LiveEvent<Any>
        get() = _termsAccepted

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.OnboardTermsConditions.name)
    }

    fun fetchTerms() {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            val isFriendsMember = SubscriptionType.FRIENDS == userManager.subscriptionType
            val request = if (isFriendsMember) GetFriendsTermsRequest() else GetStaticPagesRequest()
            zipRequestsUtil.issueApiCall(request).fold(
                ifError = { onError(it) },
                ifValue = { pages ->
                    pages.firstOrNull { page ->
                        if (isFriendsMember) page.id == SOHO_FRIENDS_TERMS.name else page.id == TERMS_AND_CONDITIONS.name
                    }?.let { _termsAndConditions.postValue(it.body) }
                },
                ifEmpty = {}
            )
            setLoadingState(LoadingState.Idle)
        }
    }

    fun agreeClicked() {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            zipRequestsUtil.issueApiCall(
                PatchAccountAttributesRequest(
                    AccountUpdate(
                        null,
                        termsConditionsConsent = true
                    )
                )
            ).fold(
                ifError = { onError(it) },
                ifValue = {
                    userManager.didConsentTermsConditions = true
                    _termsAccepted.postEvent()
                },
                ifEmpty = {}
            )
            setLoadingState(LoadingState.Idle)
        }
    }

    fun navigateFrom(context: Context): Intent = authenticationFlowManager.navigateFrom(context)

    private fun onError(error: ServerError) {
        Timber.d(error.toString())
        showError()
    }

}