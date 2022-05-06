package com.sohohouse.seven.more.bookings.detail

import com.sohohouse.seven.base.mvpimplementation.ViewController
import com.sohohouse.seven.more.bookings.detail.recycler.MorePastBookingsDetailAdapterItem

interface EventBookingDetailsViewController : ViewController {
    fun onDataReady(itemList: MutableList<MorePastBookingsDetailAdapterItem>)
}