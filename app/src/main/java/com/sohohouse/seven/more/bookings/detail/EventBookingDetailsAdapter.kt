package com.sohohouse.seven.more.bookings.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.*
import com.sohohouse.seven.more.bookings.detail.recycler.*
import com.sohohouse.seven.more.bookings.recycler.EventBookingViewHolder

interface MorePastBookingsDetailContactListener {
    fun onContactButtonClicked()
}

class MorePastBookingsDetailAdapter(
    private val dataItems: MutableList<MorePastBookingsDetailAdapterItem>,
    private val contactListener: MorePastBookingsDetailContactListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (MorePastBookingsDetailAdapterItemType.values()[viewType]) {
            MorePastBookingsDetailAdapterItemType.HEADER ->
                MorePastBookingsDetailHeaderViewHolder(
                    MorePastBookingsDetailHeaderBinding.inflate(inflater, parent, false)
                )
            MorePastBookingsDetailAdapterItemType.BOOKING_DETAIL ->
                MorePastBookingsDetailBookingDetailViewHolder(
                    MorePastBookingsDetailBookingDetailBinding
                        .inflate(inflater, parent, false)
                )
            MorePastBookingsDetailAdapterItemType.TEXT_BODY ->
                MorePastBookingsDetailTextViewHolder(
                    MorePastBookingsDetailTextBinding.inflate(inflater, parent, false)
                )
            MorePastBookingsDetailAdapterItemType.CARD -> {
                EventBookingViewHolder(
                    MorePastBookingsDetailCardBinding.inflate(inflater, parent, false)
                )
            }
            MorePastBookingsDetailAdapterItemType.CONTACT ->
                MorePastBookingsDetailContactViewHolder(
                    MorePastBookingsContactCtaBinding.inflate(inflater, parent, false)
                )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = dataItems[position]) {
            is MorePastBookingsDetailHeaderAdapterItem -> {
                (holder as MorePastBookingsDetailHeaderViewHolder).bind(item.headerString)
            }
            is MorePastBookingsDetailBookingDetailAdapterItem -> {
                (holder as MorePastBookingsDetailBookingDetailViewHolder).bind(
                    item.titleString, item.supportingString, item.isLastItem
                )
            }
            is MorePastBookingsDetailTextAdapterItem -> {
                (holder as MorePastBookingsDetailTextViewHolder).bind(item.textString)
            }
            is MorePastBookingsDetailCardAdapterItem -> {
                (holder as EventBookingViewHolder).bind(item)
            }
            is MorePastBookingsDetailContactAdapterItem ->
                (holder as MorePastBookingsDetailContactViewHolder).bind(contactListener)
        }
    }

    override fun getItemCount(): Int {
        return dataItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return dataItems[position].itemType.ordinal
    }
}
