package com.sohohouse.seven.perks.landing.adapter

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.network.core.models.Perk

class PerksItem(
    val item: Perk,
    val venueName: String = ""
) : DiffItem {
    override val key: Any?
        get() = item.id
}