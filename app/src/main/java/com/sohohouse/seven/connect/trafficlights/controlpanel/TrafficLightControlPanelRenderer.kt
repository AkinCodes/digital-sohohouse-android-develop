package com.sohohouse.seven.connect.trafficlights.controlpanel

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.TrafficLightControlPanelFragmentBinding
import kotlinx.coroutines.CoroutineScope

class TrafficLightControlPanelRenderer(
    private val coroutineScope: Lazy<CoroutineScope>,
    private val onLeaveClick: () -> Unit,
    private val startMemberInVenue: () -> Unit
) : Renderer<TrafficLightsControlPanel, TrafficLightControlPanelViewHolder> {

    override val type: Class<TrafficLightsControlPanel> = TrafficLightsControlPanel::class.java

    override fun createViewHolder(parent: ViewGroup): TrafficLightControlPanelViewHolder {
        val binding = TrafficLightControlPanelFragmentBinding.bind(
            createItemView(parent, R.layout.traffic_light_control_panel_fragment)
        )
        return TrafficLightControlPanelViewHolder(
            coroutineScope = coroutineScope,
            onLeaveClick = onLeaveClick,
            startMemberInVenue = startMemberInVenue,
            binding = binding
        )
    }

    override fun bindViewHolder(
        holder: TrafficLightControlPanelViewHolder,
        item: TrafficLightsControlPanel
    ) {
        holder.bind(item)
    }

}
