package com.sohohouse.seven.common.securewebview

import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.webkit.*
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.isStringEmpty
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.SecureWebView
import java.io.ByteArrayInputStream
import java.io.InputStream

interface SecureWebViewListener {
    fun onLoadingStarted()
    fun onLoadingFinished()
    fun getWebView(): SecureWebView
    fun destroyWebView() {
        getWebView().clearCache(true)
    }

    fun onVisitedHistoryUpdated()
}

class SecureWebViewClient(private val hostsWhitelist: List<String>?) : WebViewClient() {

    var listener: SecureWebViewListener? = null

    override fun shouldInterceptRequest(
        view: WebView,
        webResourceRequest: WebResourceRequest
    ): WebResourceResponse? {
        return if (isValidHost(webResourceRequest.url)) {
            super.shouldInterceptRequest(view, webResourceRequest)
        } else {
            getWebResourceResponseFromString()
        }
    }

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        showSslError(view.context, handler, error)
    }

    private fun getWebResourceResponseFromString(): WebResourceResponse {
        return getUtf8EncodedWebResourceResponse(ByteArrayInputStream("".toByteArray()))
    }

    private fun getUtf8EncodedWebResourceResponse(data: InputStream): WebResourceResponse {
        return WebResourceResponse("text/css", "UTF-8", data)
    }

    override fun shouldOverrideUrlLoading(
        view: WebView,
        webResourceRequest: WebResourceRequest
    ): Boolean {
        return !isValidHost(webResourceRequest.url)
    }

    private fun isValidHost(url: Uri): Boolean {
        if (hostsWhitelist.isNullOrEmpty()) return true

        if (!url.host.isStringEmpty()) {
            val host = url.host
            return hostsWhitelist.any { host?.contains(host, ignoreCase = true) == true }
        }
        return false
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        listener?.onLoadingStarted()
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        listener?.onLoadingFinished()
    }

    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        super.doUpdateVisitedHistory(view, url, isReload)
        listener?.onVisitedHistoryUpdated()
    }

    private fun showSslError(context: Context, handler: SslErrorHandler, error: SslError) {
        val message = when (error.primaryError) {
            SslError.SSL_UNTRUSTED -> R.string.web_ssl_certificate_error_not_trusted
            SslError.SSL_EXPIRED -> R.string.web_ssl_certificate_error_expired
            SslError.SSL_IDMISMATCH -> R.string.web_ssl_certificate_error_not_hostname_mismatch
            SslError.SSL_NOTYETVALID -> R.string.web_ssl_certificate_error_not_yet_valid
            else -> R.string.web_ssl_certificate_error_generic
        }
        CustomDialogFactory.createThemedAlertDialog(
            context = context,
            title = context.getString(R.string.web_ssl_certificate_error_title),
            message = context.getString(message),
            positiveButtonText = context.getString(R.string.web_ssl_certificate_error_continue_cta),
            negativeButtonText = context.getString(R.string.web_ssl_certificate_error_cancel_cta),
            positiveClickListener = DialogInterface.OnClickListener { _, _ -> handler.proceed() },
            negativeClickListener = DialogInterface.OnClickListener { _, _ -> handler.cancel() }
        ).show()
    }
}