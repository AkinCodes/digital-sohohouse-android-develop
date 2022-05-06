package com.sohohouse.seven.housepay.terms

import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.authentication.AuthenticationFlowManager
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.core.common.StaticPagesTypes
import com.sohohouse.seven.network.core.models.AccountUpdate
import com.sohohouse.seven.network.core.request.GetStaticPagesRequest
import com.sohohouse.seven.network.core.request.PatchAccountAttributesRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class HousePayTermsViewModel @Inject constructor(
    private val zipRequestsUtil: com.sohohouse.seven.common.utils.ZipRequestsUtil,
    private val userManager: UserManager,
    private val authenticationFlowManager: AuthenticationFlowManager,
    analyticsManager: AnalyticsManager,
    val ioDispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, ioDispatcher),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    private val _termsAndConditions = MutableStateFlow("")
    val termsAndConditions: StateFlow<String>
        get() = _termsAndConditions.asStateFlow()

    private val _termsAccepted = MutableSharedFlow<Boolean>()
    val termsAccepted = _termsAccepted.asSharedFlow()

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.HousePayTermsConditions.name)
    }

    fun fetchTerms() {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            setLoadingState(LoadingState.Loading)
            val request = GetStaticPagesRequest()
            zipRequestsUtil.issueApiCall(request)
                .fold(
                    ifValue = { pages ->
                        pages.firstOrNull { page ->
                            page.id == StaticPagesTypes.TERMS_AND_CONDITIONS_FOR_HOUSE_PAY.name
                        }?.let {
                            _termsAndConditions.value = it.body
                        }
                    },
                    ifError = { onError(it) },
                    ifEmpty = {}
                )
            setLoadingState(LoadingState.Idle)
        }
    }

    fun agreeClicked() {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            setLoadingState(LoadingState.Loading)
            zipRequestsUtil.issueApiCall(
                PatchAccountAttributesRequest(
                    AccountUpdate(
                        analyticsConsent = null,
                        termsConditionsConsent = null,
                        housePayTermsConditionsConsent = true
                    )
                )
            ).fold(
                ifError = { onError(it) },
                ifValue = {
                    userManager.didConsentHousePayTermsConditions = true
                    _termsAccepted.emit(true)
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
