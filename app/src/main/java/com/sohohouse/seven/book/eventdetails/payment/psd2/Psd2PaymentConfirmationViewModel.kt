package com.sohohouse.seven.book.eventdetails.payment.psd2

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.util.UnknownNull
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.LiveEvent
import com.sohohouse.seven.book.eventdetails.payment.repo.BookingStatusIsNotHeld
import com.sohohouse.seven.book.eventdetails.payment.repo.PaymentConfirmationRepo
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.coroutines.PollingException
import com.sohohouse.seven.common.views.BookingState
import com.sohohouse.seven.common.views.UserBookingState
import com.sohohouse.seven.network.core.isSuccessful
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class Psd2PaymentConfirmationViewModel @AssistedInject constructor(
    private val paymentConfirmationRepo: PaymentConfirmationRepo,
    @Assisted private val eventId: String,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager) {

    val result = MutableLiveData<String>()
    val error = LiveEvent<Unit>()

    fun pollBookingStatus() {
        viewModelScope.launch(viewModelContext) {
            try {
                val event = paymentConfirmationRepo.pollWhile(
                    eventId = eventId,
                    condition = BookingStatusIsNotHeld
                )
                if (event.isSuccessful()) {
                    val eventBooking = event.response.booking?.get(event.response.document)
                    when (eventBooking?.state) {
                        BookingState.CONFIRMED.name,
                        BookingState.UNCONFIRMED.name,
                        BookingState.HELD.name -> {
                            UserBookingState.getState(eventBooking)?.name?.let(result::postValue)
                        }
                        else -> {
                            error.postValue(Unit)
                        }
                    }
                } else {
                    error.postValue(Unit)
                }
            } catch (ex: PollingException) {
                result.postValue(UserBookingState.HELD.name)
            }

        }
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted eventId: String): Psd2PaymentConfirmationViewModel
    }

}