package com.sohohouse.seven.common.design.carousel

import com.sohohouse.seven.base.DiffItem

interface Carousel<T : CarouselItem> : DiffItem {
    val items: List<T>

    override val key: Any
        get() = this
}