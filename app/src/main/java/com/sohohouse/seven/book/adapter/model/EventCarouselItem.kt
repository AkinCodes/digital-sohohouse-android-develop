package com.sohohouse.seven.book.adapter.model

import com.sohohouse.seven.common.design.carousel.CarouselItem

class EventCarouselItem(
    override val id: String,
    override val title: String,
    override val subtitle: String,
    override val caption: Int,
    override val imageUrl: String?,
    override val isFeatured: Boolean = false
) : CarouselItem, EventItem