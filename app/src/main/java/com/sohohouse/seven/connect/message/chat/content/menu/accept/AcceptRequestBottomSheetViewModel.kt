package com.sohohouse.seven.connect.message.chat.content.menu.accept

import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.LiveEvent
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.network.chat.ChatUsersRepo
import kotlinx.coroutines.launch
import javax.inject.Inject

class AcceptRequestBottomSheetViewModel @Inject constructor(
    private val usersRepository: ChatUsersRepo,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager) {

    val dismiss = LiveEvent<Unit>()
    val goBack = LiveEvent<Unit>()

    fun declineInvitation(channelUrl: String) {
        viewModelScope.launch(viewModelContext) {
            usersRepository.decline(channelUrl)
            goBack.postValue(Unit)
        }
    }

    fun acceptInvitation(channelUrl: String) {
        viewModelScope.launch(viewModelContext) {
            usersRepository.accept(channelUrl)
            dismiss.postValue(Unit)
        }
    }

}