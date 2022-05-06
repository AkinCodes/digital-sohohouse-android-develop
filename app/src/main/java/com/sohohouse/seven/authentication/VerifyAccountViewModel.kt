package com.sohohouse.seven.authentication

import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.interactors.AccountInteractor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class VerifyAccountViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val accountInteractor: AccountInteractor,
    dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    ErrorDialogViewModel by ErrorDialogViewModelImpl() {

    private val _verificationLinkSentEvent = LiveEvent<Any>()
    val verificationLinkSentEvent: LiveEvent<Any> get() = _verificationLinkSentEvent

    fun onSendVerificationLinkClick(accountId: String) {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            accountInteractor.sendVerificationLink(accountId)
                .fold(
                    ifError = { handleError(it) },
                    ifValue = { onSuccess() },
                    ifEmpty = {}
                )
            setLoadingState(LoadingState.Idle)
        }
    }

    private fun onSuccess() {
        _verificationLinkSentEvent.postEvent()
    }
}
