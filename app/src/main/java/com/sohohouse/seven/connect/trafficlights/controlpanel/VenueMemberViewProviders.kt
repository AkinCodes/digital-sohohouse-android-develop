package com.sohohouse.seven.connect.trafficlights.controlpanel

import com.sohohouse.seven.connect.VenueMember
import com.sohohouse.seven.connect.trafficlights.AvailableStatus

class VenueMemberViewProviders(
    private val venueMembers: Collection<VenueMember>,
    private val availableStatus: AvailableStatus,
    private val estimatedTotal: Int,
    private val threshold: Int
) : Iterable<VenueMemberViewProvider> {

    override fun iterator(): Iterator<VenueMemberViewProvider> = if (venueMembers.isNotEmpty())
        mapAvailableStatus().iterator()
    else
        List(PLACE_HOLDER_COUNT_WHEN_EMPTY) {
            VenueMemberViewProvider.PlaceHolder()
        }.iterator()

    private fun mapAvailableStatus(): List<VenueMemberViewProvider> {
        return when (availableStatus) {
            AvailableStatus.AVAILABLE -> venueMembers
                .take(threshold)
                .map { VenueMemberViewProvider.NotBlurred(it.imageUrl) }
            AvailableStatus.CONNECTIONS_ONLY -> venueMembers
                .sortedByDescending { it.isConnection }
                .take(threshold)
                .map {
                    if (it.isConnection)
                        VenueMemberViewProvider.NotBlurred(it.imageUrl)
                    else
                        VenueMemberViewProvider.Blurred(it.imageUrl)
                }
            AvailableStatus.UNAVAILABLE -> venueMembers
                .take(threshold)
                .map { VenueMemberViewProvider.Blurred(it.imageUrl) }
        }.let { immutableList ->
            if (venueMembers.size <= threshold)
                immutableList
            else
                immutableList.toMutableList().also { mutableList ->
                    mutableList.add(VenueMemberViewProvider.ShowMore(estimatedTotal - threshold))
                }
        }
    }

    private companion object {
        private const val PLACE_HOLDER_COUNT_WHEN_EMPTY = 0
    }

}