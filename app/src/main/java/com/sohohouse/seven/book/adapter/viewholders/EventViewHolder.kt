package com.sohohouse.seven.book.adapter.viewholders

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.book.adapter.model.EventBookAdapterItem
import com.sohohouse.seven.book.adapter.model.EventItem
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.views.*
import com.sohohouse.seven.common.views.carousel.CarouselEventItems
import com.sohohouse.seven.network.core.models.Event
import java.util.*

abstract class EventViewHolder(
    itemView: View, val eventImage: ImageView
) : RecyclerView.ViewHolder(itemView) {

    protected abstract val houseName: HouseNameTextView
    protected abstract val eventName: TextView
    protected abstract val eventDateAndTime: TextView
    protected abstract val eventStatus: StatusView
    protected abstract val eventBookingStatus: TextView
    open val eventCategory: ImageView? = null

    init {
        eventImage.clipToOutline = true
    }

    fun bind(
        item: EventBookAdapterItem,
        onItemClicked: (event: EventItem, imageView: ImageView, position: Int) -> Unit
    ) {
        bindEvent(item.event, item.eventStatus, item.venueTimeZone)
        bindCategory(item.categoryUrl, item.categoryName)
        houseName.text =
            if (item.event.isDigitalEvent)
                getString(R.string.event_digital_event)
            else
                item.venueName

        itemView.setOnClickListener {
            onItemClicked(item, eventImage, adapterPosition)
        }
    }

    private fun bindCategory(categoryUrl: String?, categoryName: String?) {
        eventCategory?.let {
            it.setImageFromUrl(categoryUrl)
            it.contentDescription = getString(R.string.alt_explore_events_filter_category_icon)
                .replaceBraces(categoryName ?: return@let)
        }
    }

    fun bind(item: CarouselEventItems) {
        bindEvent(item.event, item.eventStatusType, item.venueTimeZone)
        houseName.text =
            if (item.event.isDigitalEvent) getString(R.string.event_digital_event) else item.venueName
    }

    fun bind(event: Event, eventStatustype: EventStatusType, timeZone: String?) {
        bindEvent(event, eventStatustype, timeZone)
    }

    private fun showEventStatus(status: EventStatusType, openDate: String? = null) {

        eventStatus.setupLayout(status, openDate)
        eventStatus.visibility = VISIBLE

        if (status == EventStatusType.LIVE_NOW) {
            houseName.setGone()
        } else {
            houseName.setVisible()
        }
    }

    private fun showEventBookingStatus(
        bookingState: UserBookingState, numberOfGuests: Int,
        isInLottery: Boolean, isTicketless: Boolean
    ) {
        eventBookingStatus.visibility = VISIBLE
        when {
            isInLottery -> eventBookingStatus.setText(
                getString(R.string.explore_events_event_lottery_card_label).toUpperCase(
                    Locale.US
                ), itemView.getAttributeColor(R.attr.colorEventLottery)
            )
            isTicketless -> eventBookingStatus.text = ""
            bookingState == UserBookingState.GUEST_LIST -> {
                eventBookingStatus.setText(
                    if (numberOfGuests > 0) {
                        getQuantityString(
                            R.plurals.explore_events_event_guests_card_label,
                            numberOfGuests
                        ).replaceBraces(
                            numberOfGuests.toString()
                        )
                    } else {
                        getString(R.string.explore_events_event_booked_success_label)
                    }, itemView.getAttributeColor(bookingState.colorAttr)
                )
            }
            bookingState == UserBookingState.WAIT_LIST -> eventBookingStatus.setText(
                R.string.explore_events_event_waiting_success_label,
                itemView.getAttributeColor(bookingState.colorAttr)
            )
            bookingState == UserBookingState.HELD -> eventBookingStatus.setText(
                R.string.explore_events_event_pending_label,
                itemView.getAttributeColor(bookingState.colorAttr)
            )
        }
    }

    private fun setEventName(text: String) {
        eventName.text = text.requiresErrorMessage(itemView.context)
    }

    private fun setEventDateAndTime(text: String) {
        eventDateAndTime.text = text.requiresErrorMessage(itemView.context)
    }

    protected fun bindEvent(
        event: Event,
        eventStatusType: EventStatusType? = null,
        timeZone: String? = null
    ) {
        val imageUrl = event.images?.large

        eventImage.setImageFromUrl(imageUrl)

        setEventName(event.name)
        setEventDateAndTime(event.startsAt?.getFormattedDateTime(timeZone) ?: "")

        eventBookingStatus.visibility = GONE
        eventStatus.visibility = GONE
        val booking = event.booking?.get(event.document)
        val bookingState = UserBookingState.getState(booking?.bookingType.let {
            it?.let { bookingType -> BookingType.valueOf(bookingType) }
        },
            booking?.state?.let { state -> BookingState.valueOf(state) })
        if (bookingState != null) {
            showEventBookingStatus(
                bookingState,
                booking?.numberOfGuests
                    ?: 0,
                event.isPendingLotteryState() && (booking?.state == BookingState.CONFIRMED.name),
                event.isTicketless
            )
        } else {
            eventStatusType?.let {
                showEventStatus(eventStatusType)
            }
        }
    }

}
