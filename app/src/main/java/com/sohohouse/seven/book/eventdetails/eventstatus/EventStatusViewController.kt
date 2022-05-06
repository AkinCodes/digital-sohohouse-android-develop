package com.sohohouse.seven.book.eventdetails.eventstatus

import com.sohohouse.seven.base.mvpimplementation.ViewController
import com.sohohouse.seven.common.views.eventdetaillist.BaseEventDetailsAdapterItem
import com.sohohouse.seven.network.core.models.Event

interface EventStatusViewController : ViewController {
    fun initLayout(event: Event, backgroundColor: Int, buttonText: String, canJoin: Boolean = false)
    fun setUpRecyclerView(data: List<BaseEventDetailsAdapterItem>)
}