package com.sohohouse.seven.profile.view.connect

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.BundleKeys.RECIPIENT_GLOBAL_ID
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import com.sohohouse.seven.network.core.models.MutualConnectionRequests
import com.sohohouse.seven.network.core.models.Profile
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import moe.banana.jsonapi2.HasOne

class ConnectRequestViewModel @AssistedInject constructor(
    @Assisted private val id: String,
    private val repo: ConnectionRepository,
    analyticsManager: AnalyticsManager,
    private val dispatcher: CoroutineDispatcher,
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    private val _requestSent: LiveEvent<Any> = LiveEvent()

    val requestSent: LiveData<Any>
        get() = _requestSent

    fun sendRequest(message: String = "") {
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            setLoadingState(LoadingState.Loading)
            onSendConnectRequest()

            repo.postConnectionRequest(
                MutualConnectionRequests(
                    message = message,
                    receiver = HasOne(Profile().also { it.id = id })
                )
            )
                .ifValue { _requestSent.postEvent() }
                .ifError { showError() }

            setLoadingState(LoadingState.Idle)
        }
    }

    private fun onSendConnectRequest() {
        analyticsManager.logEventAction(
            action = AnalyticsManager.Action.ProfileConnectMessageSent,
            params = bundleOf(Pair(RECIPIENT_GLOBAL_ID, id))
        )
    }

    fun onCancelComposeMessage() {
        analyticsManager.logEventAction(
            action = AnalyticsManager.Action.ProfileConnectMessageCancel,
            params = bundleOf(Pair(RECIPIENT_GLOBAL_ID, id))
        )
    }

    fun onClickWriteMessage() {
        analyticsManager.logEventAction(
            action = AnalyticsManager.Action.ProfileConnectWriteMessage,
            params = bundleOf(Pair(RECIPIENT_GLOBAL_ID, id))
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(id: String): ConnectRequestViewModel
    }
}