@file:Suppress("TestFunctionName")

package com.sohohouse.seven.connect.trafficlights

import com.sohohouse.seven.connect.VenueMember
import com.sohohouse.seven.profile.MutualConnectionStatus
import com.sohohouse.seven.profile.NotConnected
import java.util.*

fun FakeVenueMember(
    availableStatus: AvailableStatus = AvailableStatus.AVAILABLE,
    isConnection: Boolean = true,
    mutualConnectionStatus: MutualConnectionStatus = NotConnected
) = VenueMember(
    id = "Fake-${UUID.randomUUID()}",
    mutualConnectionStatus = mutualConnectionStatus,
    isConnection = isConnection,
    firstName = "Sir",
    lastName = "Mars King De Burjua.",
    mutualConnectionRequest = emptyList(),
    location = "Mars",
    availableStatus = availableStatus,
    imageUrl = "",
    occupation = "Motivational Speaker",
    socialOptIns = emptyList(),
    connectionId = null
)