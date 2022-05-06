package com.sohohouse.seven.connect.message.chat.content.menu.report

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class ReportUserBottomSheetViewModel @AssistedInject constructor(
    @Assisted private val recipientUserID: String,
    @Assisted private val areMessagesEmpty: Boolean,
    private val repo: ConnectionRepository,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager),
    Errorable.ViewModel by Errorable.ViewModelImpl(),
    Loadable.ViewModel by Loadable.ViewModelImpl() {

    val goToFinish = LiveEvent<Unit>()

    fun reportUser(message: String) {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            logAnalyticsActions(AnalyticsManager.Action.MessagingUserReportSubmit)
            repo.postReportMember(recipientUserID, message)
                .ifValue {
                    goToFinish.postValue(Unit)
                }.ifError {
                    showError(it.toString())
                }
            setLoadingState(LoadingState.Idle)
        }
    }

    fun logAnalyticsActions(action: AnalyticsManager.Action) {
        analyticsManager.logEventAction(
            action,
            Bundle().apply {
                putString(
                    AnalyticsManager.Parameters.MessagingRecipientGlobalID.value,
                    recipientUserID
                )
                putBoolean(
                    AnalyticsManager.Parameters.MessagingMessagesAreEmpty.value,
                    areMessagesEmpty
                )
            }
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(
            recipientUserID: String,
            areMessagesEmpty: Boolean
        ): ReportUserBottomSheetViewModel
    }


}