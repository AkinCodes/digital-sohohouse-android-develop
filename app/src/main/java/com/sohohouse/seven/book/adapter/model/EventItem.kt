package com.sohohouse.seven.book.adapter.model

interface EventItem {
    val id: String
    val title: String?
    val imageUrl: String?
    val isFeatured: Boolean
        get() = false
}