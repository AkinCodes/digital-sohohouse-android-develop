package com.sohohouse.seven.connect.trafficlights.update

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.base.mvvm.ViewController
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.extensions.setFragmentResult
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.connect.trafficlights.AvailableStatus
import com.sohohouse.seven.databinding.UpdateAvailabilityStatusBottomSheetBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class UpdateAvailabilityStatusBottomSheet :
    BaseMVVMBottomSheet<UpdateAvailabilityStatusViewModel>(), ViewController, Injectable {

    companion object {
        const val ON_LEAVE_LOCATION = "ON_LEAVE_LOCATION"
        const val ON_UPDATE_STATUS = "ON_UPDATE_STATUS"
        const val TAG = "UpdateAvailabilityStatusBottomSheet"
    }

    override val viewModelClass: Class<UpdateAvailabilityStatusViewModel>
        get() = UpdateAvailabilityStatusViewModel::class.java

    override val fixedHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT

    val binding by viewBinding(UpdateAvailabilityStatusBottomSheetBinding::bind)

    override val contentLayout: Int = R.layout.update_availability_status_bottom_sheet

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            selectableGroup.setOnClickListener { handleSelection(it) }

            viewModel.onUpdateStatus.onEach { close -> if (close) onUpdateStatus() }
                .launchIn(lifecycleScope)
            viewModel.onLeaveLocation.onEach { if (it) leaveLocation() }.launchIn(lifecycleScope)
            viewModel.profileImageUrl.onEach { setImageUrl(it) }.launchIn(lifecycleScope)
            viewModel.currentAvailableStatus.onEach { selectFromStatus(it) }
                .launchIn(lifecycleScope)

            confirm.setOnClickListener { viewModel.confirm() }
            leave.setOnClickListener { viewModel.leaveLocation() }
            close.setOnClickListener { dismiss() }
            showHideConnectionsOnlyBtn()
        }

        layoutBehavior?.state = STATE_EXPANDED
    }

    private fun UpdateAvailabilityStatusBottomSheetBinding.showHideConnectionsOnlyBtn() {
        if (FeatureFlags.TRAFFIC_LIGHTS_SHOW_CONNECTIONS_ONLY) {
            selectableGroup.referencedIds = intArrayOf(
                R.id.availableButton,
                R.id.connectionsOnlyButton,
                R.id.unavailableButton
            )
            connectionsOnlyButton.setVisible()
        } else {
            selectableGroup.referencedIds = intArrayOf(
                R.id.availableButton,
                R.id.unavailableButton
            )
            connectionsOnlyButton.setGone()
        }
    }

    private fun onUpdateStatus() {
        setFragmentResult(ON_UPDATE_STATUS)
        dismiss()
    }

    private fun leaveLocation() {
        setFragmentResult(ON_UPDATE_STATUS)
        dismiss()
    }

    private fun UpdateAvailabilityStatusBottomSheetBinding.selectFromStatus(availableStatus: AvailableStatus) {
        when (availableStatus) {
            AvailableStatus.AVAILABLE -> availableButton
            AvailableStatus.CONNECTIONS_ONLY -> connectionsOnlyButton
            AvailableStatus.UNAVAILABLE -> unavailableButton
        }.also { it.isSelected = true }
    }

    private fun UpdateAvailabilityStatusBottomSheetBinding.setImageUrl(it: String) {
        availableButton.binding.image.setImageUrl(it)
        connectionsOnlyButton.binding.image.setImageUrl(it)
        unavailableButton.binding.image.setImageUrl(it)
    }

    private fun handleSelection(it: Int) {
        when (it) {
            R.id.unavailableButton -> {
                viewModel.updateStatus(AvailableStatus.UNAVAILABLE)
            }
            R.id.connectionsOnlyButton -> {
                viewModel.updateStatus(AvailableStatus.CONNECTIONS_ONLY)
            }
            R.id.availableButton -> {
                viewModel.updateStatus(AvailableStatus.AVAILABLE)
            }
        }
    }

}