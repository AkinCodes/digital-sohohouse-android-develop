package com.sohohouse.seven.connect.mynetwork.blockedprofiles

import com.sohohouse.seven.base.DiffItem

data class BlockedProfile(
    val id: String,
    val profileId: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val occupation: String,
    val imageUrl: String?,
) : DiffItem