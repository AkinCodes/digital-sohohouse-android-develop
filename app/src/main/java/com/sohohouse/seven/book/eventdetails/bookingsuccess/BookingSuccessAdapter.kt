package com.sohohouse.seven.book.eventdetails.bookingsuccess

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.book.eventdetails.EventDetailsAdapterItemType
import com.sohohouse.seven.common.views.eventdetaillist.BaseEventDetailsAdapter
import com.sohohouse.seven.common.views.eventdetaillist.BaseEventDetailsAdapterItem
import com.sohohouse.seven.databinding.BookingSuccessDescriptionBinding
import com.sohohouse.seven.databinding.BookingSuccessOverviewLayoutBinding
import com.sohohouse.seven.databinding.BookingSuccessTicketlessOverviewLayoutBinding

interface BookingSuccessAdapterListener {
    fun onBackClicked()
}

class BookingSuccessAdapter(
    private val listener: BookingSuccessAdapterListener,
    eventData: List<BaseEventDetailsAdapterItem>
) : BaseEventDetailsAdapter(eventData) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (EventDetailsAdapterItemType.values()[viewType]) {
            EventDetailsAdapterItemType.BOOKING_SUCCESS_OVERVIEW -> {
                val binding = BookingSuccessOverviewLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                BookingSuccessOverviewViewHolder(binding)
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
        val item = data[position]

        when (item) {
            is EventBookingSuccessAdapterItem -> {
                (holder as BookingSuccessOverviewViewHolder).bind(item, listener)
            }
            is EventBookingTicketlessAdapterItem -> {
                (holder as BookingSuccessTicketlessOverviewViewHolder).bind(item, listener)
            }
            is EventBookingSuccessDescriptionItem -> {
                (holder as BookingSuccessDescriptionViewHolder).bind(item)
            }
            else -> super.onBindViewHolder(holder, position)
        }
    }
}