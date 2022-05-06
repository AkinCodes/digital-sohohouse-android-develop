package com.sohohouse.seven.connect.trafficlights.controlpanel

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.connect.trafficlights.AvailableStatus
import kotlinx.coroutines.flow.Flow
import com.sohohouse.seven.connect.VenueMember

data class TrafficLightsControlPanel(
    val venueMembers: Collection<VenueMember>,
    val venueName: String,
    val availableStatus: Flow<AvailableStatus>,
    val userImageUrl: String,
    val threshold: Int,
    val estimatedTotal: Int
) : DiffItem {
    override val key: Any = TrafficLightsControlPanel::class.java
}
