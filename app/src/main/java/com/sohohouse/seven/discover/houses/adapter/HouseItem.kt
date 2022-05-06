package com.sohohouse.seven.discover.houses.adapter

import com.sohohouse.seven.base.DiffItem

data class HouseItem(
    val id: String,
    val title: String? = null,
    val city: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val slug: String? = null
) : DiffItem {
    override val key: Any? = id
}