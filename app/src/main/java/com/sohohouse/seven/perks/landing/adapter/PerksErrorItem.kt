package com.sohohouse.seven.perks.landing.adapter

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.network.core.models.Perk

data class PerksErrorItem(val item: Perk) : DiffItem {
    override val key: Any
        get() = this
}