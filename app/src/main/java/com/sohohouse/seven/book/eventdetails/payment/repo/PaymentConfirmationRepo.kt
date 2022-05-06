package com.sohohouse.seven.book.eventdetails.payment.repo

import com.sohohouse.seven.common.coroutines.PollingCondition
import com.sohohouse.seven.common.coroutines.polledSuspendCoroutine
import com.sohohouse.seven.common.utils.ZipRequestsUtil
import com.sohohouse.seven.common.views.BookingState
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.common.extensions.formatWithCommas
import com.sohohouse.seven.network.core.isFailure
import com.sohohouse.seven.network.core.isSuccessful
import com.sohohouse.seven.network.core.models.*
import com.sohohouse.seven.network.core.request.GetEventDetailsRequest
import com.sohohouse.seven.network.core.request.PostEventBookingRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.ObjectDocument
import java.lang.IllegalStateException
import kotlin.coroutines.suspendCoroutine


interface PaymentConfirmationRepo {

    suspend fun payForTickets(payForTickets: PayForTickets): ApiResponse<EventBooking>
    suspend fun pollWhile(
        eventId: String,
        condition: PollingCondition<Event>
    ): ApiResponse<Event>

    class Impl(private val sohoApiService: SohoApiService) : PaymentConfirmationRepo {

        override suspend fun payForTickets(payForTickets: PayForTickets): ApiResponse<EventBooking> {
            return if (payForTickets.bookingId.isEmpty()) {
                val document = createPostEventBookingDocument(payForTickets)
                sohoApiService.postEventBookingRequest(document)
            } else {
                val document = createPatchEventBookingDocument(payForTickets)
                return sohoApiService.patchEventBookingRequest(
                    payForTickets.bookingId, document
                )
            }
        }

        override suspend fun pollWhile(
            eventId: String,
            condition: PollingCondition<Event>
        ): ApiResponse<Event> {
            var includeResources = arrayOf(
                GetEventDetailsRequest.VENUE_INCLUDE_TYPE,
                GetEventDetailsRequest.FILM_INCLUDE_TYPE
            )
            includeResources = includeResources.plus(GetEventDetailsRequest.BOOKING_INCLUDE_TYPE)

            return polledSuspendCoroutine(
                maxTries = 5,
                delayTime = 2000
            ) {
                val event = sohoApiService.getEvent(eventId, includeResources)

                if (event.isSuccessful() && condition(event.response)) {
                    resumeWith(Result.success(event))
                    true
                } else if (event.isFailure()) {
                    resumeWith(Result.success(event))
                    true
                } else
                    false
            }
        }

        private fun createPatchEventBookingDocument(payForTickets: PayForTickets): ObjectDocument<Event> {
            val document = ObjectDocument<Event>()
            val event = Event()
            event.id = payForTickets.eventId
            document.set(event)
            return document
        }

        private fun createPostEventBookingDocument(payForTickets: PayForTickets): ObjectDocument<NewEventBooking> {
            val document = ObjectDocument<NewEventBooking>()
            val event = Event()
            event.id = payForTickets.eventId
            val guestsList = mutableListOf<Guests>()
            for (i in 1..payForTickets.ticketsCount) {
                guestsList.add(Guests(null, null, null))
            }
            val eventBooking = if (payForTickets.cardId.isEmpty()) {
                NewEventBooking(guests = guestsList, event = HasOne(event))
            } else {
                val paymentCard = Card()
                paymentCard.id = payForTickets.cardId
                NewEventBooking(
                    guests = guestsList,
                    event = HasOne(event),
                    paymentCard = HasOne(paymentCard)
                )
            }
            document.set(eventBooking)
            return document
        }

    }

}


object BookingStatusIsNotHeld : PollingCondition<Event> {
    override fun invoke(data: Event): Boolean {
        return BookingState.isPendingOrHeld(data.bookingState).not()
    }
}

object TransactionAuthHtmlIsNotNull : PollingCondition<Event> {
    override fun invoke(data: Event): Boolean {
        return data.booking?.get(data.document)?.transactionAuthHtml?.isNotEmpty() == true
    }
}

object TransactionAuthHtmlIsNotNullOrBookingNotHeld : PollingCondition<Event> {
    override fun invoke(data: Event): Boolean {
        return TransactionAuthHtmlIsNotNull(data) || BookingStatusIsNotHeld(data)
    }
}
