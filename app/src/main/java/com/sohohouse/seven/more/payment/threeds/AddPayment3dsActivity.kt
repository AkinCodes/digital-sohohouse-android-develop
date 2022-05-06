package com.sohohouse.seven.more.payment.threeds

import android.app.Activity
import android.os.Bundle
import android.webkit.JavascriptInterface
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.BuildConfig
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseActivity
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.dagger.appComponent
import com.sohohouse.seven.common.securewebview.SecureWebViewListener
import com.sohohouse.seven.common.utils.BuildVariantConfig
import com.sohohouse.seven.common.views.SecureWebView
import com.sohohouse.seven.databinding.ActivityAddPayment3dsBinding
import com.sohohouse.seven.network.common.HeaderInterceptor

class AddPayment3dsActivity : BaseActivity(), SecureWebViewListener {

    private val binding by viewBinding(ActivityAddPayment3dsBinding::bind)

    override fun onLoadingStarted() {
        binding.theedsWebviewLoading.toggleSpinner(true)
    }

    override fun onLoadingFinished() {
        binding.theedsWebviewLoading.toggleSpinner(false)
    }

    override fun onVisitedHistoryUpdated() {}

    override fun getWebView(): SecureWebView {
        return binding.threedsWebview
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_add_payment_3ds
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initWebView()
    }

    private fun initToolbar() = with(binding) {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun initWebView() = with(binding) {
        threedsWebview.addJavascriptInterface(ThreeDsWebInterface(), JS_INTERFACE_NAME)
        threedsWebview.setup(listener = this@AddPayment3dsActivity)
        threedsWebview.loadUrl(URL, buildHeaders())
    }

    private fun buildHeaders() =
        mapOf(HeaderInterceptor.AUTHORIZATION_KEY to "${HeaderInterceptor.BEARER_IDENTIFIER_KEY} ${appComponent.userSessionManager.token}")

    @Suppress("unused")
    inner class ThreeDsWebInterface {

        @JavascriptInterface
        fun cardCreated() {
            setResult(Activity.RESULT_OK)
            finish()
        }

        @JavascriptInterface
        fun cardNotCreated() {
            setResult(BundleKeys.RESULT_ERROR)
            finish()
        }

    }

    companion object {
        private val HOST = if (BuildConfig.DEBUG) {
            BuildVariantConfig.STAGING_SOHO_WEB_HOSTNAME
        } else {
            BuildVariantConfig.SOHO_WEB_HOSTNAME
        }
        val URL = "$HOST/settings/payment/add-method?webview"

        const val JS_INTERFACE_NAME = "Android"
    }
}