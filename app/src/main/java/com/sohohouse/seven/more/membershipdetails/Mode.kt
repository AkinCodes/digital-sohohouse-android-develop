package com.sohohouse.seven.more.membershipdetails

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class Mode : Parcelable {
    DETAILS,
    CARD_ONLY
}