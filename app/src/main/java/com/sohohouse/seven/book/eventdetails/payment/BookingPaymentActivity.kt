package com.sohohouse.seven.book.eventdetails.payment

import android.app.Activity
import android.content.Intent
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseViewControllerActivity
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.ActivityBookingPaymentBinding
import com.sohohouse.seven.more.payment.threeds.AddPayment3dsActivity
import com.sohohouse.seven.payment.BasePaymentItem
import com.sohohouse.seven.payment.CardPaymentItem
import com.sohohouse.seven.payment.PaymentAdapter
import com.sohohouse.seven.payment.PaymentMethodListener

class BookingPaymentActivity : BaseViewControllerActivity<BookingPaymentPresenter>(),
    BookingPaymentViewController, PaymentMethodListener {

    companion object {
        private const val ADD_PAYMENT_REQUEST_CODE = 1035

        const val SELECTED_PAYMENT_ITEM = "SelectedPaymentItem"
    }

    private val binding by viewBinding(ActivityBookingPaymentBinding::bind)

    private val adapter = PaymentAdapter(this, true)

    //region BaseViewControllerActivity
    override fun createPresenter(): BookingPaymentPresenter {
        return App.appComponent.bookingPaymentPresenter
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_booking_payment
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PAYMENT_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    presenter.refreshData()
                    binding.emptyView.root.visibility = View.GONE
                    binding.paymentRecyclerView.visibility = View.VISIBLE
                }
                BundleKeys.RESULT_ERROR -> {
                    showGenericErrorDialog()
                }
            }
        }
    }

    override fun onCreated() {
        super.onCreated()
        with(binding) {
            paymentRecyclerView.layoutManager = LinearLayoutManager(this@BookingPaymentActivity)
            paymentRecyclerView.adapter = adapter
            emptyView.morePaymentEmptySupporting.setText(presenter.getNoPaymentMethodsMessage())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    //endregion

    //region MorePaymentViewController
    override fun onDataReady(itemList: List<BasePaymentItem>) {
        adapter.submitList(itemList.toMutableList())
    }

    override fun showEmptyView() {
        binding.emptyView.root.visibility = View.VISIBLE
        binding.paymentRecyclerView.visibility = View.GONE
    }

    override fun initLayout() {
        with(binding) {
            componentToolbar.toolbarTitle.text = getString(R.string.payment_methods_title)
            componentToolbar.toolbarBackBtn.clicks { onBackPressed() }
            emptyView.closeButton.clicks { onBackPressed() }
            emptyView.addButton.clicks { onAddPaymentSelected() }
        }
    }

    override fun setSelectedItem(cardId: String?) {
        val selectedCardId = intent?.getStringExtra(SELECTED_PAYMENT_ITEM)
        if (selectedCardId != null) {
            adapter.updateSelection(selectedCardId)
        } else if (cardId != null) {
            adapter.updateSelection(cardId)
        }
    }
    //endregion

    override val loadingView: LoadingView
        get() = binding.activityBookingPaymentLoadingView

    //endregion

    //region PaymentMethodListener
    override fun onPaymentMethodSelected(model: CardPaymentItem) {
        presenter.onPaymentMethodSelected(model)
    }

    override fun onMethodSelectedResult(model: CardPaymentItem) {
        val intent = Intent()
        intent.putExtra(SELECTED_PAYMENT_ITEM, model)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun showFailureView(errorCode: String) {
        CustomDialogFactory.createThemedAlertDialog(
            this,
            getString(R.string.payment_methods_error_header),
            getString(R.string.payment_methods_error_supporting),
            getString(R.string.payment_methods_ok_cta)
        ).show()
    }

    override fun onAddPaymentSelected() {
        val intent = Intent(this, AddPayment3dsActivity::class.java)
        startActivityForResult(intent, ADD_PAYMENT_REQUEST_CODE)
    }
    //endregion

    //region Error
    override fun getErrorStateView(): ReloadableErrorStateView {
        return binding.errorState
    }
    //endregion
}
