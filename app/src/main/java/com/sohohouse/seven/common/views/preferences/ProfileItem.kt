package com.sohohouse.seven.common.views.preferences

import android.os.Parcelable
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.connect.VenueMember
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.*
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfileItem(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val pronouns: ProfileField.Pronouns? = null,
    val occupation: String? = null,
    val location: String? = null,
    val imageUrl: String? = null,
    val socialOptIns: List<SocialMediaItem> = emptyList(),
    val status: MutualConnectionStatus? = null,
    val connectionId: String? = null,
    val isMyself: Boolean = false,
    var isStaff: Boolean = false,
    val showMessageButton: Boolean = true
) : Parcelable, DiffItem {

    constructor(
        profile: Profile,
        status: MutualConnectionStatus? = null,
        connectionId: String? = null,
        showMessageButton: Boolean = true
    ) : this(
        profile.id,
        profile.firstName,
        profile.lastName,
        ProfileFieldFactory.getPronounsField(profile.pronouns),
        profile.occupation,
        profile.city,
        profile.imageUrl,
        ProfileAdapterItemFactory.createConnectedAccount(profile),
        status,
        connectionId,
        isStaff = profile.isStaff,
        showMessageButton = showMessageButton
    )

    constructor(venueMember: VenueMember) : this(
        venueMember.id,
        firstName = venueMember.firstName,
        lastName = venueMember.lastName,
        occupation = venueMember.occupation,
        location = venueMember.location,
        imageUrl = venueMember.imageUrl,
        socialOptIns = venueMember.socialOptIns,
        status = venueMember.mutualConnectionStatus,
        connectionId = null,
        isMyself = false
    )

    override val key: Any
        get() = this

    val fullName: String
        get() = firstName + if (lastName.isNotBlank()) " $lastName" else ""
}