package com.sohohouse.seven.common.design.card

import com.sohohouse.seven.base.DiffItem

interface XLargeCard : DiffItem {
    val id: String
    val title: String
    val imageUrl: String?

    override val key: Any
        get() = id
}