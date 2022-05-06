package com.sohohouse.seven.profile

import android.os.Parcelable
import com.sohohouse.seven.common.extensions.contains
import com.sohohouse.seven.network.core.models.Profile
import kotlinx.android.parcel.Parcelize

sealed class MutualConnectionStatus : Parcelable {

    companion object {
        fun from(
            profile: Profile,
            myProfileId: String,
            blockedMembers: List<String>
        ): MutualConnectionStatus {
            return when {
                profile.id == myProfileId -> NotAvailableMySelf
                blockedMembers.contains(profile.id) -> Blocked
                profile.mutualConnections.isNotEmpty() -> Connected
                profile.mutualConnectionRequest.contains { it.sender.get().id == myProfileId } -> RequestSent
                profile.mutualConnectionRequest.contains { it.receiver.get().id == myProfileId } -> RequestReceived
                profile.mutualConnectionRequest.isNotEmpty() -> NotAvailable
                else -> NotConnected
            }
        }
    }
}

@Parcelize
object NotAvailableMySelf : MutualConnectionStatus()

@Parcelize
object NotConnected : MutualConnectionStatus()

@Parcelize
object RequestSent : MutualConnectionStatus()

@Parcelize
object RequestReceived : MutualConnectionStatus()

@Parcelize
object Connected : MutualConnectionStatus()

@Parcelize
object NotAvailable : MutualConnectionStatus()

@Parcelize
object Blocked : MutualConnectionStatus()