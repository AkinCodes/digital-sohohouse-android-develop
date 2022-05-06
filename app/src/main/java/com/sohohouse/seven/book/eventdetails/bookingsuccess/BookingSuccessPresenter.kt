package com.sohohouse.seven.book.eventdetails.bookingsuccess

import com.sohohouse.seven.R
import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.getFormattedDateTime
import com.sohohouse.seven.common.views.eventdetaillist.EventGuestAdapterItem

class BookingSuccessPresenter : BasePresenter<BookingSuccessViewController>() {
    override fun onAttach(
        view: BookingSuccessViewController,
        isFirstAttach: Boolean,
        isRecreated: Boolean
    ) {
        super.onAttach(view, isFirstAttach, isRecreated)
        view.setScreenName(AnalyticsManager.Screens.BookingConfirmation.name)
        if (isFirstAttach) {
            executeWhenAvailable { v, _, _ -> v.initLayout() }
        }
    }

    fun setUpData(item: BookingSuccessItem) {
        val data = when {
            item.isTicketless -> {
                listOf(
                    EventBookingTicketlessAdapterItem(
                        item.eventName,
                        item.eventDate?.getFormattedDateTime(item.timeZone),
                        item.eventImageUrl,
                        item.venueName,
                        item.venueColor
                    ),
                    EventBookingSuccessDescriptionItem(
                        view.context.getString(R.string.explore_events_event_details_label),
                        view.context.getString(R.string.explore_events_confirm_modal_ticketless_supporting)
                    )
                )
            }
            item.guestCount > 0 && item.eventDate != null -> {
                listOf(
                    getOverviewItem(item),
                    EventGuestAdapterItem(
                        item.maxGuest,
                        item.guestCount,
                        item.eventId,
                        item.eventName,
                        item.eventType
                    ),
                    EventGuestListAdapterItem(
                        item.guestCount,
                        null,
                        item.eventName,
                        item.venueName,
                        item.eventDate,
                        item.timeZone
                    )
                )
            }
            else -> {
                listOf(getOverviewItem(item))
            }
        }

        executeWhenAvailable { view, _, _ -> view.setUpRecyclerView(data) }
    }

    private fun getOverviewItem(item: BookingSuccessItem): EventBookingSuccessAdapterItem {
        return EventBookingSuccessAdapterItem(
            item.bookingState,
            item.eventDate?.getFormattedDateTime(item.timeZone),
            item.eventName,
            item.eventImageUrl,
            item.venueName,
            item.venueColor,
            item.isPendingLotteryState,
            item.isInduction,
            item.isDigitalEvent
        )
    }

}
