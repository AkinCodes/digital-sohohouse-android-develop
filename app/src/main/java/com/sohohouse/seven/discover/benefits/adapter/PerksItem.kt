package com.sohohouse.seven.discover.benefits.adapter

import androidx.annotation.StringRes
import com.sohohouse.seven.base.DiffItem
import java.util.*

data class PerksItem(
    val id: String = "",
    val title: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val city: String? = null,
    val promoCode: String? = null,
    @StringRes val contentPillar: Int?,
    val expiry: Date? = null
) : DiffItem {
    override val key: Any? = id
}
