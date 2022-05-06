package com.sohohouse.seven.connect.trafficlights.firstvisit

import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.prefs.LocalVenueProvider
import com.sohohouse.seven.common.prefs.PrefsManager
import com.sohohouse.seven.connect.trafficlights.AvailableStatus
import com.sohohouse.seven.connect.trafficlights.repo.TrafficLightsRepo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class TrafficLightFirstVisitViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    prefsManager: PrefsManager,
    private val trafficLightsRepo: TrafficLightsRepo,
    private val localVenueProvider: LocalVenueProvider
) : BaseViewModel(analyticsManager) {

    private val _availableStatus = MutableStateFlow(AvailableStatus.UNAVAILABLE)
    val currentAvailableStatus: StateFlow<AvailableStatus> get() = _availableStatus

    private val _closeDialog = MutableStateFlow(false)
    val closeDialog: StateFlow<Boolean> get() = _closeDialog

    val localVenueName: Flow<String> = flow {
        emit(localVenueProvider.localVenue.value?.name ?: "")
    }.flowOn(viewModelContext)

    val profileImageURL: Flow<String> = flowOf(prefsManager.profileImageURL)

    fun updateStatus(availableStatus: AvailableStatus) {
        analyticsManager.logEventAction(availableStatus.eventAction)
        _availableStatus.value = availableStatus
    }

    fun confirm() {
        viewModelScope.launch(viewModelContext) {
            analyticsManager.logEventAction(AnalyticsManager.Action.TrafficLightsCheckingConfirm)
            trafficLightsRepo.updateStatus(currentAvailableStatus.value).ifValue {
                _closeDialog.value = true
            }
        }
    }
}