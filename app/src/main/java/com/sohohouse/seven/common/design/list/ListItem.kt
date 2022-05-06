package com.sohohouse.seven.common.design.list

import com.sohohouse.seven.base.DiffItem

interface ListItem : DiffItem {
    val id: String
    val title: String?
    val subtitle: String?
    val label: String?
    val imageUrl: String?

    override val key: Any
        get() = id

}