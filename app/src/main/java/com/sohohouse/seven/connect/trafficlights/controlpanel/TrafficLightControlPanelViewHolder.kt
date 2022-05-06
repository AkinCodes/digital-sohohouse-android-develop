package com.sohohouse.seven.connect.trafficlights.controlpanel

import android.content.res.ColorStateList
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getAttributeColor
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.connect.trafficlights.AvailableStatus
import com.sohohouse.seven.databinding.TrafficLightControlPanelFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TrafficLightControlPanelViewHolder(
    private val coroutineScope: Lazy<CoroutineScope>,
    private val startMemberInVenue: () -> Unit,
    private val onLeaveClick: () -> Unit,
    private val binding: TrafficLightControlPanelFragmentBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(trafficLightsControlPanel: TrafficLightsControlPanel) {
        binding.title.text = if (trafficLightsControlPanel.estimatedTotal > 1)
            getString(R.string.n_members, trafficLightsControlPanel.estimatedTotal.toString())
        else
            getString(R.string.you_re_the_first_here)

        binding.subtitle.text = trafficLightsControlPanel.venueName

        binding.leave.setOnClickListener {
            onLeaveClick()
        }

        binding.image.setImageUrl(trafficLightsControlPanel.userImageUrl)
        binding.image.setOnClickListener { startMemberInVenue() }
        binding.connectionsContainer.setOnClickListener { startMemberInVenue() }

        trafficLightsControlPanel.availableStatus.onEach {
            binding.status.backgroundTintList = ColorStateList.valueOf(
                binding.root.getAttributeColor(it.colorAttrRes)
            )
            bindConnections(trafficLightsControlPanel, it)
        }.launchIn(coroutineScope.value)

        binding.image.setImageUrl(trafficLightsControlPanel.userImageUrl)
    }

    private fun bindConnections(
        trafficLightsControlPanel: TrafficLightsControlPanel,
        availableStatus: AvailableStatus
    ) {
        bindConnections(
            VenueMemberViewProviders(
                venueMembers = trafficLightsControlPanel.venueMembers,
                availableStatus = availableStatus,
                threshold = trafficLightsControlPanel.threshold,
                estimatedTotal = trafficLightsControlPanel.estimatedTotal
            ).toList()
        )
    }

    private fun bindConnections(list: Collection<VenueMemberViewProvider>) {
        binding.connectionsContainer.removeAllViews()
        list.forEachIndexed { index, connectMemberViewProvider ->
            val inflatedViewParent = OverlappedFrameLayout(
                context = itemView.context,
                currentIndex = index,
                totalCount = list.size
            )
            connectMemberViewProvider.inflate(inflatedViewParent)
            binding.connectionsContainer.addView(inflatedViewParent)
        }
    }

}