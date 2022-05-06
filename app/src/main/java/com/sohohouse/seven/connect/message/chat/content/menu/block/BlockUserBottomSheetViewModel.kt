package com.sohohouse.seven.connect.message.chat.content.menu.block

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class BlockUserBottomSheetViewModel @AssistedInject constructor(
    @Assisted private val recipientUserID: String,
    @Assisted private val areMessagesEmpty: Boolean,
    private val repo: ConnectionRepository,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager),
    Errorable.ViewModel by Errorable.ViewModelImpl(),
    Loadable.ViewModel by Loadable.ViewModelImpl() {

    val dismiss = LiveEvent<Unit>()
    val isCurrentMemberBlocked = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            repo.getBlockedMembers()
                .ifValue {
                    isCurrentMemberBlocked.postValue(
                        it.blockedMembers?.contains(recipientUserID) ?: false
                    )
                }
            setLoadingState(LoadingState.Idle)
        }
    }

    fun unblockMember() {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            repo.patchUnblockMember(recipientUserID)
                .ifValue {
                    dismiss.postValue(Unit)
                }.ifError { showError() }
            setLoadingState(LoadingState.Idle)
        }
    }

    fun blockMember() {
        logAnalyticsAction()
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            repo.patchBlockMember(recipientUserID)
                .ifValue {
                    dismiss.postValue(Unit)
                }.ifError { showError() }
            setLoadingState(LoadingState.Idle)
        }
    }

    private fun logAnalyticsAction() {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.MessagingUserBlockConfirm,
            Bundle().apply {
                putString(
                    AnalyticsManager.Parameters.MessagingRecipientGlobalID.value,
                    recipientUserID
                )
                putBoolean(
                    AnalyticsManager.Parameters.MessagingMessagesAreEmpty.value,
                    areMessagesEmpty
                )

            })
    }

    @AssistedFactory
    interface Factory {
        fun create(userID: String, areMessagesEmpty: Boolean): BlockUserBottomSheetViewModel
    }
}