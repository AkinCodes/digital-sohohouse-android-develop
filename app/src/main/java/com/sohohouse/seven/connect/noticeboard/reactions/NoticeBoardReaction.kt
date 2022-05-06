package com.sohohouse.seven.connect.noticeboard.reactions

import android.os.Parcelable
import com.sohohouse.seven.network.core.models.Reaction
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoticeBoardReaction(
    val reaction: Reaction,
    val iconUrl: String,
) : Parcelable