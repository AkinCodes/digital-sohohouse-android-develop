package com.sohohouse.seven.connect

import com.sohohouse.seven.connect.trafficlights.AvailableStatus
import com.sohohouse.seven.network.core.models.MutualConnectionRequests
import com.sohohouse.seven.profile.MutualConnectionStatus
import com.sohohouse.seven.profile.SocialMediaItem

data class VenueMember(
    val id: String,
    val mutualConnectionStatus: MutualConnectionStatus,
    val isConnection: Boolean,
    val firstName: String,
    val lastName: String,
    val occupation: String,
    val availableStatus: AvailableStatus,
    val imageUrl: String,
    val mutualConnectionRequest: Iterable<MutualConnectionRequests>,
    val location: String,
    val socialOptIns: List<SocialMediaItem>,
    val connectionId: String?
) {
    val fullName get() = firstName + if (lastName.isNotBlank()) " $lastName" else ""
}
