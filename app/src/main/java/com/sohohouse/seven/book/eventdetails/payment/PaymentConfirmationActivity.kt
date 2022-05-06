package com.sohohouse.seven.book.eventdetails.payment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseViewControllerActivity
import com.sohohouse.seven.base.error.ErrorHelper
import com.sohohouse.seven.base.mvpimplementation.ActivityLifeCycleListener
import com.sohohouse.seven.book.eventdetails.payment.BookingPaymentActivity.Companion.SELECTED_PAYMENT_ITEM
import com.sohohouse.seven.book.eventdetails.payment.psd2.Psd2PaymentConfirmationActivity
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.replaceBraces
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.securewebview.SecureWebViewLifeCycleListener
import com.sohohouse.seven.common.utils.CurrencyUtils
import com.sohohouse.seven.common.views.*
import com.sohohouse.seven.databinding.ActivityPaymentConfirmationBinding
import com.sohohouse.seven.membership.ActiveMembershipInfoActivity
import com.sohohouse.seven.payment.CardPaymentItem
import com.sohohouse.seven.payment.PaymentCardStatus

class PaymentConfirmationActivity : BaseViewControllerActivity<PaymentConfirmationPresenter>(),
    PaymentConfirmationViewController {

    val binding by viewBinding(ActivityPaymentConfirmationBinding::bind)

    override fun createPresenter(): PaymentConfirmationPresenter {
        return App.appComponent.paymentConfirmationPresenter
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_payment_confirmation
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BOOKING_PAYMENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val paymentCardItem = data?.getSerializableExtra(SELECTED_PAYMENT_ITEM)
            if (paymentCardItem != null && paymentCardItem is CardPaymentItem) {
                presenter.cardSelected(paymentCardItem)
                showSelectedCard(paymentCardItem)
            }
        } else if (requestCode == PSD2_VERIFY && resultCode == Activity.RESULT_OK) {
            bookSuccess(
                intent.getIntExtra(PAYMENT_CONFIRMATION_TICKET_COUNT, 0),
                data?.getStringExtra(USER_BOOKING_STATE) ?: ""
            )
        }
    }

    override fun createLifeCycleListenerList(
        lifeCycleListenerList: MutableList<ActivityLifeCycleListener?>
    ): List<ActivityLifeCycleListener?> {
        lifeCycleListenerList.add(SecureWebViewLifeCycleListener(this))
        return super.createLifeCycleListenerList(lifeCycleListenerList)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        binding.paymentBackBtn.clicks { onBackPressed() }

        val price = intent.getIntExtra(PAYMENT_CONFIRMATION_PRICE, 0)
        val tickets = intent.getIntExtra(PAYMENT_CONFIRMATION_TICKET_COUNT, 0)
        val newTickets = intent.getIntExtra(PAYMENT_CONFIRMATION_NEW_TICKET_COUNT, 0)
        val currencyCode = intent.getStringExtra(PAYMENT_CONFIRMATION_CURRENCY)
        val eventId = intent.getStringExtra(PAYMENT_CONFIRMATION_EVENT_ID) ?: ""
        val bookingId = intent.getStringExtra(PAYMENT_CONFIRMATION_BOOKING_ID)
        val isJoinEvent = bookingId == null
        val eventType = intent.getStringExtra(PAYMENT_CONFIRMATION_EVENT_TYPE) ?: ""
        presenter.eventType = eventType

        val totalPrice = price * if (isJoinEvent) newTickets + 1 else newTickets
        currencyCode?.let {
            binding.paymentConfirmationCost.text = CurrencyUtils.getFormattedPrice(totalPrice, it)
        }


        binding.buyButton.clicks { presenter.buyTickets(eventId, bookingId, tickets) }
        binding.paymentItem.root.clicks { presenter.launchCardListActivity() }
        binding.paymentEmptyItem.root.clicks { presenter.launchCardListActivity() }

        when {
            EventType.get(eventType).isFitnessEvent() -> {
                binding.subscribe.visibility = View.VISIBLE
                binding.subscribe.setOnClickListener { presenter.onSubscribeClicked() }
            }
            EventType.get(eventType).isCinemaEvent() -> {
                binding.paymentTitle.setText(R.string.payment_confirm_deposit)
                binding.buyButton.setText(R.string.payment_confirm_deposit)
            }
        }
    }

    override fun onDataReady(card: CardPaymentItem) {
        showSelectedCard(card)
    }

    override fun showEmptyView() {
        binding.paymentEmptyItem.root.setVisible()
        binding.paymentItem.root.setGone()
    }

    override fun bookSuccess(tickets: Int, state: String) {
        val intent = Intent().apply {
            putExtra(PAYMENT_CONFIRMATION_TICKET_COUNT, tickets)
            putExtra(PAYMENT_CONFIRMATION_STATE, state)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun showFailureView(errorCode: String) {
        val messageRes = ErrorHelper.errorCodeMap[errorCode]
            ?: R.string.payment_fail_supporting
        CustomDialogFactory.createThemedAlertDialog(
            this,
            getString(R.string.payment_fail_header),
            getString(messageRes),
            getString(R.string.payment_fail_cta)
        ).show()
    }
    //endregion

    override val loadingView: LoadingView
        get() = binding.activityPaymentConfirmationLoadingView

    //endregion

    private fun showSelectedCard(card: CardPaymentItem) {
        binding.paymentEmptyItem.root.setGone()
        binding.paymentItem.root.setVisible()

        binding.buyButton.isEnabled = true
        binding.paymentItem.listPaymentImage.setImageResource(card.paymentCardType.resDrawable)
        binding.paymentItem.listPaymentImage.contentDescription =
            getString(card.paymentCardType.resAltString)

        binding.paymentItem.listPaymentNumber.text =
            getString(R.string.payment_card_number_label).replaceBraces(card.lastFour)

        if (card.status == PaymentCardStatus.ACTIVE && !card.isDefault) {
            binding.paymentItem.listPaymentType.setGone()
        } else if (card.isDefault) {
            binding.paymentItem.listPaymentType.text =
                getString(R.string.payment_methods_default_label)
            binding.paymentItem.listPaymentType.setVisible()
        }
        binding.paymentItem.listPaymentButton.setImageResource(R.drawable.forward)
    }

    override fun launchCardListActivity(id: String) {
        val intent = Intent(this, BookingPaymentActivity::class.java)
        if (id.isNotEmpty()) intent.putExtra(SELECTED_PAYMENT_ITEM, id)
        startActivityForResult(intent, BOOKING_PAYMENT_REQUEST_CODE)
    }

    //region Error
    override fun getErrorStateView(): ReloadableErrorStateView {
        return binding.errorState
    }

    override fun showGenericErrorDialog(errorCodes: Array<out String>) {
        val messageRes = ErrorHelper.errorCodeMap.get(errorCodes.firstOrNull())
            ?: R.string.payment_methods_error_supporting
        CustomDialogFactory.createThemedAlertDialog(
            this,
            getString(R.string.payment_methods_error_header),
            getString(messageRes),
            getString(R.string.payment_methods_ok_cta)
        ).show()
    }

    override fun showReloadableErrorState() {
        super.showReloadableErrorState()
        binding.buyButton.setGone()
    }

    override fun hideReloadableErrorState() {
        super.hideReloadableErrorState()
        binding.buyButton.setVisible()
    }

    override fun openActiveMembershipInfo() {

        val eventId = intent.getStringExtra(PAYMENT_CONFIRMATION_EVENT_ID)
        val eventName = intent.getStringExtra(PAYMENT_CONFIRMATION_EVENT_NAME)
        val eventType = intent.getStringExtra(PAYMENT_CONFIRMATION_EVENT_TYPE)
        startActivity(ActiveMembershipInfoActivity.getIntent(this, eventId, eventName, eventType))
    }

    override fun showPsd2Confirmation(transactionAuthHtml: String) {
        startActivityForResult(
            Psd2PaymentConfirmationActivity.newInstance(
                this,
                transactionAuthHtml,
                eventId = intent.getStringExtra(PAYMENT_CONFIRMATION_EVENT_ID) ?: ""
            ), PSD2_VERIFY
        )
    }

    //endregion


    companion object {
        const val PAYMENT_CONFIRMATION_REQUEST_CODE = 9876
        private const val PAYMENT_CONFIRMATION_PRICE = "PaymentConfirmationPrice"
        private const val PAYMENT_CONFIRMATION_CURRENCY = "PaymentConfirmationCurrency"
        const val PAYMENT_CONFIRMATION_TICKET_COUNT = "PaymentConfirmationTicketCount"
        private const val PAYMENT_CONFIRMATION_NEW_TICKET_COUNT =
            "PaymentConfirmationNewTicketCount"
        private const val PAYMENT_CONFIRMATION_BOOKING_ID = "PaymentConfirmationBookingId"
        private const val PAYMENT_CONFIRMATION_EVENT_ID = "PaymentConfirmationEventId"
        private const val PAYMENT_CONFIRMATION_EVENT_NAME = "PaymentConfirmationEventName"
        private const val PAYMENT_CONFIRMATION_EVENT_TYPE = "PaymentConfirmationEventType"
        const val PAYMENT_CONFIRMATION_STATE = "PaymentConfirmationState"
        const val USER_BOOKING_STATE = "USER_BOOKING_STATE"


        private const val BOOKING_PAYMENT_REQUEST_CODE = 2333
        private const val PSD2_VERIFY = 1333

        fun getIntent(
            context: Context,
            eventId: String,
            eventName: String,
            eventType: String,
            priceCents: Int,
            currency: String?,
            ticketCount: Int,
            newTickets: Int,
            bookingId: String?
        ): Intent {
            return Intent(context, PaymentConfirmationActivity::class.java).apply {
                putExtra(PAYMENT_CONFIRMATION_EVENT_ID, eventId)
                putExtra(PAYMENT_CONFIRMATION_EVENT_NAME, eventName)
                putExtra(PAYMENT_CONFIRMATION_PRICE, priceCents)
                putExtra(PAYMENT_CONFIRMATION_CURRENCY, currency)
                putExtra(PAYMENT_CONFIRMATION_TICKET_COUNT, ticketCount)
                putExtra(PAYMENT_CONFIRMATION_NEW_TICKET_COUNT, newTickets)
                putExtra(PAYMENT_CONFIRMATION_BOOKING_ID, bookingId)
                putExtra(PAYMENT_CONFIRMATION_EVENT_TYPE, eventType)
            }
        }
    }
}
