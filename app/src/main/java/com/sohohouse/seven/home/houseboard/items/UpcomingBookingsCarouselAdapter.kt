package com.sohohouse.seven.home.houseboard.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.databinding.ItemTableBookingBinding
import com.sohohouse.seven.databinding.ListViewEventCardSmallBinding
import com.sohohouse.seven.home.houseboard.renderers.UpcomingBookingsListener
import com.sohohouse.seven.more.bookings.recycler.*

class UpcomingBookingsCarouselAdapter :
    BaseRecyclerDiffAdapter<RecyclerView.ViewHolder, DiffItem>() {

    companion object {
        const val VIEW_TYPE_ROOM_BOOKING = 111
        const val VIEW_TYPE_EVENT_BOOKING = 222
        const val VIEW_TYPE_TABLE_BOOKING = 333
    }

    var listener: UpcomingBookingsListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_ROOM_BOOKING -> RoomBookingViewHolder(
                ListViewEventCardSmallBinding.inflate(inflater, parent, false),
                lightTheme = true
            )
            VIEW_TYPE_TABLE_BOOKING -> TableBookingViewHolder(
                ItemTableBookingBinding.inflate(inflater, parent, false),
                lightTheme = true
            )
            else -> EventBookingSmallViewHolder(
                ListViewEventCardSmallBinding.inflate(inflater, parent, false),
                lightTheme = true
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        currentItems[position].let { item ->
            when (item) {
                is RoomBookingAdapterItem -> {
                    (holder as RoomBookingViewHolder).bind(item) {
                        listener?.onRoomBookingClick(it.roomBooking)
                    }
                }
                is EventBookingAdapterItem -> {
                    (holder as EventBookingSmallViewHolder).bind(item) {
                        listener?.onEventBookingClick(it)
                    }
                }
                is TableBookingAdapterItem -> {
                    (holder as TableBookingViewHolder).bind(item) { listener?.onTableBookingClick(it) }
                }
                else -> {}
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (currentItems[position]) {
            is RoomBookingAdapterItem -> VIEW_TYPE_ROOM_BOOKING
            is TableBookingAdapterItem -> VIEW_TYPE_TABLE_BOOKING
            else -> VIEW_TYPE_EVENT_BOOKING
        }
    }
}