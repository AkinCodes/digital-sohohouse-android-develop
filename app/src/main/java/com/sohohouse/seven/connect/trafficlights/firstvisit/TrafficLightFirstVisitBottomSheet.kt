package com.sohohouse.seven.connect.trafficlights.firstvisit

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.base.mvvm.ViewController
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.connect.trafficlights.AvailableStatus
import com.sohohouse.seven.databinding.FragmentTrafficLightFirstVisitBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class TrafficLightFirstVisitBottomSheet : BaseMVVMBottomSheet<TrafficLightFirstVisitViewModel>(),
    ViewController, Injectable {

    companion object {
        const val TAG = "TrafficLightFirstVisitBottomSheet"
    }

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    override val viewModelClass: Class<TrafficLightFirstVisitViewModel> =
        TrafficLightFirstVisitViewModel::class.java

    val binding by viewBinding(FragmentTrafficLightFirstVisitBinding::bind)

    override val contentLayout: Int = R.layout.fragment_traffic_light_first_visit

    override val fixedHeight get() = ViewGroup.LayoutParams.WRAP_CONTENT

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            selectableGroup.setOnClickListener {
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

            observeViewModel()

            confirm.setOnClickListener { viewModel.confirm() }
            cancel.setOnClickListener {
                analyticsManager.logEventAction(AnalyticsManager.Action.TrafficLightsCheckingDismiss)
                dismiss()
            }

            layoutBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun FragmentTrafficLightFirstVisitBinding.observeViewModel() {
        viewModel.currentAvailableStatus.onEach { availableStatus ->
            when (availableStatus) {
                AvailableStatus.AVAILABLE -> availableButton
                AvailableStatus.CONNECTIONS_ONLY -> connectionsOnlyButton
                AvailableStatus.UNAVAILABLE -> unavailableButton
            }.also { it.isSelected = true }
        }.launchIn(lifecycleScope)

        viewModel.closeDialog.onEach { close ->
            if (close) dismiss()
        }.launchIn(lifecycleScope)

        viewModel.localVenueName.onEach {
            title.text = getString(R.string.traffic_list_first_visit_title, it)
        }.launchIn(lifecycleScope)

        viewModel.profileImageURL.onEach {
            availableButton.binding.image.setImageUrl(it)
            connectionsOnlyButton.binding.image.setImageUrl(it)
            unavailableButton.binding.image.setImageUrl(it)
        }.launchIn(lifecycleScope)
    }

}