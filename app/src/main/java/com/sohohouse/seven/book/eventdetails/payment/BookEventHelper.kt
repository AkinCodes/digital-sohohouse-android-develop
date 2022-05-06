package com.sohohouse.seven.book.eventdetails.payment

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.views.BookingState
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.EventBooking
import com.sohohouse.seven.network.core.request.GetEventDetailsRequest
import com.sohohouse.seven.network.core.request.PatchEventBookingRequest
import com.sohohouse.seven.network.core.request.PatchInductionBookingRequest
import com.sohohouse.seven.network.core.request.PostEventBookingRequest
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BookEventHelper @Inject constructor(private val zipRequestsUtil: ZipRequestsUtil) {
    fun bookOrUpdateEvent(
        eventId: String,
        bookingId: String?,
        paymentId: String?,
        tickets: Int,
        onValue: (EventBooking) -> Unit,
        onError: (String) -> Unit,
        isInductionEvent: Boolean = false
    ): Single<Either<ServerError, EventBooking>> {
        val call = when {
            bookingId == null -> PostEventBookingRequest(tickets, eventId, paymentId)
            isInductionEvent -> PatchInductionBookingRequest(eventId, bookingId)
            else -> PatchEventBookingRequest(tickets, eventId, bookingId, paymentId)
        }
        return zipRequestsUtil.issueApiCall(call)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { either ->
                either.fold(
                    ifValue = {
                        if (it.state == BookingState.HELD.name || it.state == BookingState.PENDING.name) {
                            getUpdatedEventBooking(getUpdatedEvent(eventId, 0))
                        } else {
                            Single.just(either)
                        }
                    },
                    ifError = {
                        FirebaseCrashlytics.getInstance().log("${it}, eventId? $eventId")
                        Single.just(
                            Either.Error<ServerError>(
                                ServerError.BAD_REQUEST,
                                detail = (either as Either.Error).detail
                            )
                        )
                    },
                    ifEmpty = {
                        FirebaseCrashlytics.getInstance().log("Empty response, eventId? $eventId")
                        Single.just(Either.Empty())
                    }
                )
            }
            .map {
                when (it) {
                    is Either.Value -> {
                        val bookingInfo = it.value
                        when (bookingInfo.state) {
                            BookingState.CONFIRMED.name,
                            BookingState.UNCONFIRMED.name,
                            BookingState.HELD.name,
                            BookingState.PENDING.name -> {
                                onValue(bookingInfo)
                            }
                            BookingState.FAILED.name -> {
                                FirebaseCrashlytics.getInstance()
                                    .log("Booking failed, eventId? $eventId")
                                onError(it.value.failure?.code ?: "")
                            }
                            else -> {
                                FirebaseCrashlytics.getInstance()
                                    .log("Invalid address, eventId? $eventId")
                                return@map Either.Error(ServerError.INVALID_RESPONSE)
                            }
                        }
                    }
                    is Either.Error -> {
                        onError(it.detail)
                    }
                    else -> {
                    }
                }
                return@map it
            }
    }

    private fun getUpdatedEventBooking(eventResponse: Single<Either<ServerError, Event>>): Single<Either<ServerError, EventBooking>> {
        return eventResponse.flatMap { either ->
            either.fold(
                ifValue = { event ->
                    event.booking?.get(event.document)?.let {
                        Single.just(Either.Value(it))
                    } ?: Single.just(Either.Error(ServerError.INVALID_RESPONSE))
                },
                ifError = { Single.just(Either.Error(it)) },
                ifEmpty = { Single.just(Either.Empty()) })
        }
    }

    private fun getUpdatedEvent(
        eventId: String,
        timesTried: Int
    ): Single<Either<ServerError, Event>> {
        return zipRequestsUtil.issueApiCall(
            GetEventDetailsRequest(
                eventId = eventId,
                includeBookings = true
            )
        )
            .delaySubscription(2, TimeUnit.SECONDS)
            .flatMap {
                if (it is Either.Value && timesTried < 5) {
                    val bookingState = it.value.booking?.get(it.value.document)?.state
                    if (bookingState == BookingState.HELD.name || bookingState == BookingState.PENDING.name) {
                        return@flatMap getUpdatedEvent(eventId, timesTried + 1)
                    } else {
                        return@flatMap Single.just(it)
                    }
                } else {
                    return@flatMap Single.just(it)
                }
            }
    }
}
