package com.sohohouse.seven.common.design.carousel

import com.sohohouse.seven.base.DiffItem

interface CarouselHeader : DiffItem {
    val title: Int
    val subtitle: Int
    val hasMore: Boolean

    override val key: Any
        get() = title
}