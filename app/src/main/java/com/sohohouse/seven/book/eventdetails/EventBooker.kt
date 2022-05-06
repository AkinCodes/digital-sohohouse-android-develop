package com.sohohouse.seven.book.eventdetails

import android.annotation.SuppressLint
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.book.eventdetails.payment.BookEventHelper
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.EventBooking
import com.sohohouse.seven.network.core.request.DeleteBookingRequest
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Definition
 */
interface EventBooker {

    val bookEventHelper: BookEventHelper
    val zipRequestsUtil: ZipRequestsUtil

    fun deleteGuest(
        request: EventBookingRequest,
        onSuccess: (eventBooking: EventBooking) -> Unit,
        onError: (String) -> Unit,
        transformers: List<SingleTransformer<Either<ServerError, EventBooking>, Either<ServerError, EventBooking>>>
    ) {
        updateBooking(request, onSuccess, onError, transformers)
    }

    fun deleteBooking(
        request: EventBookingRequest,
        onSuccess: (either: Either<ServerError, Void>) -> Unit,
        onError: (throwable: Throwable) -> Unit,
        transformers: List<SingleTransformer<Either<ServerError, Void>, Either<ServerError, Void>>>
    )

    fun createBooking(view: EventDetailsViewController?, request: EventBookingRequest)

    fun updateBooking(
        request: EventBookingRequest,
        onSuccess: (eventBooking: EventBooking) -> Unit,
        onError: (String) -> Unit,
        transformers: List<SingleTransformer<Either<ServerError, EventBooking>, Either<ServerError, EventBooking>>>
    )
}

/**
 * Implementation
 */
class EventBookerImpl(
    override val bookEventHelper: BookEventHelper,
    override val zipRequestsUtil: ZipRequestsUtil
) : EventBooker {

    override fun createBooking(view: EventDetailsViewController?, request: EventBookingRequest) {
        view?.getPaymentMethod(
            eventId = request.eventId,
            eventName = request.eventName,
            eventType = request.eventType,
            priceCents = request.eventPrice,
            currency = request.eventPriceCurrency,
            ticketCount = request.numberOfTickets,
            newTickets = request.newTickets,
            bookingId = request.bookingId
        )
    }

    @SuppressLint("CheckResult")
    override fun updateBooking(
        request: EventBookingRequest,
        onSuccess: (eventBooking: EventBooking) -> Unit,
        onError: (String) -> Unit,
        transformers: List<SingleTransformer<Either<ServerError, EventBooking>, Either<ServerError, EventBooking>>>
    ) {
        bookEventHelper.bookOrUpdateEvent(
            eventId = request.eventId,
            bookingId = request.bookingId,
            paymentId = null,
            tickets = request.numberOfTickets,
            onValue = onSuccess,
            onError = onError
        )
            .apply { transformers.forEach { compose(it) } }
            .subscribe()
    }

    @SuppressLint("CheckResult")
    override fun deleteBooking(
        request: EventBookingRequest,
        onSuccess: (either: Either<ServerError, Void>) -> Unit,
        onError: (throwable: Throwable) -> Unit,
        transformers: List<SingleTransformer<Either<ServerError, Void>, Either<ServerError, Void>>>
    ) {
        val id = request.bookingId ?: return
        zipRequestsUtil.issueApiCall(DeleteBookingRequest(id))
            .apply { transformers.forEach { compose(it) } }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onSuccess(it) }, onError)
    }

}

