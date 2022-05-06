package com.sohohouse.seven.common.views

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.webkit.WebView
import com.sohohouse.seven.common.securewebview.SecureWebViewClient
import com.sohohouse.seven.common.securewebview.SecureWebViewListener

class SecureWebView @JvmOverloads constructor(
    con: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.webViewStyle
) : WebView(con, attrs, defStyleAttr) {

    var secureWebViewClient: SecureWebViewClient? = null

    init {
        @SuppressLint("SetJavaScriptEnabled")
        //javascript could be a vector to exploit your applications
        settings.javaScriptEnabled = true

        //Should an attacker somehow find themselves in a position to inject script into a WebView, then they could exploit the opportunity to access local resources. This can be somewhat prevented by disabling local file system access. It is enabled by default. The Android WebSettings class can be used to disable local file system access via the public method setAllowFileAccess.
        //This restricts the WebView to loading local resources from file:///android_asset (assets) and file:///android_res (resources).
        settings.allowFileAccess = false

        //disable Geolocation API
        settings.setGeolocationEnabled(false)

        settings.allowFileAccessFromFileURLs = false

        settings.allowUniversalAccessFromFileURLs = false

        settings.allowContentAccess = false
    }

    fun setup(hostsWhitelist: List<String>? = null, listener: SecureWebViewListener? = null) {
        secureWebViewClient = SecureWebViewClient(  hostsWhitelist?.map { Uri.parse(it).host ?: "" }).also {
            it.listener = listener
        }
        webViewClient = secureWebViewClient!!
    }

    fun loadHtmlData(data: String) {
        loadData(data, "text/html; charset=utf-8", "UTF-8")
    }

    fun onDestroy() {
        secureWebViewClient?.listener = null
    }
}