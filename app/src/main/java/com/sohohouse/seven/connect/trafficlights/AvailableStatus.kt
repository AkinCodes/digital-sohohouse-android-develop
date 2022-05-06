package com.sohohouse.seven.connect.trafficlights

import androidx.annotation.AttrRes
import com.sohohouse.seven.R
import com.sohohouse.seven.common.analytics.AnalyticsManager

enum class AvailableStatus(
    val value: String,
    @AttrRes val colorAttrRes: Int,
    val eventAction: AnalyticsManager.Action
) {

    AVAILABLE(
        "available",
        R.attr.colorTrafficLightAvailable,
        AnalyticsManager.Action.TrafficLightsCheckingAvailable
    ),
    CONNECTIONS_ONLY(
        "only_connected",
        R.attr.colorTrafficLightConnectionsOnly,
        AnalyticsManager.Action.TrafficLightsCheckingConnectionsOnly
    ),
    UNAVAILABLE(
        "unavailable",
        R.attr.colorTrafficLightUnavailable,
        AnalyticsManager.Action.TrafficLightsCheckingUnavailable
    );

    companion object {
        fun from(value: String) = when (value) {
            AVAILABLE.value -> AVAILABLE
            CONNECTIONS_ONLY.value -> CONNECTIONS_ONLY
            UNAVAILABLE.value -> UNAVAILABLE
            else -> error("Unknown AvailableStatus: $value")
        }
    }
}