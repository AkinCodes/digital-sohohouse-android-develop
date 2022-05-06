package com.sohohouse.seven.connect.trafficlights.repo

import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.ZipRequestsUtil
import com.sohohouse.seven.connect.VenueMember
import com.sohohouse.seven.connect.trafficlights.AvailableStatus
import com.sohohouse.seven.connect.trafficlights.UserAvailableStatus
import com.sohohouse.seven.connect.trafficlights.VenueMembers
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.empty
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.Connection
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.network.core.models.ProfileAvailabilityStatus
import com.sohohouse.seven.network.core.request.*
import com.sohohouse.seven.profile.*

class TrafficLightsRepoImpl(
    private val zipRequestsUtil: ZipRequestsUtil,
    private val userManager: UserManager,
    private val profileToMutualConnectionStatus: ProfileToMutualConnectionStatus
) : TrafficLightsRepo {

    private var cachedMembers = VenueMembers(
        venueMembers = mutableListOf(),
        estimatedTotal = 0,
        nextPage = 0
    )

    override suspend fun getVenueMembers(
        isInitialLoad: Boolean,
        page: Int,
        perPage: Int,
    ): Either<ServerError, VenueMembers> {
        if (isInitialLoad && cachedMembers.isNotEmpty()) {
            return value(cachedMembers)
        }
        val listRequest = GetProfileAvailabilityStatusPageableRequest(
            page = page,
            perPage = perPage
        )
        val myRequest = GetProfileAvailabilityStatusRequest()
        var estimatedTotal = 0
        var nextPage = 0
        var venueID: String? = null
        return zipRequestsUtil.issueApiCall(listRequest, myRequest).fold(
            ifValue = { (listResponse, myResponse) ->
                val profileAvailabilityStatuses = responseToProfileAvailabilityStatuses(
                    listResponse = listResponse,
                    myResponse = myResponse
                )

                listResponse.ifValue {
                    if (it.isEmpty() && page > 1) return empty()

                    estimatedTotal = (listRequest.getMeta(it)?.estimatedTotal ?: 0) + 1
                    nextPage = (listRequest.getMeta(it)?.page ?: 0) + 1
                }
                myResponse.ifValue {
                    venueID = it.venueResource?.get()?.id ?: ""
                }

                profileAvailabilityStatuses.ifEmpty {
                    return Either.Empty()
                }.filterIsNotCurrentUser().map {
                    getVenueMemberFromProfileAvailabilityStatus(
                        it.profile?.get()?.id ?: "",
                        AvailableStatus.from(it.status),
                        myProfileId = userManager.profileID
                    )
                }.let(::value)
            },
            ifError = { Either.Error(it) },
            ifEmpty = { Either.Empty() }
        ).foldAndMapToVenueMembersList(
            estimatedTotal = estimatedTotal,
            nextPage = nextPage,
            venueID = venueID
        ).ifValue { setUpCachedMembers(isInitialLoad, it, estimatedTotal, page) }
    }

    private fun setUpCachedMembers(
        isInitialLoad: Boolean,
        it: VenueMembers,
        estimatedTotal: Int,
        page: Int
    ) {
        if (isInitialLoad) {
            cachedMembers = it
            cachedMembers.estimatedTotal = estimatedTotal
        } else {
            cachedMembers.addAll(it)
            cachedMembers.nextPage = page + 1
            cachedMembers.estimatedTotal = estimatedTotal
        }
    }

    override fun updateSentConnection(venueMember: VenueMember) {
        if (cachedMembers.contains(venueMember)) {
            val index = cachedMembers.indexOf(venueMember)
            cachedMembers[index] = venueMember.copy(mutualConnectionStatus = RequestSent)
        }
    }

    override fun updateConnectedConnection(venueMember: VenueMember) {
        if (cachedMembers.contains(venueMember)) {
            val index = cachedMembers.indexOf(venueMember)
            cachedMembers[index] = venueMember.copy(mutualConnectionStatus = Connected)
        }
    }

    override suspend fun getUserStatus(): Either<ServerError, AvailableStatus> {
        return zipRequestsUtil.issueApiCall(GetProfileAvailabilityStatusRequest()).fold(
            ifValue = {
                val availableStatus = AvailableStatus.from(it.status)
                userManager.saveAvailabilityStatus(
                    UserAvailableStatus(
                        id = it.id,
                        availableStatus = AvailableStatus.from(it.status)
                    )
                )
                value(availableStatus)
            },
            ifError = { Either.Error(it) },
            ifEmpty = { Either.Empty() }
        )
    }

    private fun Either<ServerError, List<Either<ServerError, VenueMember>>>.foldAndMapToVenueMembersList(
        estimatedTotal: Int,
        nextPage: Int,
        venueID: String?
    ): Either<ServerError, VenueMembers> {
        return fold(
            ifError = { Either.Error(it) },
            ifEmpty = { Either.Empty() },
            ifValue = {
                it.filterIsInstance<Either.Value<VenueMember>>()
                    .map { either -> either.value }
                    .let { venueMembers ->
                        VenueMembers(
                            venueMembers = venueMembers.toMutableList(),
                            estimatedTotal = estimatedTotal,
                            nextPage = nextPage,
                            venueID = venueID
                        )
                    }.let(::value)
            }
        )
    }

    private fun responseToProfileAvailabilityStatuses(
        listResponse: Either<ServerError, List<ProfileAvailabilityStatus>>,
        myResponse: Either<ServerError, ProfileAvailabilityStatus>,
    ): List<ProfileAvailabilityStatus> {
        val profileAvailabilityStatuses = mutableListOf<ProfileAvailabilityStatus>()
        listResponse.ifValue {
            profileAvailabilityStatuses.addAll(it)
        }
        myResponse.ifValue { profileAvailabilityStatuses.add(it) }
        return profileAvailabilityStatuses
    }

    private fun List<ProfileAvailabilityStatus>.filterIsNotCurrentUser(): List<ProfileAvailabilityStatus> =
        filterNot {
            val isCurrentUser = userManager.profileID == it.profile?.get()?.id
            if (isCurrentUser) {
                userManager.saveAvailabilityStatus(
                    UserAvailableStatus(
                        it.id,
                        AvailableStatus.from(it.status)
                    )
                )
            }
            isCurrentUser
        }

    private fun getVenueMemberFromProfileAvailabilityStatus(
        profileId: String,
        availableStatus: AvailableStatus,
        myProfileId: String
    ) = zipRequestsUtil.issueApiCall(GetProfileRequest(profileId)).fold(
        ifEmptyOrError = {
            Either.Empty()
        },
        ifValue = { profile ->
            value(
                VenueMember(
                    isConnection = profile.mutualConnections.isNotEmpty(),
                    firstName = profile.firstName,
                    lastName = profile.lastName,
                    occupation = profile.occupation ?: "",
                    availableStatus = availableStatus,
                    imageUrl = profile.imageUrl,
                    id = profileId,
                    mutualConnectionStatus = profileToMutualConnectionStatus.map(profile),
                    mutualConnectionRequest = profile.mutualConnectionRequest,
                    location = profile.city ?: "",
                    socialOptIns = ProfileAdapterItemFactory.createConnectedAccount(profile),
                    connectionId = getConnectionId(profile, myProfileId = myProfileId)
                )
            )
        }
    )

    private fun getConnectionId(
        profile: Profile,
        myProfileId: String
    ): String? {
        profile.mutualConnectionRequest.firstOrNull {
            Connection.getValidConnectionId(myProfileId, it) != null
        }?.let { return it.id }

        return profile.mutualConnections.firstOrNull {
            Connection.getValidConnectionId(myProfileId, it) != null
        }?.id
    }

    override suspend fun updateStatus(availableStatus: AvailableStatus): Either<ServerError, AvailableStatus> {
        return zipRequestsUtil.issueApiCall(
            PatchProfileAvailabilityStatusRequest(ProfileAvailabilityStatus(availableStatus.value))
        ).fold(
            ifValue = {
                val newAvailableStatus = AvailableStatus.from(it.status)
                userManager.saveAvailabilityStatus(
                    UserAvailableStatus(
                        it.id,
                        AvailableStatus.from(it.status)
                    )
                )
                value(newAvailableStatus)
            },
            ifError = { Either.Error(it) },
            ifEmpty = { Either.Empty() }
        )
    }

    override suspend fun clearStatus(): Either<ServerError, Unit> {
        return zipRequestsUtil.issueApiCall(
            DeleteProfileAvailabilityStatusRequest(userManager.availableStatusFlow.value.id)
        ).fold(
            ifValue = { value(Unit) },
            ifError = { Either.Error(it) },
            ifEmpty = { value(Unit) }
        )
    }

    override fun clearCache() {
        cachedMembers = VenueMembers(
            mutableListOf(),
            0,
            0
        )
    }

}