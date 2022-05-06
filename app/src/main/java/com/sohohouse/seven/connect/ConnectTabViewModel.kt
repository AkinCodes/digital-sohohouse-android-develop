package com.sohohouse.seven.connect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import com.sohohouse.seven.network.chat.ChatChannelsRepo
import com.sohohouse.seven.network.chat.ChatConnectionRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ConnectTabViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val userManager: UserManager,
    private val connectionRepository: ChatConnectionRepo,
    private val channelRepository: ChatChannelsRepo,
    private val userRepository: ConnectionRepository
) : BaseViewModel(analyticsManager) {

    private val _unreadCountLiveData = MutableLiveData<Int>()
    val unreadCountLiveData: LiveData<Int>
        get() = _unreadCountLiveData

    private val _totalConnections = MutableLiveData<Int>()
    val totalConnections: LiveData<Int>
        get() = _totalConnections

    private val _numberOfConnectRequests= MutableStateFlow(0)
    val numberOfConnectRequests = _numberOfConnectRequests.asStateFlow()

    init {
        viewModelScope.launch(viewModelContext) {
            connectionRepository.connect(userManager.getMiniProfileForSB())

            channelRepository.channels()
                .map { channels ->
                    channels.count {
                        it.isUnread
                    }
                }.onEach {
                    withContext(Dispatchers.Main) {
                        _unreadCountLiveData.value = it
                    }
                }.launchIn(this)

            launch {
                userRepository.numberOfConnectRequests.collect {
                    _numberOfConnectRequests.value = it
                }
            }

            userRepository.totalConnections.onEach {
                _totalConnections.postValue(it)
            }.launchIn(this)
        }
    }

}