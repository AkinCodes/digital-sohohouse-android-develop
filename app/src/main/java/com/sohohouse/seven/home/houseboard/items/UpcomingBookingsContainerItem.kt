package com.sohohouse.seven.home.houseboard.items

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.more.bookings.recycler.BookingAdapterItem

data class UpcomingBookingsContainerItem(
    val items: List<BookingAdapterItem>,
    val showSeeAllBtn: Boolean
) : DiffItem {
    override val key: Any?
        get() = UpcomingBookingsContainerItem::class
}