package com.sohohouse.seven.common.views.webview

import androidx.fragment.app.FragmentManager
import com.sohohouse.seven.common.apihelpers.SohoWebHelper

fun openWebView(
    fragmentManager: FragmentManager,
    kickoutType: SohoWebHelper.KickoutType
) {
    WebViewBottomSheetFragment.withKickoutType(
        type = kickoutType,
        showHeader = true
    )
        .show(fragmentManager, WebViewBottomSheetFragment.TAG)
}