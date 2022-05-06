package com.sohohouse.seven.book.eventdetails.bookingsuccess

import com.sohohouse.seven.base.mvpimplementation.ViewController
import com.sohohouse.seven.common.views.eventdetaillist.BaseEventDetailsAdapterItem

interface BookingSuccessViewController : ViewController {
    fun initLayout()
    fun setUpRecyclerView(data: List<BaseEventDetailsAdapterItem>)
}