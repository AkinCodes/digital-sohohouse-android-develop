package com.sohohouse.seven.book.eventdetails.eventstatus

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.book.eventdetails.EventDetailsAdapterItemType
import com.sohohouse.seven.book.eventdetails.bookingsuccess.BookingSuccessDescriptionViewHolder
import com.sohohouse.seven.book.eventdetails.bookingsuccess.BookingSuccessTicketlessOverviewViewHolder
import com.sohohouse.seven.book.eventdetails.bookingsuccess.EventBookingSuccessDescriptionItem
import com.sohohouse.seven.book.eventdetails.bookingsuccess.EventBookingTicketlessAdapterItem
import com.sohohouse.seven.common.views.eventdetaillist.BaseEventDetailsAdapter
import com.sohohouse.seven.common.views.eventdetaillist.BaseEventDetailsAdapterItem
import com.sohohouse.seven.databinding.BookingSuccessDescriptionBinding
import com.sohohouse.seven.databinding.BookingSuccessOverviewLayoutBinding
import com.sohohouse.seven.databinding.BookingSuccessTicketlessOverviewLayoutBinding

interface EventStatusAdapterListener {
    fun onBackPress()
}

class EventStatusAdapter(
    eventData: List<BaseEventDetailsAdapterItem>, val listener: EventStatusAdapterListener
) : BaseEventDetailsAdapter(eventData) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (EventDetailsAdapterItemType.values()[viewType]) {
            EventDetailsAdapterItemType.BOOKING_SUCCESS_OVERVIEW -> {
                val binding = BookingSuccessOverviewLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                EventStatusOverviewViewHolder(binding)
            }
            EventDetailsAdapterItemType.BOOKING_SUCCESS_TICKETLESS_OVERVIEW -> {
                val binding = BookingSuccessTicketlessOverviewLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                BookingSuccessTicketlessOverviewViewHolder(binding)
            }
            EventDetailsAdapterItemType.DESCRIPTION -> {
                val binding = BookingSuccessDescriptionBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                BookingSuccessDescriptionViewHolder(binding)
            }
            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (val item = data[position]) {
            is EventStatusAdapterItem -> {
                (holder as EventStatusOverviewViewHolder).bind(item, listener)
            }
            is EventBookingTicketlessAdapterItem -> {
                (holder as BookingSuccessTicketlessOverviewViewHolder).bind(item)
            }
            is EventBookingSuccessDescriptionItem -> {
                (holder as BookingSuccessDescriptionViewHolder).bind(item)
            }
            else -> super.onBindViewHolder(holder, position)
        }
    }
}