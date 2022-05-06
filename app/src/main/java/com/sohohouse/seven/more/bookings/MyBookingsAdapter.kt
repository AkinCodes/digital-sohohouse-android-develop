package com.sohohouse.seven.more.bookings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter
import com.sohohouse.seven.common.viewholders.ErrorStateListener
import com.sohohouse.seven.common.viewholders.ErrorStateViewHolder
import com.sohohouse.seven.databinding.*
import com.sohohouse.seven.more.bookings.recycler.*
import com.sohohouse.seven.more.bookings.recycler.BookingAdapterItemType.*

interface MorePastBookingsAdapterListener : ErrorStateListener {
    fun onEventBookingClick(eventBooking: EventBookingAdapterItem)
    fun onTableBookingClick(tableBookingAdapterItem: TableBookingAdapterItem)
    fun onExploreButtonClicked()
    fun onMonthHeaderClick(item: PastBookingsCollapsableMonthItem) {}
    fun onRoomBookingClick(it: RoomBookingAdapterItem)
}

class MyBookingsAdapter constructor(private val adapterListener: MorePastBookingsAdapterListener) :
    BaseRecyclerDiffAdapter<RecyclerView.ViewHolder, BookingAdapterItem>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (values()[viewType]) {
            DATE_HEADER -> EventBookingsDateViewHolder(
                MorePreviousBookingsDateBinding.inflate(inflater, parent, false)
            )
            EVENT_BOOKING -> EventBookingViewHolder(
                MorePastBookingsDetailCardBinding.inflate(inflater, parent, false)
            )
            EMPTY -> BookingsEmptyViewHolder(
                MorePreviousEmptyBinding.inflate(inflater, parent, false)
            )
            ERROR -> ErrorStateViewHolder(
                MorePastBookingsReloadableErrorStateBinding.inflate(inflater, parent, false)
            )
            COLLAPSIBLE_MONTH -> PastBookingsCollapsibleMonthHeaderViewHolder(
                ItemPastBookingsCollabsibleMonthBinding.inflate(inflater, parent, false)
            )
            EMPTY_MONTH -> PastBookingsEmptyMonthViewHolder(parent)
            ROOM_BOOKING -> RoomBookingViewHolder(
                ItemBookingListItemBinding.inflate(inflater, parent, false)
            )
            HEADER -> BookingsHeaderTextViewHolder(
                ItemBookingsHeaderTextBinding.inflate(inflater, parent, false)
            )
            TABLE_BOOKING -> TableBookingViewHolder(
                ItemTableBookingBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is UpcomingBookingsDateAdapterItem -> {
                (holder as EventBookingsDateViewHolder).bind(item.formattedDate)
            }
            is EventBookingAdapterItem -> {
                (holder as EventBookingViewHolder).bind(item) {
                    adapterListener.onEventBookingClick(it)
                }
            }
            is BookingEmptyAdapterItem -> {
                (holder as BookingsEmptyViewHolder).bind(item) {
                    adapterListener.onExploreButtonClicked()
                }
            }
            is BookingErrorStateAdapterItem -> {
                (holder as ErrorStateViewHolder).reloadClicks {
                    adapterListener.onReloadButtonClicked()
                }
            }
            is PastBookingsCollapsableMonthItem -> {
                (holder as PastBookingsCollapsibleMonthHeaderViewHolder).bind(item) {
                    adapterListener.onMonthHeaderClick(it)
                }
            }
            is RoomBookingAdapterItem -> {
                (holder as RoomBookingViewHolder).bind(item) {
                    adapterListener.onRoomBookingClick(it)
                }
            }
            is BookingsHeaderTextItem -> {
                (holder as BookingsHeaderTextViewHolder).bind(item)
            }
            is TableBookingAdapterItem -> {
                (holder as TableBookingViewHolder).bind(item) {
                    adapterListener.onTableBookingClick(it)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType.ordinal
    }

}
