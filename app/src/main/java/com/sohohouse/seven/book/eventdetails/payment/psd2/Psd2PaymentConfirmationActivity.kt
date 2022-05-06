package com.sohohouse.seven.book.eventdetails.payment.psd2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.book.eventdetails.payment.PaymentConfirmationActivity
import com.sohohouse.seven.common.securewebview.SecureWebViewListener
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.SecureWebView
import com.sohohouse.seven.databinding.Psd2ActivityBinding
import javax.inject.Inject

class Psd2PaymentConfirmationActivity : BaseMVVMActivity<Psd2PaymentConfirmationViewModel>(),
    SecureWebViewListener {

    val binding by viewBinding(Psd2ActivityBinding::bind)

    @Inject
    lateinit var factory: Psd2PaymentConfirmationViewModel.Factory

    override val viewModel: Psd2PaymentConfirmationViewModel by lazy {
        factory.create(intent.getStringExtra(EVENT_ID) ?: "")
    }

    override val viewModelClass: Class<Psd2PaymentConfirmationViewModel>
        get() = Psd2PaymentConfirmationViewModel::class.java

    override fun onLoadingStarted() {
        binding.theedsWebviewLoading.toggleSpinner(true)
    }

    override fun onLoadingFinished() {
        binding.theedsWebviewLoading.toggleSpinner(false)
    }

    override fun getWebView(): SecureWebView {
        return binding.threedsWebview
    }

    override fun onVisitedHistoryUpdated() {}

    override fun getContentLayout(): Int {
        return R.layout.psd2_activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.result.observe(this) {
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(PaymentConfirmationActivity.USER_BOOKING_STATE, it)
            })
            finish()
        }

        viewModel.error.observe(this) {
            CustomDialogFactory.createThemedAlertDialog(
                this,
                message = getString(R.string.error_general),
                positiveButtonText = getString(R.string.payment_fail_cta),
                positiveClickListener = { _, _ -> finish() },
                negativeClickListener = { _, _ -> finish() }
            ) { finish() }.show()
        }

        val htmlData = intent.getStringExtra(HTML_DATA)
        if (htmlData?.isNotEmpty() == true) getWebView().loadHtmlData(htmlData)

        getWebView().setup(listener = this)
        getWebView().webViewClient = webViewClient()


    }

    private fun webViewClient() = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            binding.theedsWebviewLoading.toggleSpinner(true)
            viewModel.pollBookingStatus()
            view.loadUrl(request.url.toString())
            return false
        }
    }

    companion object {
        fun newInstance(
            context: Context,
            htmlData: String,
            eventId: String
        ) = Intent(context, Psd2PaymentConfirmationActivity::class.java).apply {
            putExtra(HTML_DATA, htmlData)
            putExtra(EVENT_ID, eventId)
        }

        private const val HTML_DATA = "HTML_DATA"
        private const val EVENT_ID = "EVENT_ID"
    }
}
