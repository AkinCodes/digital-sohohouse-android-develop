package com.sohohouse.seven.common.design.carousel

import com.sohohouse.seven.base.DiffItem

interface CarouselItem : DiffItem {
    val id: String
    val title: String
    val subtitle: String
    val caption: Int
    val imageUrl: String?

    override val key: Any
        get() = id
}