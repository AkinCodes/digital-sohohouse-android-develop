package com.sohohouse.seven.more.bookings.detail

import android.content.Context
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.getFormattedDateTime
import com.sohohouse.seven.common.extensions.getLocationColor
import com.sohohouse.seven.common.utils.CurrencyUtils
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.common.views.BookingState
import com.sohohouse.seven.more.bookings.detail.recycler.*
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.EventBooking
import com.sohohouse.seven.network.core.models.Venue
import javax.inject.Inject

class EventBookingDetailsPresenter @Inject constructor(private val venueRepo: VenueRepo) :
    BasePresenter<EventBookingDetailsViewController>() {

    lateinit var booking: EventBooking
    lateinit var event: Event
    lateinit var venue: Venue

    override fun onAttach(
        view: EventBookingDetailsViewController,
        isFirstAttach: Boolean,
        isRecreated: Boolean
    ) {
        super.onAttach(view, isFirstAttach, isRecreated)
        view.setScreenName(AnalyticsManager.Screens.PastBookingsDetail.name)
    }

    fun setup(booking: EventBooking, context: Context) {
        this.booking = booking
        event = booking.event ?: Event()
        venue = venueRepo.venues().findById(booking.venue.id) ?: Venue()
        createData(context)
    }

    private fun createData(context: Context) {
        val dateAndTime = event.startsAt?.getFormattedDateTime(venue.timeZone) ?: ""
        val imageUrl = event.images?.large ?: ""
        val bookingStatus = when (booking.state?.let { BookingState.valueOf(it) }) {
            BookingState.CONFIRMED -> context.getString(R.string.explore_events_booking_details_confirmed_label)
            else -> context.getString(R.string.explore_events_booking_details_cancelled_label)
        }
        val numberOfTickets = (booking.numberOfGuests?.plus(1)) ?: 1
        val priceCurrency = event.priceCurrency ?: ""
        val totalCost = event.priceCents * numberOfTickets
        val cost =
            if (totalCost > 0) CurrencyUtils.getFormattedPrice(totalCost, priceCurrency)
            else context.getString(R.string.explore_events_no_charge_label)

        val data: MutableList<MorePastBookingsDetailAdapterItem> = mutableListOf()
        data.add(
            MorePastBookingsDetailCardAdapterItem(
                venue.name,
                venue.getLocationColor(),
                event.name,
                dateAndTime,
                imageUrl
            )
        )
        data.add(
            MorePastBookingsDetailHeaderAdapterItem(
                context.getString(R.string.explore_events_booking_details_label),
                true
            )
        )
        data.add(
            MorePastBookingsDetailBookingDetailAdapterItem(
                context.getString(R.string.explore_events_booking_details_status_label),
                bookingStatus
            )
        )
        data.add(
            MorePastBookingsDetailBookingDetailAdapterItem(
                context.getString(R.string.explore_events_booking_details_tickets_label),
                numberOfTickets.toString()
            )
        )
        data.add(
            MorePastBookingsDetailBookingDetailAdapterItem(
                context.getString(R.string.explore_events_booking_details_total_label),
                cost,
                true
            )
        )
        data.add(MorePastBookingsDetailHeaderAdapterItem(context.getString(R.string.explore_events_event_details_label)))
        data.add(MorePastBookingsDetailTextAdapterItem(event.description))
        data.add(MorePastBookingsDetailHeaderAdapterItem(context.getString(R.string.explore_events_booking_details_contact_label)))
        data.add(MorePastBookingsDetailContactAdapterItem())

        executeWhenAvailable { view, _, _ ->
            view.onDataReady(data)
        }
    }
}
