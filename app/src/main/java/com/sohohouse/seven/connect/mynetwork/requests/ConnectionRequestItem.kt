package com.sohohouse.seven.connect.mynetwork.requests

import android.os.Parcelable
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.network.core.models.Connection
import com.sohohouse.seven.profile.RequestReceived
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class ConnectionRequestItem(
    val id: String,
    val message: String?,
    val profile: ProfileItem,
    val createdAt: Date? = null
) : DiffItem, Parcelable {

    constructor(connection: Connection) : this(
        connection.id,
        connection.message,
        ProfileItem(connection.sender.get(connection.document), RequestReceived, connection.id),
        connection.createdAt
    )
}