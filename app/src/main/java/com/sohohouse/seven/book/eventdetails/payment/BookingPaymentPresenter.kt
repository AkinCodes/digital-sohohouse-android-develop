package com.sohohouse.seven.book.eventdetails.payment

import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.network.core.models.Card
import com.sohohouse.seven.payment.CardPaymentItem
import com.sohohouse.seven.payment.PaymentCardStatus
import com.sohohouse.seven.payment.PaymentCardType
import javax.inject.Inject

class BookingPaymentPresenter @Inject constructor(
    zipRequestsUtil: ZipRequestsUtil,
    userManager: UserManager
) :
    BasePaymentPresenter<BookingPaymentViewController>(zipRequestsUtil, userManager) {
    companion object {
        private const val TAG = "MorePaymentPresenter"
    }

    override fun onAttach(
        view: BookingPaymentViewController,
        isFirstAttach: Boolean,
        isRecreated: Boolean
    ) {
        super.onAttach(view, isFirstAttach, isRecreated)
        // TODO: 2019-03-14  Add with the correct screen key - view.setScreenName(FirebaseEventTracking.Screens.PaymentMethods)
        if (isFirstAttach) {
            executeWhenAvailable { v, _, _ ->
                v.initLayout()
            }
            fetchData()
        }
    }

    override fun onDataFetched(value: List<Card>) {
        if (value.isEmpty()) {
            executeWhenAvailable { view, _, _ -> view.showEmptyView() }
        } else {
            val defaultCards =
                value.filter { it.isPrimary && it.status == PaymentCardStatus.ACTIVE.name }
            val defaultCard = if (defaultCards.isNotEmpty()) defaultCards[0] else null

            setupPaymentMethodItems(value.map {
                CardPaymentItem(
                    it.id,
                    PaymentCardType.valueOf(it.cardType),
                    it.lastFour,
                    it.isPrimary,
                    PaymentCardStatus.valueOf(it.status)
                )
            }, view::onDataReady)

            executeWhenAvailable { view, _, _ -> view.setSelectedItem(defaultCard?.id) }

        }
    }

    fun onPaymentMethodSelected(model: CardPaymentItem) {
        if (model.status == PaymentCardStatus.ACTIVE) {
            view.onMethodSelectedResult(model)
        } else {
            view.showFailureView()
        }
    }

}

