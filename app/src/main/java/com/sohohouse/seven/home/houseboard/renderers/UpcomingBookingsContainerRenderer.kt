package com.sohohouse.seven.home.houseboard.renderers

import android.view.View
import ca.symbilityintersect.rendereradapter.BaseRenderer
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ItemUpcomingBookingsCarouselBinding
import com.sohohouse.seven.home.houseboard.items.UpcomingBookingsContainerItem
import com.sohohouse.seven.home.houseboard.viewholders.UpcomingBookingsContainerViewHolder
import com.sohohouse.seven.more.bookings.recycler.EventBookingAdapterItem
import com.sohohouse.seven.more.bookings.recycler.TableBookingAdapterItem
import com.sohohouse.seven.network.core.models.RoomBooking

interface UpcomingBookingsListener {
    fun onRoomBookingClick(roomBooking: RoomBooking)
    fun onEventBookingClick(eventBooking: EventBookingAdapterItem)
    fun onTableBookingClick(tableBooking: TableBookingAdapterItem)
    fun onSeeAllClick()
}

class UpcomingBookingsContainerRenderer(val listener: UpcomingBookingsListener) :
    BaseRenderer<UpcomingBookingsContainerItem, UpcomingBookingsContainerViewHolder>(
        UpcomingBookingsContainerItem::class.java
    ) {
    override fun bindViewHolder(
        p0: UpcomingBookingsContainerItem?,
        p1: UpcomingBookingsContainerViewHolder?
    ) {
        p0?.let { p1?.bind(it, listener) }
    }

    override fun getLayoutResId() = R.layout.item_upcoming_bookings_carousel

    override fun createViewHolder(view: View): UpcomingBookingsContainerViewHolder {
        return UpcomingBookingsContainerViewHolder(ItemUpcomingBookingsCarouselBinding.bind(view))
    }

}