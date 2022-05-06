package com.sohohouse.seven.book.eventdetails

import com.sohohouse.seven.base.error.ErrorDialogViewController
import com.sohohouse.seven.base.error.ErrorViewStateViewController
import com.sohohouse.seven.base.load.PullToRefreshViewController
import com.sohohouse.seven.book.eventdetails.bookingsuccess.BookingSuccessItem
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.common.views.RemindMeButtonStatus
import com.sohohouse.seven.common.views.RemindMeListener
import com.sohohouse.seven.common.views.eventdetaillist.BaseEventDetailsAdapterItem

interface EventDetailsViewController : PullToRefreshViewController, ErrorDialogViewController,
    ErrorViewStateViewController, RemindMeListener {
    fun showEventDetails(data: List<BaseEventDetailsAdapterItem>)
    fun onAddressClicked(address: String)
    fun showBookingStepper(config: StepperPresenter.Config)
    fun hideBookingStepper()
    fun showRemindMeView(status: RemindMeButtonStatus)
    fun hideRemindMeView()
    fun showBookingSuccess(bookingSuccessItem: BookingSuccessItem)
    fun showDeleteDialogue(newGuestCount: Int)
    fun getPaymentMethod(
        eventId: String,
        eventName: String,
        eventType: String,
        priceCents: Int,
        currency: String?,
        ticketCount: Int,
        newTickets: Int,
        bookingId: String? = null
    )

    fun showConfirmationButton(onConfirmed: () -> Unit)
    fun showBookingError()
    fun showBookingErrorWithMessage(message: String)
    fun showActiveMembershipInfo(eventId: String, eventName: String, eventType: String)
    fun setupScreenName(eventType: EventType)
    fun launchAddToCalendarIntent(name: String, address: String, startsAt: Long, endsAt: Long)
    fun setVideoUrl(url: String?)
}
