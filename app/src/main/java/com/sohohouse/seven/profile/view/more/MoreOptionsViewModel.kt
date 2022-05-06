package com.sohohouse.seven.profile.view.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import com.sohohouse.seven.profile.Connected
import com.sohohouse.seven.profile.NotConnected
import com.sohohouse.seven.profile.RequestReceived
import com.sohohouse.seven.profile.RequestSent
import com.sohohouse.seven.profile.view.model.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class MoreOptionsViewModel @AssistedInject constructor(
    @Assisted private val profile: ProfileItem,
    private val repo: ConnectionRepository,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager),
    Errorable.ViewModel by Errorable.ViewModelImpl(),
    Loadable.ViewModel by Loadable.ViewModelImpl() {

    private val _statusChanged: LiveEvent<Any> = LiveEvent()
    val statusChanged: LiveData<Any>
        get() = _statusChanged

    val moreOptions: List<ProfileAction>
        get() = when (profile.status) {
            RequestSent,
            RequestReceived,
            NotConnected -> listOf(Block)
            Connected -> listOf(Remove, Block)
            else -> emptyList()
        }

    fun removeFromConnection() {
        viewModelScope.launch(viewModelContext) {
            repo.deleteMutualConnection(profile.connectionId ?: return@launch)
                .ifEmpty { _statusChanged.postValue(Any()) }
                .ifError { showError() }
        }
    }

    fun blockMember() {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            repo.patchBlockMember(profile.id)
                .ifValue { _statusChanged.postValue(Any()) }
                .ifError { showError() }
            setLoadingState(LoadingState.Idle)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted profile: ProfileItem): MoreOptionsViewModel
    }
}