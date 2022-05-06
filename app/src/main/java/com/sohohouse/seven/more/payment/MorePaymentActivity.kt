package com.sohohouse.seven.more.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseViewControllerActivity
import com.sohohouse.seven.branding.ThemeManager
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.ActivityMorePaymentBinding
import com.sohohouse.seven.more.payment.threeds.AddPayment3dsActivity
import com.sohohouse.seven.network.core.models.Card
import com.sohohouse.seven.payment.*
import javax.inject.Inject

class MorePaymentActivity : BaseViewControllerActivity<MorePaymentPresenter>(),
    MorePaymentViewController, PaymentMethodListener, Injectable {

    private val binding by viewBinding(ActivityMorePaymentBinding::bind)

    @Inject
    lateinit var themeManager: ThemeManager

    private val adapter = PaymentAdapter(this)

    override fun setBrandingTheme() {
        setTheme(themeManager.lightTheme)
    }

    //region BaseViewControllerActivity
    override fun createPresenter(): MorePaymentPresenter {
        return App.appComponent.morePaymentPresenter
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_more_payment
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        with(binding.componentToolbar) {
            toolbarTitle.text = getString(R.string.payment_methods_title)
            toolbarBackBtn.clicks { onBackPressed() }
        }
        with(binding.emptyView) {
            closeButton.clicks { onBackPressed() }
            addButton.clicks { onAddPaymentSelected() }
        }
        with(binding.paymentRecyclerView) {
            layoutManager = LinearLayoutManager(this@MorePaymentActivity)
            adapter = this@MorePaymentActivity.adapter
        }
        binding.emptyView.morePaymentEmptySupporting.setText(presenter.getNoPaymentMethodsMessage())
    }

    override fun onBackPressed() {
        with(binding.overflowView) {
            if (isVisible())
                hideOverflowView()
        }
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PAYMENT_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> with(binding) {
                    presenter.refreshData()
                    emptyView.root.setGone()
                    paymentRecyclerView.setVisible()
                }
                BundleKeys.RESULT_ERROR -> {
                    showGenericErrorDialog()
                }
            }
        }
    }
    //endregion

    //region MorePaymentViewController
    override fun onDataReady(itemList: List<BasePaymentItem>) {
        adapter.submitList(itemList.toMutableList())
    }

    override fun showEmptyView() = with(binding) {
        emptyView.root.setVisible()
        paymentRecyclerView.setGone()
    }

    override fun defaultPaymentUpdated(card: Card) {
        val adapter = binding.paymentRecyclerView.adapter as PaymentAdapter
        adapter.updateDefaultPayment(card.id)
    }

    override fun paymentDeleted(id: String) {
        val adapter = binding.paymentRecyclerView.adapter as PaymentAdapter
        adapter.deletePayment(id)
        if (adapter.itemCount == 0) {
            showEmptyView()
        }
    }

    override fun showFailureView(errorCode: String) {
        CustomDialogFactory.createThemedAlertDialog(
            this,
            getString(R.string.payment_methods_error_header),
            getString(R.string.payment_methods_error_supporting),
            getString(R.string.payment_methods_ok_cta)
        )
            .show()
    }
    //endregion

    override val loadingView: LoadingView
        get() = binding.activityMorePaymentLoadingView

    //endregion

    //region PaymentMethodListener
    override fun onPaymentMethodSelected(model: CardPaymentItem) = with(binding) {
        val deleteButton =
            Pair(getString(R.string.payment_methods_delete_cta), View.OnClickListener {
                presenter.deletePaymentMethod(model.id)
            })
        if (!model.isDefault && model.status == PaymentCardStatus.ACTIVE) {
            val makeDefaultButton =
                Pair(getString(R.string.payment_methods_make_default_cta), View.OnClickListener {
                    presenter.setDefaultPaymentMethod(model.id)
                })
            overflowView.showOverflowView(makeDefaultButton, deleteButton)
        } else {
            overflowView.showOverflowView(deleteButton)
        }
    }

    override fun onAddPaymentSelected() {
        presenter.logAddPaymentClick()
        val intent = Intent(this, AddPayment3dsActivity::class.java)
        startActivityForResult(intent, ADD_PAYMENT_REQUEST_CODE)
    }
    //endregion

    //region Error
    override fun getErrorStateView(): ReloadableErrorStateView {
        return binding.errorState
    }
    //endregion

    companion object {
        private const val ADD_PAYMENT_REQUEST_CODE = 1033
    }
}
