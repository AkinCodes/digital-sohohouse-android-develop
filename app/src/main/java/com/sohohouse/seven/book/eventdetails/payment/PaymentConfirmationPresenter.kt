package com.sohohouse.seven.book.eventdetails.payment

import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.book.eventdetails.payment.repo.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.coroutines.PollingException
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.views.*
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.isSuccessful
import com.sohohouse.seven.common.views.BookingState
import com.sohohouse.seven.common.views.BookingType
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.common.views.UserBookingState
import com.sohohouse.seven.network.core.models.Card
import com.sohohouse.seven.network.core.models.EventBooking
import com.sohohouse.seven.payment.CardPaymentItem
import com.sohohouse.seven.payment.PaymentCardStatus
import com.sohohouse.seven.payment.PaymentCardType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import javax.inject.Inject

class PaymentConfirmationPresenter @Inject constructor(
    zipRequestsUtil: ZipRequestsUtil,
    private val analyticsManager: AnalyticsManager,
    private val paymentConfirmationRepo: PaymentConfirmationRepo,
    userManager: UserManager,
    private val featureFlags: FeatureFlags
) :
    BasePaymentPresenter<PaymentConfirmationViewController>(zipRequestsUtil, userManager) {

    lateinit var eventType: String
    private lateinit var cardItem: CardPaymentItem

    private val presenterScope = CoroutineScope(Dispatchers.IO)

    companion object {
        private const val TAG = "PaymentConfirmationPresenter"
    }

    override fun onAttach(
        view: PaymentConfirmationViewController,
        isFirstAttach: Boolean,
        isRecreated: Boolean
    ) {
        super.onAttach(view, isFirstAttach, isRecreated)
        view.setScreenName(AnalyticsManager.Screens.PaymentConfirmation.name)
        if (isFirstAttach) {
            fetchData()
        }
    }

    override fun onDataFetched(value: List<Card>) {

        val defaultCards =
            value.filter { it.isPrimary && it.status == PaymentCardStatus.ACTIVE.name }

        if (defaultCards.isNotEmpty())
            executeWhenAvailable { v, _, _ ->
                val card = defaultCards[0]
                cardItem = CardPaymentItem(
                    card.id,
                    PaymentCardType.valueOf(card.cardType),
                    card.lastFour,
                    card.isPrimary,
                    PaymentCardStatus.valueOf(card.status)
                )
                v.onDataReady(cardItem)
            }
        else
            executeWhenAvailable { v, _, _ ->
                v.showEmptyView()
            }

    }

    fun buyTickets(eventId: String, bookingId: String?, tickets: Int) {
        if (EventType.get(eventType).isCinemaEvent()) {
            analyticsManager.logEventAction(AnalyticsManager.Action.ScreeningsConfirmDeposit)
        } else {
            analyticsManager.logEventAction(AnalyticsManager.Action.EventsConfirmPayment)
        }

        presenterScope.launch {
            executeWhenAvailable { view, _, _ -> view.showLoadingState() }

            val eventBooking = paymentConfirmationRepo.payForTickets(
                PayForTickets(
                    eventId = eventId,
                    ticketsCount = tickets,
                    cardId = cardItem.id,
                    bookingId = bookingId ?: ""
                )
            )

            if (eventBooking.isSuccessful()) {
                pollBookingStatus(eventId, tickets, eventBooking.response.venue.scaRequired)
            } else {
                executeWhenAvailable { view, _, _ ->
                    view.showFailureView()
                }
            }

            executeWhenAvailable { view, _, _ -> view.hideLoadingState() }
        }
    }

    private suspend fun pollBookingStatus(eventId: String, tickets: Int, scaEnabled: Boolean) {
        val event = try {
            paymentConfirmationRepo.pollWhile(
                eventId = eventId,
                condition = if (featureFlags.psd2Payments) {
                    TransactionAuthHtmlIsNotNullOrBookingNotHeld
                } else {
                    BookingStatusIsNotHeld
                }
            )

        } catch (ex: PollingException) {
            executeWhenAvailable { view, _, _ -> view.showFailureView() }
            return
        }

        if (event.isSuccessful()) {
            executeWhenAvailable { view, _, _ ->
                val eventBooking = event.response.booking?.get(event.response.document)
                if (eventBooking != null) {
                    if (featureFlags.psd2Payments && scaEnabled && eventBooking.transactionAuthHtml?.isNotEmpty() == true) {
                        view.showPsd2Confirmation(eventBooking.transactionAuthHtml ?: "")
                    } else {
                        val state = eventBooking.state?.let { BookingState.valueOf(it) }
                        if (!(state == BookingState.CONFIRMED || state == BookingState.UNCONFIRMED || state == BookingState.HELD)) {
                            view.showFailureView(eventBooking.failure?.code ?: "")
                        } else {
                            view.bookSuccess(
                                tickets,
                                UserBookingState.getState(
                                    BookingType.valueOf(eventBooking.bookingType),
                                    state
                                )?.name.orEmpty()
                            )
                        }
                    }
                } else {
                    view.showFailureView()
                }
            }
        } else {
            executeWhenAvailable { view, _, _ -> view.showFailureView() }
        }
    }

    fun launchCardListActivity() {
        view.launchCardListActivity(if (::cardItem.isInitialized) cardItem.id else "")
    }

    fun cardSelected(card: CardPaymentItem) {
        cardItem = card
    }

    fun onSubscribeClicked() {
        view.openActiveMembershipInfo()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenterScope.cancel()
    }

}