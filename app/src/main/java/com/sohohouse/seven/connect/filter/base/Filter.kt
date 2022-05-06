package com.sohohouse.seven.connect.filter.base

import android.os.Parcelable
import com.sohohouse.seven.base.DiffItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Filter(val id: String, val title: String) : Parcelable, DiffItem {
    override val key: Any
        get() = id

    constructor(item: FilterItem) : this(item.id, item.title)
}
