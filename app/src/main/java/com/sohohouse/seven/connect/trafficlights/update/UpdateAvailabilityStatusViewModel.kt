package com.sohohouse.seven.connect.trafficlights.update

import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.connect.trafficlights.AvailableStatus
import com.sohohouse.seven.connect.trafficlights.repo.TrafficLightsRepo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateAvailabilityStatusViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val userManager: UserManager,
    private val trafficLightsRepo: TrafficLightsRepo,
) : BaseViewModel(analyticsManager) {

    private val _onUpdateStatus: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val onUpdateStatus: StateFlow<Boolean> get() = _onUpdateStatus

    private val _onLeaveLocation: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val onLeaveLocation: StateFlow<Boolean> = _onLeaveLocation

    val profileImageUrl = flowOf(userManager.profileImageURL)

    val currentAvailableStatus: Flow<AvailableStatus> get() = userManager.availableStatusFlow.map { it.availableStatus }
    private var updatedAvailableStatus: AvailableStatus =
        userManager.availableStatusFlow.value.availableStatus

    fun updateStatus(availableStatus: AvailableStatus) {
        analyticsManager.logEventAction(availableStatus.eventAction)
        updatedAvailableStatus = availableStatus
    }

    fun confirm() {
        viewModelScope.launch(viewModelContext) {
            trafficLightsRepo.updateStatus(updatedAvailableStatus).ifValue {
                _onUpdateStatus.value = true
            }
        }
    }

    fun leaveLocation() {
        viewModelScope.launch(viewModelContext) {
            analyticsManager.logEventAction(AnalyticsManager.Action.TrafficLightsLeaveVenueConfirm)
            trafficLightsRepo.clearStatus().ifValue {
                _onLeaveLocation.value = true
            }
        }
    }

}