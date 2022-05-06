package com.sohohouse.seven.connect.trafficlights.repo

import com.sohohouse.seven.connect.VenueMember
import com.sohohouse.seven.connect.trafficlights.AvailableStatus
import com.sohohouse.seven.connect.trafficlights.VenueMembers
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either

interface TrafficLightsRepo {

    suspend fun getVenueMembers(
        isInitialLoad: Boolean,
        page: Int = 1,
        perPage: Int
    ): Either<ServerError, VenueMembers>

    fun updateSentConnection(venueMember: VenueMember)

    fun updateConnectedConnection(venueMember: VenueMember)

    suspend fun getUserStatus(): Either<ServerError, AvailableStatus>

    suspend fun updateStatus(availableStatus: AvailableStatus): Either<ServerError, AvailableStatus>

    suspend fun clearStatus(): Either<ServerError, Unit>

    fun clearCache()

}
